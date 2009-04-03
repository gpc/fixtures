package grails.fixture

import grails.test.GrailsUnitTestCase

class FixtureLoaderTests extends GrailsUnitTestCase  {
	def fixtureLoader = new FixtureLoader()
	def applicationContext

	def publications = {
		guillaume(Author) {
			name = "Guillaume Laforge"
			firstBook = ref("gina")
		}
		dierk(Author) {
			name = "Dierk Koenig"
		}
		gina(Book) {
			name = "Groovy In Action"
			authors = [guillaume, dierk]
		}
	}

	void testBasicBean() {
		fixtureLoader.load(publications)
	}

	void testBeanBuilder() {
		def bb = new grails.spring.BeanBuilder()
		bb.beans(publications)
		assertNotNull bb.createApplicationContext().gina
	}

	void testBiDirectional() {
		fixtureLoader.load(	{
			bob(Person) {
				name = "Bob"
				sibling = ref("bruce")
			}
			bruce(Person) {
				name = "bruce"
				sibling = ref("bob")
			}
		})
	}
}