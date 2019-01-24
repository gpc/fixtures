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

import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.runtime.MetaClassHelper
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.config.BeanPostProcessor

class FixtureBeanPostProcessor implements BeanPostProcessor {

	def grailsDomainClassMappingContext

	// If Hibernate plugin is not installed, this may be null.
	def sessionFactory

	def postProcessBeforeInitialization(bean, String beanName) {
		bean
	}

	def postProcessAfterInitialization(bean, String beanName) {
		if (bean instanceof FactoryBean) return bean

		def log = LogFactory.getLog(FixtureBeanPostProcessor.name + '.' + beanName)
		log.debug("processing bean $beanName of type ${bean.class.name}")

		PersistentEntity domainClass = getPersistentEntity(bean.class)
		log.debug("domainClass: $domainClass")
		def shouldSave = processDomainInstance(bean, log)

		if (domainClass && shouldSave) {
			bean.save(flush: true, failOnError: true)
		}
		else {
			log.info("not saving fixture bean $beanName")
		}

		bean
	}

	private boolean processDomainInstance(instance, log) {
		boolean shouldSave = true
		PersistentEntity entityClass = grailsDomainClassMappingContext.getPersistentEntity(instance.getClass().name)
		for (p in entityClass?.persistentProperties) {
			log.debug("inpecting property $p")
			shouldSave &= processDomainProperty(instance, p, log)
		}

		return shouldSave
	}

	private boolean processDomainProperty(instance, PersistentProperty p, log) {
		boolean shouldSave = true
		if (p instanceof Association && p.associatedEntity != null) {
			log.debug("is a domain association")
			log.debug("bidirectional = ${p.bidirectional}, oneToOne = ${p instanceof OneToOne}, manyToOne = ${p instanceof ManyToOne}, oneToMany = ${p instanceof OneToMany}")
			// boolean owningSide = p.isOwningSide()
			boolean owningSide = isOwningSide(p)
			log.debug("${owningSide ? 'IS' : 'IS NOT'} owning side")
			def value = instance."${p.name}"
			if (value) {
				if (p instanceof OneToMany || p instanceof ManyToMany) {
					log.debug("is to many")
					def associateType = p.type
					def associates = new ArrayList(value)
					value.clear()
					for (associate in associates) {
						if (associate instanceof Map) {
							associate = associateType.newInstance(associate)
						}
						log.debug("adding value $associate (${associate.getClass()})")
						instance."addTo${MetaClassHelper.capitalize(p.name)}"(associate)
						if (!owningSide) {
							log.debug("saving $associate (owning side)")
							associate.save(flush: true, failOnError: true)
							try {
								associate.refresh()
							} catch (UnsupportedOperationException e) {
								// not all Datastores support refresh, i.e. MongoDB with 'codec' mapping
							}
						}
					}
				} else {
					log.debug('is not to many')
					if (p.bidirectional && (!owningSide || p instanceof ManyToOne)) {
						if (log.debugEnabled) {
							String reason = !owningSide ? 'owning side' : 'is many side'
							log.debug("setting this on $value ($reason)")
						}
						String otherSideName = p.getReferencedPropertyName()
						// String otherSideName = p.getInverseSide().name
						if (p instanceof ManyToOne) {
							def addMethodName = "addTo${MetaClassHelper.capitalize(otherSideName)}"
							log.debug("Calling $addMethodName on $value")
							value."$addMethodName"(instance)
						} else {
							log.debug("Setting $otherSideName on $value")
							value."$otherSideName" = instance
						}
						value.save(flush: true, failOnError: true)
						try {
							value.refresh()
						} catch (UnsupportedOperationException e) {
							// not all Datastores support refresh, i.e. MongoDB with 'codec' mapping
						}
					}
				}
			}

			if (!owningSide && p.bidirectional && (p instanceof OneToOne || p instanceof ManyToOne) && (instance.ident() != null) && (value || !p.optional)) {
				shouldSave = false
			}
		}

		return shouldSave
	}

	// Workaround for GRAILS-6714 - still needed?
	private boolean isOwningSide(PersistentProperty property) {
		// property will be an instance of Association
		if (property.isOwningSide() || !property.isInherited()) {
			return true
		} else {
			PersistentEntity superDomainClass = property.owner.getParentEntity()
			return isOwningSide(superDomainClass.getPropertyByName(property.name))
		}
	}

	PersistentEntity getPersistentEntity(clazz) {
		grailsDomainClassMappingContext.getPersistentEntity(clazz.name)
	}
}
