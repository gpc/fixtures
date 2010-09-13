import grails.plugin.spock.*
import collectiontypes.list.*
import spock.lang.*

class ListsSpec extends IntegrationSpec {

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