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
	def version = "1.3"
	def grailsVersion = "1.3 > *"

	def author = "Grails Plugin Collective"
	def authorEmail = "grails.plugin.collective@gmail.com"
	def title = "Grails Fixtures Plugin"
	def description = "Load complex domain data via a simple DSL"
	def documentation = "http://gpc.github.com/grails-fixtures/"

	def license = "APACHE"
	def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPFIXTURES" ]
	def scm = [url: "https://github.com/gpc/grails-fixtures"]

	def pluginExcludes = [
		"grails-app/domain/**",
		"grails-app/services/**",
		"grails-app/i18n/*",
		"fixtures",
		"src/*/grails/plugin/fixtures/test/**"
	]

	def doWithSpring = {
		fixtureLoader(FixtureLoader, application)
	}

	/**
	 * For Platform Core if and when this plugin depends on it.
	 */
	def doWithConfigOptions = {
		'file.encoding' type: String, defaultValue: "UTF-8"
	}
}
