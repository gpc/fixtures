package grails.plugin.fixtures.files

import org.springframework.core.io.Resource
import grails.plugin.fixtures.exception.UnknownFixtureException

class FixtureFilePatternResolver {
    
    def grailsApplication

    Resource[] resolve(String locationPattern) {
        def prefix = (grailsApplication.warDeployed) ? "" : "file:"
        def resources
        try {
            resources = grailsApplication.mainContext.getResources("${prefix}fixtures/${locationPattern}.groovy")
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