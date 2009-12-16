package grails.plugin.fixtures.builder.processor

import grails.plugin.fixtures.exception.*

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.config.BeanPostProcessor
import org.codehaus.groovy.runtime.MetaClassHelper

import org.apache.commons.logging.LogFactory

class FixtureBeanPostProcessor implements BeanPostProcessor {
    
    def grailsApplication
    def messageSource
        
    def postProcessBeforeInitialization(Object bean, String beanName) {
        bean
    }

    def postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof FactoryBean) return bean
        
        def shouldSave = true
        def log = LogFactory.getLog(FixtureBeanPostProcessor.name + '.' + beanName)
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
                        
                        def propValue = bean."${p.name}"
                        if (propValue) {
                            log.debug("non owning, but has value of $propValue")
                            def otherSideName = p.otherSide.name
                            log.debug("attempting to set owning side inversely (prop name: ${otherSideName})")
                            def propValues = (propValue instanceof Collection) ? propValue : [propValue]
                            def otherSideIsMultiValue = (Collection.isAssignableFrom(p.otherSide.type))
                            log.debug("other side ${otherSideIsMultiValue ? 'IS' : 'IS NOT'} multi valued")
                            propValues.each {
                                if (otherSideIsMultiValue) {
                                    it."addTo${MetaClassHelper.capitalize(otherSideName)}"(bean)
                                } else {
                                    it."$otherSideName" = bean
                                }
                                assert it.save(flush: true)
                            }
                        }
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
        messageSource.getMessage(error, null)
    }

    def getDomainClass(clazz) {
        grailsApplication.getDomainClass(clazz.name)
    }
}