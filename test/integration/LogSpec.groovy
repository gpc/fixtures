import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*

@TestMixin(IntegrationTestMixin)
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
