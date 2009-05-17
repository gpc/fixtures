package grails.fixture

abstract class AbstractFixtureLoader {
    
    abstract createFixture()
    
    def load(String[] fixtures) {
        createFixture().load(fixtures)
    }

    def load(Closure fixture) {
        createFixture().load(fixture)
    }
}