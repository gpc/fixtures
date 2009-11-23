package grails.plugin.fixtures

import grails.plugin.fixtures.support.AbstractFixture
import grails.plugin.fixtures.builder.FixtureBuilder
import grails.plugin.fixtures.exception.UnknownFixtureException

class Fixture extends AbstractFixture {
    
    def grailsApplication
    
    Fixture(applicationContext, innerFixtures = []) {
        super(innerFixtures)
        this.applicationContext = applicationContext
        this.grailsApplication = applicationContext.getBean('grailsApplication')
    }
    
    def createBuilder() {
        new FixtureBuilder(this)
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