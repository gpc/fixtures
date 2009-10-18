package grails.plugin.fixtures

import org.springframework.beans.factory.config.BeanPostProcessor
import org.codehaus.groovy.runtime.MetaClassHelper

abstract class AbstractFixtureBeanPostProcessor implements BeanPostProcessor {
    
	static log = org.slf4j.LoggerFactory.getLogger(AbstractFixtureBeanPostProcessor)    

	def parentCtx
    
    abstract getDomainClass(clazz) 
    
    abstract getMessageSource()
    
    def postProcessAfterInitialization(Object bean, String beanName) {
        bean
    }
    
    def postProcessBeforeInitialization(Object bean, String beanName) {
        def shouldSave = true
        log.debug("processing bean $beanName of type ${bean.class.name}")
        
        def domainClass = getDomainClass(bean.class)
        log.debug("domainClass: $domainClass")
        
        domainClass?.persistentProperties?.each { p ->
            log.debug("inpecting property $p")
    
            if (p.association && p.referencedDomainClass != null) {
                log.debug("is a domain association")
                if (p.owningSide) {
                    log.debug("is owning side")
                    def value = bean."${p.name}"
                    if (value instanceof Collection) {
                        log.debug("is a collection")
                        bean."${p.name}" = []
                        value.each {
                            bean."addTo${MetaClassHelper.capitalize(p.name)}"(it)
                        }
                    }
                } else {
                    log.debug("is NOT owning side")
                    log.debug("bidirectional = ${p.bidirectional}, oneToOne = ${p.oneToOne}, manyToOne = ${p.manyToOne}")
                    if (p.bidirectional && (p.oneToOne || p.manyToOne)) {
                        log.info("not saving fixture bean $beanName")
                        shouldSave = false
                    }
                }
            } 
        }

        if (domainClass && shouldSave) {
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
