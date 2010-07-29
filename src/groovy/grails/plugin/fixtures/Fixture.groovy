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
package grails.plugin.fixtures

import grails.plugin.fixtures.builder.FixtureBuilder
import grails.plugin.fixtures.files.FixtureFileLoader
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import grails.plugin.fixtures.exception.UnknownFixtureBeanException

class Fixture {
	
	def grailsApplication
	def applicationContext
	
	protected inners = []

	Fixture(GrailsApplication grailsApplication, ApplicationContext applicationContext, inners = []) {
		this.grailsApplication = grailsApplication
		this.applicationContext = applicationContext
		this.inners = inners
	}
	
	def load(Closure f) {
		applicationContext = createBuilder().beans(f).createApplicationContext()
		this
	}

	def build(Closure f) {
		applicationContext = createBuilder().build(f).createApplicationContext()
		this
	}

	def load(String[] patterns) {
		def fileLoader = new FixtureFileLoader(this, inners, createBuilder())
		applicationContext = fileLoader.load(*patterns)
		fileLoader.posts*.call()
		fileLoader.posts.clear()
		this
	}
	
	def propertyMissing(name) {
		def bean = getBean(name)
		if (!bean) {
			throw new UnknownFixtureBeanException(name)
		}
		bean
	}
		
	def getBean(name) {
		if (applicationContext.containsBean(name)) {
		   applicationContext.getBean(name)
		} else {
			def bean
			inners.find { bean = it.getBean(name) }
			bean
		}
	}
	
	protected createBuilder() {
		new FixtureBuilder(this)
	}
	
}