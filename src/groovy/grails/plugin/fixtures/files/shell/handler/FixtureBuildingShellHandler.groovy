package grails.plugin.fixtures.files.shell.handler

abstract class FixtureBuildingShellHandler extends Closure {
	
	abstract getName()

	FixtureBuildingShellHandler(fileLoader) {
		super(fileLoader)
	}
	
	void register(shell) {
		shell.setVariable(getName(), this)
	}
}