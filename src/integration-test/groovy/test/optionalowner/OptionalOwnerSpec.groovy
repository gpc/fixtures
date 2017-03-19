package optionalowner

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.*
import test.optionalowner.OptionallyOwnedThing

@Integration
@Rollback
class OptionalOwnerSpec extends Specification {

	def fixtureLoader

	def "objects with optional owners will save when owner is optional"() {
		when:
		def thing = fixtureLoader.build { oot(OptionallyOwnedThing, name: "oot") }.oot
		then:
		thing.id != null
	}
}
