import grails.plugin.fixtures.FixtureLoader

class FixturesGrailsPlugin {
    def version = "1.0.RC1.SNAPSHOT"
    def dependsOn = [:]
    def grailsVersion = "1.1 > *"

    def author = "Luke Daley"
    def authorEmail = "ld@ldaley.com"
    def title = "Grails Fixtures Plugin"
    def description = "Load complex domain data via a simple DSL"
    def documentation = "http://alkemist.github.com/grails-fixtures/"
    def pluginExcludes = [
        "grails-app/domain/**",
        "grails-app/services/**",
        "grails-app/i18n/*",
        "fixtures"
    ]

    def doWithSpring = {
        fixtureLoader(FixtureLoader, application)
    }
}
