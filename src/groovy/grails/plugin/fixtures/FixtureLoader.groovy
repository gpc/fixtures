package grails.plugin.fixtures

import grails.plugin.fixtures.support.AbstractFixtureLoader

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader implements ApplicationContextAware {

    ApplicationContext applicationContext
    
    private namedFixtures = [:]
    
    def createFixture() {
        new Fixture(applicationContext)
    }
    
    def load(String[] fixtures) {
        doLoad(*fixtures)
    }
    
    def load(Closure fixture) {
        doLoad(fixture)
    }
    
    protected doLoad(Object[] fixtures) {
        createFixture().load(*fixtures)
    }
    
    def propertyMissing(String name) {
        if (!namedFixtures.containsKey(name)) namedFixtures[name] = doLoad()
        namedFixtures[name]
    }
}