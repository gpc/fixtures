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
package grails.plugin.fixtures.buildtestdata

class BuildTestDataBeanDefinitionTranslator {

	def grailsApplication

	protected static BEAN_DEFINING_NO_PROPERTIES_SIGNATURE = [Class]
	protected static BEAN_DEFINING_CLOSURE_ONLY_SIGNATURE = [Class, Closure]
	protected static BEAN_DEFINING_MAP_CONSTRUCTOR_ONLY_SIGNATURE = [Map, Class]
	protected static BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR = [Class, Map]
	protected static BEAN_DEFINING_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE = [Map, Class, Closure]
	protected static BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE = [Class, Map, Closure]

	def translate(builder, name, Object[] args) {
		def signature = args*.getClass()

		def isSignature = { targetSignature ->
			(signature.size() == targetSignature.size()) && (0..(targetSignature.size() - 1)).every { targetSignature[it].isAssignableFrom(signature[it]) }
		}

		if (isSignature(BEAN_DEFINING_NO_PROPERTIES_SIGNATURE)) {
			translate(builder, name, args[0], null, null)
		} else if (isSignature(BEAN_DEFINING_CLOSURE_ONLY_SIGNATURE)) {
			translate(builder, name, args[0], null, args[1])
		} else if (isSignature(BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR)) {
			translate(builder, name, args[0], args[1], null)
		} else if (isSignature(BEAN_DEFINING_MAP_CONSTRUCTOR_ONLY_SIGNATURE)) {
			translate(builder, name, args[1], args[0], null)
		} else if (isSignature(BEAN_DEFINING_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE)) {
			translate(builder, name, args[1], args[0], args[2])
		} else if (isSignature(BEAN_DEFINING_LITERAL_MAP_CONSTRUCTOR_AND_CLOSURE_SIGNATURE)) {
			translate(builder, name, args[0], args[1], args[2])
		} else {
			null
		}
	}

	protected translate(builder, name, domainClass, overridePropertiesMap, overridePropertiesClosure) {
		if (!grailsApplication.isDomainClass(domainClass)) {
			return null
		}

		def overridePropertiesBuilder = new OverridePropertyMapBuilder(
			builder,
			overridePropertiesMap, 
			overridePropertiesClosure
		)
		
		builder.with {
			noBuild {
				"$name"(BuildTestDataUtilisingFactoryBean) {
					delegate.domainClass = domainClass
					delegate.overrideProperties = overridePropertiesBuilder.build()
				}
			}
		}
	}
}
