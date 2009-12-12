package grails.plugin.fixtures.files.shell.handler

import grails.plugin.fixtures.exception.*

class RequireBeansHandler extends FixtureBuildingShellHandler {

    final name = 'requireBeans'

    RequireBeansHandler(fileLoader) {
        super(fileLoader)
    }
    
    def doCall(String[] requirements) {
        requirements.each {
            if (!fixture.getBean(it)) {
                throw new UnsatisfiedBeanRequirementException(it, currentlyLoadingFixtureName, currentLoadPattern)
            }
        }
    }

}