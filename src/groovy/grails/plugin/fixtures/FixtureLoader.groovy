package grails.plugin.fixtures

import org.codehaus.groovy.grails.commons.GrailsApplication

class FixtureLoader  {

    protected grailsApplication
    protected namedFixtures = [:]
    
    FixtureLoader(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    def createFixture() {
        new Fixture(grailsApplication)
    }
    
    def load(String[] fixtures) {
        doLoad(*fixtures)
    }
    
    def load(Closure fixture) {
        doLoad(fixture)
    }

    def build(Closure fixture) {
        createFixture().build(fixture)
    }
    
    protected doLoad(Object[] fixtures) {
        createFixture().load(*fixtures)
    }
    
    def propertyMissing(String name) {
        if (!namedFixtures.containsKey(name)) namedFixtures[name] = doLoad()
        namedFixtures[name]
    }
}