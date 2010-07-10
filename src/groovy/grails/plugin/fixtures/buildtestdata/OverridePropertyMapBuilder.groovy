package grails.plugin.fixtures.buildtestdata

import org.springframework.beans.factory.support.ManagedMap
import org.springframework.beans.factory.support.ManagedList

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