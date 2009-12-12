package grails.plugin.fixtures.files.shell.handler

import grails.plugin.fixtures.exception.*

class RequireHandler extends FixtureBuildingShellHandler {

    final name = 'require'
    
    RequireHandler(fileLoader) {
        super(fileLoader)
    }
    
    def doCall(String[] requirements) {
        requirements.each {
            if (!fixture.getBean(it) && !builder.getBeanDefinition(it)) {
                throw new UnsatisfiedFixtureRequirementException(
                    "Fixture '$currentlyLoadingFixtureName' requires '$it' which was not satisfied (load pattern: $currentLoadPattern)",
                    it, 
                    currentlyLoadingFixtureName, 
                    currentLoadPattern
                )
            }
        }
    }

}