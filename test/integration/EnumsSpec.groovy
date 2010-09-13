import grails.plugin.spock.*

import enums.*
import spock.lang.*

class EnumsSpec extends IntegrationSpec {

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