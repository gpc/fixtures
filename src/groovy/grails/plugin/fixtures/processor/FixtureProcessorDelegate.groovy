package grails.plugin.fixtures.processor
 
class FixtureProcessorDelegate {

    final fixture
    
    FixtureProcessorDelegate(fixture) {
        this.fixture = fixture
    }

    def propertyMissing(name) {
        fixture.getBean(name) ?: super.getProperty(name)
    }

}