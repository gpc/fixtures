import grails.plugin.spock.IntegrationSpec

import circular.Buyer
import circular.BuyerAccount

class CircularSpec extends IntegrationSpec {

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
