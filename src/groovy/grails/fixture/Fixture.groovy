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
        try {
            applicationContext.getResources("file:fixtures/${locationPattern}.groovy")
        } catch(Exception e) {
            throw new UnknownFixtureException(locationPattern, e)
        }
    }
}