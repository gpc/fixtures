package grails.fixture

import org.apache.commons.collections.map.IdentityMap
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.apache.commons.lang.StringUtils

class Fixture {

    def applicationContext
    def messageSource
    def grailsApplication
    def beans = [:]
    
    Fixture(applicationContext) {
        this.applicationContext = applicationContext
        grailsApplication = applicationContext.parent.getBean("grailsApplication")
        messageSource = applicationContext.parent.getBean("messageSource")
        applicationContext.beanDefinitionNames.each {
            beans[it] = applicationContext.getBean(it)
        }
        save()
    }
    
    private save() {
        def beansToSave = beans.clone()
        
        // Method to remove from the map by value
        def removeValue = { map, value ->
            map.find { k,v ->
                if (value.is(v)) {
                    map.remove(k)
                    true
                } else {
                    false
                }
            }
        }
        
        beans.each { name, bean ->
            def domainClass = grailsApplication.getDomainClass(bean.class.name)
            domainClass.persistentProperties.each { p ->
                if (p.association && p.owningSide) {
                    def value = bean."${p.name}"
                    if (value instanceof Set) {
                        bean."${p.name}" = []
                        value.each {
                            removeValue(beansToSave, it)
                            bean."addTo${StringUtils.capitalize(p.name)}"(it)
                        }
                    } else {
                        removeValue(beansToSave, value)
                    }
                }
            }
        }
        
        beansToSave.each { name, bean ->
            if (!bean.validate()) {
                def errorcodes = bean.errors.allErrors.collect { "'${messageSource?.getMessage(it, null)}'" }
                throw new IllegalStateException("fixture bean '$name' has errors: ${errorcodes.join(', ')}")
            }
            if (!bean.save(flush: true)) {
                throw new Error("failed to save fixture bean '$name'")
            }                        
        }
    }

}