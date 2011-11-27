import grails.plugin.spock.*
import spock.lang.*
import circular.*

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
