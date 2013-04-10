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
package grails.plugin.fixtures.files.shell.handler

import grails.plugin.fixtures.Fixture
import grails.plugin.fixtures.exception.*

class LoadHandler extends FixtureBuildingShellHandler {

	final name = 'load'
	
	LoadHandler(fileLoader) {
		super(fileLoader)
	}
	
	def doCall(patternsOrClosures) {
		def inner = new Fixture(fixture.grailsApplication, fixture.applicationContext, fixture.params, inners.clone())
		
		if (patternsOrClosures instanceof Closure) {
			inner.load(patternsOrClosures)
		} else {
			try {
				inner.load(patternsOrClosures)
			} catch (Exception e) {
				throw new FixtureException("Failed to load inner fixture with $patternsOrClosures", e)
			}
		}
		
		inners << inner
	}

}