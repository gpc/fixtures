import enums.UsesEnum
import enums.UsesEnumEnum
import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*

@TestMixin(IntegrationTestMixin)
class EnumsSpec extends Specification {

	def fixtureLoader

	def "basic inheritance"() {
		when:
		def f = fixtureLoader.build {
			u(UsesEnum, value: UsesEnumEnum.A)
		}
		then:
		f.u.value == UsesEnumEnum.A
	}
}
