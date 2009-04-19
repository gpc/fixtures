package grails.fixture
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.apache.commons.lang.StringUtils
import grails.spring.BeanBuilder

class FixtureLoader implements ApplicationContextAware {

    def classLoader
    def grailsApplication
    ApplicationContext applicationContext

    def createBuilder() {
        new BeanBuilder(applicationContext, classLoader)
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
        
        new Fixture(bb.createApplicationContext())
    }

    def load(Closure fixture) {
        def bb = createBuilder()
        bb.beans(beans)
        new Fixture(bb.createApplicationContext())
    }
}