package grails.plugin.fixtures.files.shell.handler

abstract class ProcessorHandlerSupport extends FixtureBuildingShellHandler {

	ProcessorHandlerSupport(fileLoader) {
		super(fileLoader)
	}

	def attachDelegate(processor) {
		processor.delegate = new ProcessorDelegate(fixture)
		processor
	}

}
 
class ProcessorDelegate {
	final fixture
	
	ProcessorDelegate(fixture) {
		this.fixture = fixture
	}

	def propertyMissing(name) {
		fixture.getBean(name) ?: super.getProperty(name)
	}
}