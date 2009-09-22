package grails.fixture

class Fixture extends AbstractFixture {
    
    Fixture(applicationContext) {
        super()
        this.applicationContext = applicationContext
    }
    
    def createBuilder() {
        new FixtureBuilder(applicationContext, this.class.classLoader)
    }

    def resolveLocationPattern(String locationPattern) {
        applicationContext.getResources("file:fixtures/${locationPattern}.groovy")
    }
}