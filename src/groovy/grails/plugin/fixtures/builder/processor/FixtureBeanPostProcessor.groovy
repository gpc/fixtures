
/*
 * Copyright 2010 Grails Plugin Collective
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
				def owningSide = isOwningSide(p)
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
						if (p.bidirectional && (!owningSide || p.manyToOne)) {
							if (log.debugEnabled) {
								def reason = !owningSide ? 'owning side' : 'is many side'
								log.debug("setting this on $value ($reason)")
							}
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
				if (!owningSide && p.bidirectional && (p.oneToOne || p.manyToOne) && (value || !p.optional)) {
					log.info("not saving fixture bean $beanName")
					shouldSave = false
				}
				
			} 
		}

		if (domainClass && shouldSave) {
			bean.save(flush: true, failOnError: true)
		}
		bean
	}
	
	// Workaround for GRAILS-6714
	private isOwningSide(property) {
		def isOwning = property.owningSide
		if (isOwning || !property.inherited) {
			isOwning
		} else {
			def superDomainClass = getDomainClass(property.domainClass.clazz.superclass)
			isOwningSide(superDomainClass.getPropertyByName(property.name))
		}
	}

	def getErrorMessage(error) {
		messageSource.getMessage(error, null)
	}

	def getDomainClass(clazz) {
		grailsApplication.getDomainClass(clazz.name)
	}
}