import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*
import m2m.A
import m2m.B
import m2m.C

@TestMixin(IntegrationTestMixin)
class ManyToManySpec extends Specification {

	def fixtureLoader

	def "attempt to build invalid object"() {
		when:
		def f = fixtureLoader.load {
			a(A)
			b(B)
			c(C, as: [a], bs: [b])
		}

		then:
		f.c.as.toList().first() == f.a
		f.c.bs.toList().first() == f.b
	}
}
