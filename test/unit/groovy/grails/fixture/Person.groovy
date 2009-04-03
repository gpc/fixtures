package grails.fixture

public class Person {
	String name
	Person husband
	int age
	Set children
	Person parent
	Person sibling
	
	static hasMany = [children: Person]

	def validate() {
		true
	}

	def save(Map map) {
		true
	}
}