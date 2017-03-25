import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.*
import grails.validation.ValidationException
import test.Post
import org.springframework.beans.factory.BeanCreationException


@Integration
@Rollback
class InvalidFixtureBeanSpec extends Specification {

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
