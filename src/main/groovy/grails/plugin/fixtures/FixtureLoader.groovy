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

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class FixtureLoader implements ApplicationContextAware {

	protected grailsApplication
	protected namedFixtures = [:]

	ApplicationContext applicationContext

	FixtureLoader(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication
	}

	def createFixture(Map params = [:]) {
		new Fixture(grailsApplication, applicationContext, params)
	}

	def load(String[] fixtures) {
		load(fixtures, [:])
	}

	def load(String fixtures, Map params) {
		load([fixtures] as String[], params)
	}

	def load(String[] fixtures, Map params) {
		createFixture(params).load(fixtures)
	}

	def load(Closure fixture) {
		createFixture().load(fixture)
	}

	def build(Closure fixture) {
		createFixture().build(fixture)
	}

	def propertyMissing(String name) {
		if (!namedFixtures.containsKey(name)) namedFixtures[name] = load(null as String[], null)
		namedFixtures[name]
	}
}
