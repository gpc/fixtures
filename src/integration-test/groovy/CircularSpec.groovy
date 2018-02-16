import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.*

import test.circular.Buyer
import test.circular.BuyerAccount

@Integration
@Rollback
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
