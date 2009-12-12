package grails.plugin.fixtures

import grails.plugin.fixtures.builder.FixtureBuilder
import grails.plugin.fixtures.files.FixtureFileLoader
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.codehaus.groovy.grails.commons.GrailsApplication

class Fixture {
    
    final grailsApplication
    final applicationContext
    
    protected inners = []

    Fixture(GrailsApplication grailsApplication, inners = []) {
        this.grailsApplication = grailsApplication
        this.inners = inners
        this.applicationContext = grailsApplication.mainContext
    }
    
    def load(Closure f) {
        applicationContext = createBuilder().beans(f).createApplicationContext()
        this
    }

    def load(String[] patterns) {
        def fileLoader = new FixtureFileLoader(this, inners, createBuilder())
        applicationContext = fileLoader.load(*patterns)
        fileLoader.posts*.call()
        fileLoader.posts.clear()
        this
    }
    
    def propertyMissing(name) {
        getBean(name) ?: super.getProperty(name)
    }
        
    def getBean(name) {
        if (applicationContext.containsBean(name)) {
           applicationContext.getBean(name)
        } else {
            def bean
            inners.find { bean = it.getBean(name) }
            bean
        }
    }
    
    protected createBuilder() {
        new FixtureBuilder(this)
    }
    
}