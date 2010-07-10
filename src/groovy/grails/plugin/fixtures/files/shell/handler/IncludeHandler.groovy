package grails.plugin.fixtures.files.shell.handler

class IncludeHandler extends FixtureBuildingShellHandler {

	final name = 'include'
	
	IncludeHandler(fileLoader) {
		super(fileLoader)
	}
	
	def doCall(String[] includes) {
		this.include(*includes)
	}

}