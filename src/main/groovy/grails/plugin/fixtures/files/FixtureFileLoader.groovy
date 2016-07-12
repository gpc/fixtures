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
package grails.plugin.fixtures.files

import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.files.shell.FixtureBuildingShell

import org.springframework.context.ApplicationContext
import org.springframework.util.Assert;

class FixtureFileLoader {

	protected loading = false

	final fixture
	final inners
	final builder

	final fileResolver
	final shell
	final fileEncoding

	def fixtureNameStack = []
	def currentLoadPattern

	final posts = []

	FixtureFileLoader(fixture, inners, builder) {
		this.fixture = fixture
		this.inners = inners
		this.builder = builder

		fileResolver = new FixtureFilePatternResolver(fixture.grailsApplication, fixture.applicationContext)
		shell = new FixtureBuildingShell(this)

		fileEncoding = fixture.grailsApplication.config.plugin.fixtures.file.encoding ?: "UTF-8"
	}

	ApplicationContext load(String[] patterns) {
		loading = true
		doLoad(patterns)
		loading = false
		builder.createApplicationContext()
	}

	void include(String[] includes) {
		Assert.state loading, "Can not include unless loading"

		doLoad(includes)
	}

	void addPost(Closure post) {
		posts << post
	}

	protected doLoad(String[] locationPatterns) {
		locationPatterns.each { locationPattern ->
			currentLoadPattern = locationPattern
			fileResolver.resolve(locationPattern).each { fixtureResource ->
				def fixtureName = fixtureResource.filename
				fixtureNameStack.push(fixtureName)
				try {
					shell.evaluate(fixtureResource.inputStream.newReader(fileEncoding), fixtureName)
				} catch (Throwable e) {
					throw new FixtureException("Failed to evaluate ${fixtureName} (pattern: '$locationPattern')", e)
				}
				fixtureNameStack.pop()
			}
			currentLoadPattern = null
		}
	}

	def getCurrentlyLoadingFixtureName() {
		fixtureNameStack?.last()
	}
}
