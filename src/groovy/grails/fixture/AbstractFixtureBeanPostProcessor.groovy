package grails.fixture

import org.springframework.beans.factory.config.BeanPostProcessor
import org.codehaus.groovy.runtime.MetaClassHelper

abstract class AbstractFixtureBeanPostProcessor implements BeanPostProcessor {
    
    def parentCtx
    
    abstract getDomainClass(clazz) 
    
    abstract getMessageSource()
    
    def postProcessAfterInitialization(Object bean, String beanName) {
        bean
    }
    
    def postProcessBeforeInitialization(Object bean, String beanName) {
        def shouldSave = true

        def domainClass = getDomainClass(bean.class)
        domainClass.persistentProperties.each { p ->
            if (p.association && p.referencedDomainClass != null) {
                if (p.owningSide) {
                    def value = bean."${p.name}"
                    if (value instanceof Collection) {
                        bean."${p.name}" = []
                        value.each {
                            bean."addTo${MetaClassHelper.capitalize(p.name)}"(it)
                        }
                    }
                } else if (p.bidirectional && (p.oneToOne || p.manyToOne)) {
                    shouldSave = false
                }
            } 
        }

        if (shouldSave) {
            if (!bean.validate()) {
                def errorcodes = bean.errors.allErrors.collect { getErrorMessage(it) }
                throw new FixtureException("fixture bean '$beanName' has errors: ${errorcodes.join(', ')}")
            }
            if (!bean.save(flush: true)) {
                throw new FixtureException("failed to save fixture bean '$beanName'")
            }
        }
        bean
    }
    
    def getErrorMessage(error) {
        getMessageSource().getMessage(error, null)
    }
    
}
