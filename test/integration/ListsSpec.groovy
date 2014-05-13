import spock.lang.*
import grails.test.mixin.integration.IntegrationTestMixin
import grails.test.mixin.*
import collectiontypes.list.InList
import collectiontypes.list.ListUsing

@TestMixin(IntegrationTestMixin)
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
