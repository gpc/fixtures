package grails.plugin.fixtures.files.shell

import grails.plugin.fixtures.files.shell.handler.*

class FixtureBuildingShell extends GroovyShell {

	static handlers = [
		FixtureHandler, RequireHandler, RequireDefinitionsHandler, 
		RequireBeansHandler, PreHandler, PostHandler, IncludeHandler,
		LoadHandler, BuildHandler
	]
	
	FixtureBuildingShell(fileLoader) {
		super(fileLoader.fixture.grailsApplication.classLoader)
		handlers*.newInstance(fileLoader)*.register(this)
	}

}