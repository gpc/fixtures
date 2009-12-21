package grails.plugin.fixtures

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class FixtureLoader implements ApplicationContextAware {

    protected grailsApplication
    protected namedFixtures = [:]
    
    ApplicationContext applicationContext
    
    FixtureLoader(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    def createFixture() {
        new Fixture(grailsApplication, applicationContext)
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