import grails.fixture.FixtureLoader

class FixturesGrailsPlugin {
    def version = "0.7-RC3"
    def dependsOn = [:]

    def author = "Luke Daley"
    def authorEmail = "ld@ldaley.com"
    def title = "fixtures"
    def description = "Allows loading of data sets defined with the BeanBuilder DSL"
    def documentation = "http://grails.org/Fixtures+Plugin"

    def doWithSpring = {
        fixtureLoader(FixtureLoader)
    }
}
