package test.optionalowner

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
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
