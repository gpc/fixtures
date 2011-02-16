import grails.plugin.spock.*
import spock.lang.*
import grails.validation.ValidationException
import org.springframework.beans.factory.BeanCreationException

class InvalidFixtureBeanSpec extends IntegrationSpec {

	def fixtureLoader
	
	def "attempt to build invalid object"() {
		when:
		def f = fixtureLoader.load {
			i(Post) // post requires a text value
		}
		
		then:
		BeanCreationException e = thrown()
		e.cause instanceof ValidationException
	}
	

}