package grails.plugin.fixtures.exception

import org.springframework.core.io.Resource

class UnsatisfiedFixtureRequirementException extends FixtureException {

    String requirement
    Resource fixture
    String pattern
    
    UnsatisfiedFixtureRequirementException(message, requirement, fixture, pattern) {
        super(message)
        this.requirement = requirement
        this.fixture = fixture
        this.pattern = pattern
    }
    
}

