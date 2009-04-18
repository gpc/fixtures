package grails.fixture

class FixtureLoaderTests extends GroovyTestCase  {
    
    def fixtureLoader
    
    void setUp() {
        fixtureLoader = new FixtureLoader(classLoader: this.class.classLoader)
    }
    
    void testLoadFromFile() {
        def fixturesDir = new File("fixtures")
        if (!fixturesDir.exists()) fixturesDir.mkdir()
        
        def testFixture = new File(fixturesDir, "testFixture.groovy")
        if (testFixture.exists()) testFixture.delete()
        
        testFixture << """
        
package grails.fixture
fixture {
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
"""
        def fixture = fixtureLoader.load("testFixture")
        assertNotNull(fixture.guillaume)
        
        // clean up
        testFixture.delete()
        fixturesDir.delete()
    }

    void testLoadNonExistantFile() {
        shouldFail(UnknownFixtureException) {
            fixtureLoader.load("nonexistant")
        }
    }

    void testLoadFromClosure() {
        def fixture = fixtureLoader.load {
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
        assertNotNull(fixture.dierk)
    }
    
    void testGetNonExistantBean() {
        def fixture = fixtureLoader.load {
            dierk(Author) {
                name = "Dierk Koenig"
            }
        }
        shouldFail(UnknownFixtureBeanException) {
            fixture.blah
        }
    }
}