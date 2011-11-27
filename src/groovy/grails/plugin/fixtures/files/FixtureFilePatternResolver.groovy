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

import org.springframework.core.io.Resource
import grails.plugin.fixtures.exception.UnknownFixtureException
import org.springframework.web.context.support.ServletContextResourcePatternResolver

class FixtureFilePatternResolver {
	
	protected final grailsApplication
	protected final applicationContext
	protected final prefix
	
	private FixtureFilePatternResolver(grailsApplication, applicationContext) {
		this.grailsApplication = grailsApplication
		this.applicationContext = applicationContext
		this.prefix = (grailsApplication.warDeployed) ? "" : "file:"
	}

	Resource[] resolve(String locationPattern) {
		def resources
		def resolver = new ServletContextResourcePatternResolver(grailsApplication.mainContext.servletContext)
		
		try {
			resources = resolver.getResources("${prefix}fixtures/${locationPattern}.groovy")
		} catch (Exception e) {
			throw new UnknownFixtureException(locationPattern, e)
		}
		if (resources.size() == 0 || resources.any { !it.exists() }) {
			throw new UnknownFixtureException(locationPattern)
		} else {
			resources
		}
	}
}