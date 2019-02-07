import grails.testing.mixin.integration.Integration
import spock.lang.*

@Integration
@Ignore
class LogSpec extends Specification {

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
