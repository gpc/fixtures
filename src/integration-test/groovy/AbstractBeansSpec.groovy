import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*

@TestMixin(IntegrationTestMixin)
class AbstractBeansSpec extends Specification {

	def fixtureLoader

	def "attempt to build invalid object"() {
		when:
		def f = fixtureLoader.load {
			ab {
				text = "Something"
			}
			p(Post) {
				it.parent = ab
			}
		}

		then:
		f.p.text == "Something"
	}
}
