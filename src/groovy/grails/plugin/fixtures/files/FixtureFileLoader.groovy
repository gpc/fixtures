package grails.plugin.fixtures.files

import org.springframework.context.ApplicationContext
import grails.plugin.fixtures.files.shell.FixtureBuildingShell
import grails.plugin.fixtures.exception.*

class FixtureFileLoader {
    
    protected loading = false
    
    final fixture 
    final inners
    final builder
    
    final fileResolver
    final shell
    
    def fixtureNameStack = []
    def currentLoadPattern
    
    final posts = []
    
    FixtureFileLoader(fixture, inners, builder) {
        this.fixture = fixture
        this.inners = inners
        this.builder = builder
        
        this.fileResolver = new FixtureFilePatternResolver(fixture.grailsApplication)
        this.shell = new FixtureBuildingShell(this)
    }
    
    ApplicationContext load(String[] patterns) {
        loading = true
        doLoad(*patterns)
        loading = false
        builder.createApplicationContext()
    }
    
    void include(String[] includes) {
        if (!loading)
            throw new IllegalStateException("Can not include unless loading")
        
        doLoad(includes)
    }
    
    void addPost(Closure post) {
        posts << post
    }
    
    protected doLoad(String[] locationPatterns) {
        locationPatterns.each { locationPattern ->
            currentLoadPattern = locationPattern
            fileResolver.resolve(locationPattern).each { fixtureResource ->
                def fixtureName = fixtureResource.filename
                fixtureNameStack.push(fixtureName)
                try {
                    shell.evaluate(fixtureResource.inputStream, fixtureName)
                } catch (Throwable e) {
                    throw new FixtureException("Failed to evaluate ${fixtureName} (pattern: '$locationPattern')", e)
                }
                fixtureNameStack.pop()
            }
            currentLoadPattern = null
        }
    }

    def getCurrentlyLoadingFixtureName() {
        fixtureNameStack ? fixtureNameStack.last() : null
    }
}