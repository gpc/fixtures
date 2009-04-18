package grails.fixture
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader implements ApplicationContextAware {

    def classLoader
    ApplicationContext applicationContext

    def createBuilder() {
        new FixtureBuilder(applicationContext, classLoader)
    }

    def load(String[] fixtures) {
        def bb = createBuilder()
        fixtures.each {
            bb.loadBeans("file:fixtures/${it}.groovy")
        }
        new Fixture(applicationContext: bb.createApplicationContext())
    }

    def load(Closure beans) {
        def bb = createBuilder()
        bb.beans(beans)
        new Fixture(applicationContext: bb.createApplicationContext())
    }
}