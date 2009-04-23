package grails.fixture

import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.apache.commons.lang.StringUtils

class FixtureBuilderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {

    def messageSource
    def grailsApplication
    
    public FixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
        messageSource = parent.getBean("messageSource")
        grailsApplication = parent.getBean("grailsApplication")
    }
    
    protected GenericApplicationContext createApplicationContext(ApplicationContext parent) {
        def ctx = super.createApplicationContext(parent)
        ctx.beanFactory.addBeanPostProcessor(
            [
                postProcessBeforeInitialization: { Object bean, String beanName ->
                    bean
                },
                postProcessAfterInitialization: { Object bean, String beanName ->
                    
                    def shouldSave = true
                    
                    def domainClass = grailsApplication.getDomainClass(bean.class.name)
                    domainClass.persistentProperties.each { p ->
                        if (p.association && p.referencedDomainClass != null) {
                            if (p.owningSide) {
                                def value = bean."${p.name}"
                                if (value instanceof Collection) {
                                    bean."${p.name}" = []
                                    value.each {
                                        bean."addTo${StringUtils.capitalize(p.name)}"(it)
                                    }
                                }
                            } else if (p.bidirectional && p.oneToOne) {
                                shouldSave = false
                            }
                        } 
                    }
                    
                    println "will save $beanName: $shouldSave"
                    if (shouldSave) {
                        if (!bean.validate()) {
                            def errorcodes = bean.errors.allErrors.collect { "'${messageSource?.getMessage(it, null)}'" }
                            throw new IllegalStateException("fixture bean '$beanName' has errors: ${errorcodes.join(', ')}")
                        }
                        if (!bean.save(flush: true)) {
                            println "saving $name"
                            throw new Error("failed to save fixture bean '$beanName'")
                        }
                    }
                    bean
                    
                },
            ] as BeanPostProcessor
        )
        
        ctx
    }
}