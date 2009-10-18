package grails.plugin.fixtures
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader extends AbstractFixtureLoader implements ApplicationContextAware {

    ApplicationContext applicationContext
    
    def createFixture() {
        new Fixture(applicationContext)
    }
}