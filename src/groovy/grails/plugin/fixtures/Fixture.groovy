package grails.plugin.fixtures

class Fixture extends AbstractFixture {
    
    def grailsApplication
    
    Fixture(applicationContext) {
        super()
        this.applicationContext = applicationContext
        this.grailsApplication = applicationContext.getBean('grailsApplication')
    }
    
    def createBuilder() {
        new FixtureBuilder(applicationContext, this.class.classLoader)
    }

    def resolveLocationPattern(String locationPattern) {
        def prefix = (grailsApplication.warDeployed) ? "" : "file:"
        try {
            applicationContext.getResources("${prefix}fixtures/${locationPattern}.groovy")
        } catch(Exception e) {
            throw new UnknownFixtureException(locationPattern, e)
        }
    }
}