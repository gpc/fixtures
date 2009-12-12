package grails.plugin.fixtures.files.shell

import grails.plugin.fixtures.files.shell.handler.FixtureHandler
import grails.plugin.fixtures.files.shell.handler.RequireHandler
import grails.plugin.fixtures.files.shell.handler.RequireDefinitionsHandler
import grails.plugin.fixtures.files.shell.handler.RequireBeansHandler
import grails.plugin.fixtures.files.shell.handler.PreHandler
import grails.plugin.fixtures.files.shell.handler.PostHandler
import grails.plugin.fixtures.files.shell.handler.IncludeHandler
import grails.plugin.fixtures.files.shell.handler.LoadHandler

class FixtureBuildingShell extends GroovyShell {

    static handlers = [
        FixtureHandler, RequireHandler, RequireDefinitionsHandler, 
        RequireBeansHandler, PreHandler, PostHandler, IncludeHandler,
        LoadHandler
    ]
    
    FixtureBuildingShell(fileLoader) {
        super(fileLoader.fixture.grailsApplication.classLoader)
        handlers*.newInstance(fileLoader)*.register(this)
    }

}