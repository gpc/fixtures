package grails.fixture
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class FixtureLoader implements ApplicationContextAware {
    
    def classLoader
    ApplicationContext applicationContext
    
    def createBuilder() {
        new FixtureBuilder(applicationContext, classLoader)
    }
    
    void load(String[] fixtures) {
        def bb = createBuilder()
        fixtures.each {
            bb.loadBeans("/fixtures/${it}.groovy")
        }
        bb.createApplicationContext()
    }
    
    void load(Closure beans) {
        def bb = createBuilder()
        bb.beans(beans)
        bb.createApplicationContext()
    }
}