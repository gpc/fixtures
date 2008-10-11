package grails.fixture

class FixtureLoader {
    
    def classLoader
    
    def createBuilder() {
        new FixtureBuilder(classLoader)
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