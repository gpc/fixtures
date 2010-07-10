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
				log.debug("bidirectional = ${p.bidirectional}, oneToOne = ${p.oneToOne}, manyToOne = ${p.manyToOne}, oneToMany = ${p.oneToMany}")
				def owningSide = p.owningSide
				log.debug("${owningSide ? 'IS' : 'IS NOT'} owning side")
				def value = bean."${p.name}"
				if (value) {
					if (p.oneToMany || p.manyToMany) {
						log.debug("is to many")
						value = value.toList()
						bean."${p.name}".clear() 
						value.each {
							log.debug("adding value $it")
							bean."addTo${MetaClassHelper.capitalize(p.name)}"(it)
							if (!owningSide) {
								log.debug("saving $it (owning side)")
								it.save(flush: true)
								it.refresh()
							}
						}
					} else {
						log.debug('is not to many')
						if (p.bidirectional && !owningSide) {
							log.debug("setting this on $value (owning side)")
							def otherSideName = p.otherSide.name
							if (p.manyToOne) {
								def addMethodName = "addTo${MetaClassHelper.capitalize(otherSideName)}"
								log.debug("Calling $addMethodName on $value")
								value."$addMethodName"(bean)
							} else {
								log.debug("Setting $otherSideName on $value")
								value."$otherSideName" = bean
							}
							value.save(flush: true, failOnError: true)
							value.refresh()
						}
					}
				}
				if (!owningSide && p.bidirectional && (p.oneToOne || p.manyToOne)) {
					log.info("not saving fixture bean $beanName")
					shouldSave = false
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