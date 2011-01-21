package optionalowner

import spock.lang.*
import grails.plugin.spock.*

class OptionalOwnerSpec extends IntegrationSpec {

	def fixtureLoader
	
	def "objects with optional owners will save when owner is optional"() {
		when:
		def thing = fixtureLoader.build { oot(OptionallyOwnedThing, name: "oot") }.oot
		then:
		thing.id != null
	}
	
	
}