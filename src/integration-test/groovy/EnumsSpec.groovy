import test.enums.UsesEnum
import test.enums.UsesEnumEnum
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.*

@Integration
@Rollback
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
