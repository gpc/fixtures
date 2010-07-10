package grails.plugin.fixtures.files.shell.handler

class BuildHandler extends FixtureBuildingShellHandler {

	final name = 'build'
	
	BuildHandler(fileLoader) {
		super(fileLoader)
	}
	
	def doCall(Closure f) {
		builder.build(f)
	}

}