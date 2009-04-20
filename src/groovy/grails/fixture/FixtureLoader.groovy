package grails.fixture
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader implements ApplicationContextAware {

    ApplicationContext applicationContext
        
    def load(String[] fixtures) {
        new Fixture(applicationContext).load(fixtures)
    }

    def load(Closure fixture) {
        new Fixture(applicationContext).load(fixture)
    }
}