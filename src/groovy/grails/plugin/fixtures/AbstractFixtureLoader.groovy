package grails.plugin.fixtures

abstract class AbstractFixtureLoader {
    
    private namedFixtures = [:]
    
    abstract createFixture()
    
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