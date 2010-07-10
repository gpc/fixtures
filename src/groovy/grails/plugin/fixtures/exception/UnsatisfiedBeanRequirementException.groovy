package grails.plugin.fixtures.exception

class UnsatisfiedBeanRequirementException extends UnsatisfiedFixtureRequirementException {

	UnsatisfiedBeanRequirementException(requirement, fixture, pattern) {
		super(
			"Fixture '$fixture' requires the bean '$requirement' which was not satisfied (load pattern: $pattern)",
			requirement,
			fixture,
			pattern
		)
	}
}