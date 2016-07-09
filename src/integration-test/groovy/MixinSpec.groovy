import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*

@TestMixin(IntegrationTestMixin)
class MixinSpec extends Specification {

	def fixtureLoader

	def "load object with mixin"() {
		when:
		def f = fixtureLoader.load {
			m(MixinTarget) {
				name = "foo"
			}
		}

		then:
		f.m.name == "foo"
	}
}
