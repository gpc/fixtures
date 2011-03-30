import grails.plugin.spock.*
import collectiontypes.list.*
import spock.lang.*

class LogSpec extends IntegrationSpec {

	def fixtureLoader
	
	def "log available in the fixtureLoader"() {
		when:
		def f = fixtureLoader.load {
			log.info "Shouldn't fail"
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
