import grails.plugin.spock.IntegrationSpec
import spock.lang.Ignore

@Ignore
class LogSpec extends IntegrationSpec {

	def fixtureLoader

	def "log available in the fixtureLoader"() {
		when:
		def f = fixtureLoader.load {
			assert log.name.startsWith("grails.app.fixture.inline.")
		}
		then:
		notThrown(Exception)
	}

	def "log available in file"() {
		when:
		def f = fixtureLoader.load("log/**/*")
		then:
		notThrown(Exception)
	}
}
