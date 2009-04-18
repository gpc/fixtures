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
        def binding = new Binding()
        binding.setVariable("fixture") {
            bb.beans(it)
        }
        def shell = new GroovyShell(classLoader, binding)
        
        fixtures.each {
            def fixture = new File("fixtures/${it}.groovy")
            if (fixture.exists()) {
                shell.evaluate(fixture)
            } else {
                throw new UnknownFixtureException(it)
            }
        }
        
        new Fixture(applicationContext: bb.createApplicationContext())
    }

    def load(Closure beans) {
        def bb = createBuilder()
        bb.beans(beans)
        new Fixture(applicationContext: bb.createApplicationContext())
    }
}