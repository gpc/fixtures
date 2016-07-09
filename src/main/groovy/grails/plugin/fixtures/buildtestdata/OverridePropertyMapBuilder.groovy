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

import org.springframework.beans.factory.support.ManagedList
import org.springframework.beans.factory.support.ManagedMap

class OverridePropertyMapBuilder {

	def builder
	def map
	def closure

	OverridePropertyMapBuilder(builder, map, closure) {
		this.builder = builder
		this.map = new ManagedMap(map?.size() ?: 0)
		map.each { this.map[it.key] = manageIfNecessary(it.value) }
		this.closure = closure
	}

	def build() {
		if (closure) {
			closure.delegate = this
			closure.resolveStrategy = Closure.DELEGATE_FIRST
			closure.call()
		}
		map
	}

	def methodMissing(String name, args) {
		builder."$name"(*args)
	}

	void setProperty(String name, value) {
		map[name] = manageIfNecessary(value)
	}

	def propertyMissing(String name) {
		builder."$name"
	}

	protected manageIfNecessary(value) {
		if (value instanceof Map) {
			def managedMap = new ManagedMap(value.size())
			managedMap.putAll(value)
			value = managedMap
		} else if (value instanceof List) {
			def managedList = new ManagedList(value.size())
			managedList.addAll(value)
			value = managedList
		}

		return value
	}
}