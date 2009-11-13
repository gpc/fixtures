package grails.plugin.fixtures.exception

import org.springframework.core.io.Resource

class UnsatisfiedFixtureRequirementException extends FixtureException {

    String requirement
    Resource fixture
    String pattern
    
    UnsatisfiedFixtureRequirementException(requirement, fixture, pattern) {
        super("Fixture '$fixture.filename' requires the bean '$requirement' which was not satisfied (load pattern: $pattern)" as String)
        this.requirement = requirement
        this.fixture = fixture
        this.pattern = pattern
    }
    
}

