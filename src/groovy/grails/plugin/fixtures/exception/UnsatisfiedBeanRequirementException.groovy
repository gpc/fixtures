package grails.plugin.fixtures.exception

class UnsatisfiedBeanRequirementException extends UnsatisfiedFixtureRequirementException {

    UnsatisfiedBeanRequirementException(requirement, fixture, pattern) {
        super(
            "Fixture '$fixture.filename' requires the bean '$requirement' which was not satisfied (load pattern: $pattern)" as String,
            requirement,
            fixture,
            pattern
        )
    }
}