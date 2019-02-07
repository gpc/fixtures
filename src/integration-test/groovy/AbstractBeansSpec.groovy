import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

import test.Post

@Integration
@Rollback
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
