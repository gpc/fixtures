import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.*
import test.m2m.A
import test.m2m.B

@Integration
@Rollback
class MapSpec extends Specification {

	def fixtureLoader

	def "fixture can serialise its output as a map"() {
		given: 'a fixture'
		def f = fixtureLoader.load {
			a(A)
			b(B)
		}

		when: 'get value as a map'
			def map = f.toMap()

		then:
			map.size() == 2
			map.a instanceof A
			map.b instanceof B
	}
}
