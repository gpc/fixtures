package grails.fixture

public class Author {
	String name
	Set books
	Book firstBook

	def validate() {
		true
	}

	def save(Map map) {
		true
	}
}