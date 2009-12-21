package grails.plugin.fixtures.files

import org.springframework.core.io.Resource
import grails.plugin.fixtures.exception.UnknownFixtureException

class FixtureFilePatternResolver {
    
    protected grailsApplication
    protected applicationContext
    
    private FixtureFilePatternResolver(grailsApplication, applicationContext) {
        this.grailsApplication = grailsApplication
        this.applicationContext = applicationContext
    }

    Resource[] resolve(String locationPattern) {
        def prefix = (grailsApplication.warDeployed) ? "" : "file:"
        def resources
        try {
            resources = applicationContext.getResources("${prefix}fixtures/${locationPattern}.groovy")
        } catch (Exception e) {
            throw new UnknownFixtureException(locationPattern, e)
        }
        
        if (resources.size() == 0 || resources.any { !it.exists() }) {
            throw new UnknownFixtureException(locationPattern)
        } else {
            resources
        }
    }
}