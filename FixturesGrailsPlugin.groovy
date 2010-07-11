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
import grails.plugin.fixtures.FixtureLoader

class FixturesGrailsPlugin {
	def version = "1.0-SNAPSHOT"
	def dependsOn = [:]
	def grailsVersion = "1.1 > *"

	def author = "Luke Daley"
	def authorEmail = "ld@ldaley.com"
	def title = "Grails Fixtures Plugin"
	def description = "Load complex domain data via a simple DSL"
	def documentation = "http://alkemist.github.com/grails-fixtures/"
	def pluginExcludes = [
		"grails-app/domain/**",
		"grails-app/services/**",
		"grails-app/i18n/*",
		"fixtures"
	]

	def doWithSpring = {
		fixtureLoader(FixtureLoader, application)
	}
}
