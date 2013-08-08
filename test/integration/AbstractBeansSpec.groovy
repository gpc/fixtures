import grails.plugin.spock.IntegrationSpec

class AbstractBeansSpec extends IntegrationSpec {

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
