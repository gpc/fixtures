import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*

import circular.Buyer
import circular.BuyerAccount

@TestMixin(IntegrationTestMixin)
class CircularSpec extends Specification {

	def fixtureLoader

	def "load circular"() {
		when:
		def f = fixtureLoader.load {
			testBuyerAccount(BuyerAccount, balance: 5.0)
			testBuyer(Buyer, fullName: "Test user", account: testBuyerAccount)
		}

		then:
		f.testBuyer.fullName == "Test user"
	}
}
