import grails.plugin.fixtures.FixtureLoader

class FixturesGrailsPlugin {
    def version = "1.0-M1"
    def dependsOn = [:]
    def grailsVersion = "1.1 > *"

    def author = "Luke Daley"
    def authorEmail = "ld@ldaley.com"
    def title = "fixtures"
    def description = "Allows loading of data sets defined with the BeanBuilder DSL"
    def documentation = "http://grails.org/Fixtures+Plugin"
    def pluginExcludes = [
        "grails-app/domain/**",
        "grails-app/services/**",
        "grails-app/i18n/*",
        "fixtures"
    ]

    def doWithSpring = {
        fixtureLoader(FixtureLoader)
    }
}
