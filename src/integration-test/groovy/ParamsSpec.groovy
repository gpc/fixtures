import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.*

@Integration
@Rollback
class ParamsSpec extends Specification {

	def fixtureLoader

	def "params not provided"() {
		when:
		def f = fixtureLoader.load("params/basic")

		then:
		f.u.name == 'default name'
		f.v.name == 'name'
	}

	def "params provided"() {
		when:
		def f = fixtureLoader.load("params/basic", [name: "Uncle Sam"])

		then:
		f.u.name == "Uncle Sam"
		f.v.name == "Uncle Sam"
	}

	def "params passed over include boundary"() {
		expect :
		fixtureLoader.load("params/includer", [name: "Uncle Sam"]).u.name == "Uncle Sam"

		and:
		fixtureLoader.load("params/includer").u.name == "default name"
	}
}
