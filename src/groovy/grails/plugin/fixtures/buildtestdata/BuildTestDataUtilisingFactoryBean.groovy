package grails.plugin.fixtures.buildtestdata

import org.springframework.beans.factory.FactoryBean

class BuildTestDataUtilisingFactoryBean implements FactoryBean {

	boolean singleton = true

	def domainClass
	def overrideProperties
	
	def getObject() {
		domainClass.buildWithoutSave(overrideProperties)
	}

	Class getObjectType() {
		domainClass
	}

}