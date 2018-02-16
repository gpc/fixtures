import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.*
import test.m2m.A
import test.m2m.B
import test.m2m.C

@Integration
@Rollback
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
