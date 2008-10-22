import grails.fixture.FixtureLoader

class FixturesGrailsPlugin {
    def version = 0.2
    def dependsOn = [:]

    // TODO Fill in these fields
    def author = "Luke Daley"
    def authorEmail = "ld@ldaley.com"
    def title = "fixtures"
    def description = '''\
fixtures
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/Fixtures+Plugin"

    def doWithSpring = {
        fixtureLoader(FixtureLoader) {
            classLoader = classLoader
        }
    }
   
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }
	                                      
    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }
	
    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
