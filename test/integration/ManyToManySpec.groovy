import grails.plugin.spock.*
import spock.lang.*
import m2m.*

class ManyToManySpec extends IntegrationSpec {

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
