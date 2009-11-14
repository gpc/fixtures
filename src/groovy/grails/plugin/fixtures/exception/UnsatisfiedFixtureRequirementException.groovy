package grails.plugin.fixtures.exception

class UnsatisfiedFixtureRequirementException extends FixtureException {

    String requirement
    String fixture
    String pattern
    
    UnsatisfiedFixtureRequirementException(message, requirement, fixture, pattern) {
        super(message)
        this.requirement = requirement
        this.fixture = fixture
        this.pattern = pattern
    }
    
}

