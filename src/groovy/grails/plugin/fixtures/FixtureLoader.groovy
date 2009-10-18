package grails.plugin.fixtures

import grails.plugin.fixtures.support.AbstractFixtureLoader

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader extends AbstractFixtureLoader implements ApplicationContextAware {

    ApplicationContext applicationContext
    
    def createFixture() {
        new Fixture(applicationContext)
    }
}