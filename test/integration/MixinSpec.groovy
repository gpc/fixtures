import grails.plugin.spock.IntegrationSpec

class MixinSpec extends IntegrationSpec {

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
