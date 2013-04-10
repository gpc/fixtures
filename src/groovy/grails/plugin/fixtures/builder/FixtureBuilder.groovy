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
package grails.plugin.fixtures.builder

import grails.plugin.fixtures.Fixture
import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.builder.processor.*

import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.config.RuntimeBeanReference

import grails.spring.BeanBuilder

import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

import grails.plugin.fixtures.buildtestdata.*
import org.springframework.beans.factory.BeanIsAbstractException

class FixtureBuilder extends BeanBuilder {
		
	def build = false // controls whether build-test-data is used

	protected fixture
	
	protected buildTestDataTranslator
	protected buildTestDataPluginInstalled
	
	protected definining = false // are we in the middle of a call to beans() ?
	
	FixtureBuilder(Fixture fixture) {
		super(fixture.applicationContext, fixture.class.classLoader)
		this.fixture = fixture
		this.buildTestDataTranslator = new BuildTestDataBeanDefinitionTranslator(grailsApplication: fixture.grailsApplication)
		registerPostProcessors()
		lookForBuildTestDataPlugin()
	}

	protected lookForBuildTestDataPlugin() {
		buildTestDataPluginInstalled = PluginManagerHolder.pluginManager.hasGrailsPlugin('build-test-data')
	}
	
	protected registerPostProcessors() {
		beans {
			autoAutoWirer(FixtureBeanAutoAutowireConfigurer)
			fixtureBeanPostProcessor(FixtureBeanPostProcessor)
		}
	}

    Map getParams() {
        fixture.params
    }

	def getProperty(String name) {
		def parentCtx = getParentCtx()
		if (parentCtx?.containsBean(name)) {
			parentCtx.getBean(name)
		} else {
			try {
				def property = super.getProperty(name)
				property
			} catch (MissingPropertyException e) {
				def bean = bean(name)
				if (!bean) throw e
				bean
			}
		}
	}
	
	def build(Closure definitions) {
		def previousBuild = this.build
		build = true
		beans(definitions)
		build = previousBuild
		this
	}
	
	def noBuild(Closure definitions) {
		def previousBuild = this.build
		build = false
		beans(definitions)
		build = previousBuild
		this
	}
	
	def bean(String name) {
		def bean = fixture.getBean(name)
		if (!bean) {
			throw new IllegalArgumentException("Fixture does not have bean '$name'")
		} 
		bean
	}

	BeanBuilder beans(Closure definition) {
		assertBuildTestDataPluginInstalledIfNeeded()
		if (!definining) {
			definining = true
			super.beans(definition)
			definining = false
		} else {
			definition.delegate = this
			definition.call()
		}
		this
	}
	
	def invokeMethod(String name, args) {
		def mm = this.metaClass.getMetaMethod(name, *args)
		if (mm) {
			mm.invoke(this, *args)
		} else {
			translateToBuild(name, *args) ?: super.invokeMethod(name, args)
		}
	}	 
	
	protected translateToBuild(String name, Object[] args) {
		isBuildTestDataActive() ? buildTestDataTranslator.translate(this, name, *args) : null
	}
	
	protected boolean isBuildTestDataActive() {
		this.build
	}
	
	protected assertBuildTestDataPluginInstalledIfNeeded() {
		if (isBuildTestDataActive() && !buildTestDataPluginInstalled) {
			throw new FixtureException("build feature is unavailable as build-test-data plugin is not installed")
		}
	}
	
	def ApplicationContext createApplicationContext() {
		def ctx = super.createApplicationContext()
		def grailsApplication = ctx.getBean("grailsApplication")
		
		for (name in ctx.beanDefinitionNames) {
			try {
				def bean = ctx.getBean(name)
				if (grailsApplication.isDomainClass(bean.class)) {
					if (bean.ident() != null) {
						bean.refresh()
					}
				}
			} catch (BeanIsAbstractException e) {
				// template bean
			} catch (Exception e) {
				throw new FixtureException("Error refresh()ing bean '$name'", e)
			}
		}
		
		ctx
	} 
}
