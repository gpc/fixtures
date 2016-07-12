import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*
import m2m.A
import m2m.B

@TestMixin(IntegrationTestMixin)
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
