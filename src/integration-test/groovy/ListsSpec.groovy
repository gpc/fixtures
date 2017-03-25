import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.*
import test.list.InList
import test.list.ListUsing

@Integration
@Rollback
class ListsSpec extends Specification {

	def fixtureLoader

	def "build using lists"() {
		when:
		def f = fixtureLoader.load {
			i(ListUsing, name: "i")
			j(InList, listUsing: i)
		}
		then:
		f.j.listUsing.name == "i"
		f.i.name == "i"
		f.i.inLists.size() == 1
	}
}
