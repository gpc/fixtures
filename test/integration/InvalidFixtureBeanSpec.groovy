import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*
import grails.validation.ValidationException

import org.springframework.beans.factory.BeanCreationException


@TestMixin(IntegrationTestMixin)
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
