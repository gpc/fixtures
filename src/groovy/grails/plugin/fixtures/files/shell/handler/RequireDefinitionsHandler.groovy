package grails.plugin.fixtures.files.shell.handler

import grails.plugin.fixtures.exception.*

class RequireDefinitionsHandler extends FixtureBuildingShellHandler {

	final name = 'requireDefinitions'
	
	RequireDefinitionsHandler(fileLoader) {
		super(fileLoader)
	}
	
	def doCall(String[] requirements) {
		requirements.each {
			if (!builder.getBeanDefinition(it)) {
				throw new UnsatisfiedBeanDefinitionRequirementException(it, currentlyLoadingFixtureName, currentLoadPattern)
			}
		}
	}

}