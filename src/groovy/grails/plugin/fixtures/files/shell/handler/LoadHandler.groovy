package grails.plugin.fixtures.files.shell.handler

import grails.plugin.fixtures.Fixture
import grails.plugin.fixtures.exception.*

class LoadHandler extends FixtureBuildingShellHandler {

	final name = 'load'
	
	LoadHandler(fileLoader) {
		super(fileLoader)
	}
	
	def doCall(patternsOrClosures) {
		def inner = new Fixture(fixture.grailsApplication, fixture.applicationContext, inners.clone())
		
		if (patternsOrClosures instanceof Closure) {
			inner.load(patternsOrClosures)
		} else {
			try {
				inner.load(patternsOrClosures)
			} catch (Exception e) {
				throw new FixtureException("Failed to load inner fixture with $patterns", e)
			}
		}
		
		inners << inner
	}

}