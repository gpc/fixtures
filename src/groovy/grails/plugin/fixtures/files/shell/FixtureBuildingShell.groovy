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
package grails.plugin.fixtures.files.shell

import grails.plugin.fixtures.files.shell.handler.BuildHandler
import grails.plugin.fixtures.files.shell.handler.FixtureHandler
import grails.plugin.fixtures.files.shell.handler.IncludeHandler
import grails.plugin.fixtures.files.shell.handler.LoadHandler
import grails.plugin.fixtures.files.shell.handler.PostHandler
import grails.plugin.fixtures.files.shell.handler.PreHandler
import grails.plugin.fixtures.files.shell.handler.RequireBeansHandler
import grails.plugin.fixtures.files.shell.handler.RequireDefinitionsHandler
import grails.plugin.fixtures.files.shell.handler.RequireHandler

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.Resource

class FixtureBuildingShell extends GroovyShell {

	private static final Log log = LogFactory.getLog(FixtureBuildingShell)
	private static final String LOG_PREFIX = 'grails.app.fixtures'

	// Assuming that fixtures are stored in a dir named 'fixtures', see FixtureFilePatternResolver
	private static final NAME_EXTRACTION_PATTERN = ~/(fixtures\/)+(.*).groovy$/

	static handlers = [
		FixtureHandler, RequireHandler, RequireDefinitionsHandler,
		RequireBeansHandler, PreHandler, PostHandler, IncludeHandler,
		LoadHandler, BuildHandler
	]

	FixtureBuildingShell(fileLoader) {
		super(fileLoader.fixture.grailsApplication.classLoader)
		handlers*.newInstance(fileLoader)*.register(this)
		setVariable("params", fileLoader.fixture.params)
	}

	def evaluate(Resource resource, String fileName) {
		addLogToBindings(resource)
		evaluate(resource.URL.newReader(), fileName)
	}

	protected addLogToBindings(Resource resource) {
		setVariable('log', LogFactory.getLog(LOG_PREFIX + getFixturePathName(resource)))
	}

	protected getFixturePathName(Resource resource) {
		resource.URL.toString().find(NAME_EXTRACTION_PATTERN) { exp, fix, path -> path }?.replaceAll('/','.') ?: 'anonymous'
	}
}
