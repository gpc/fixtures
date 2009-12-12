package grails.plugin.fixtures.files.shell.handler

class PreHandler extends ProcessorHandlerSupport {

    final name = 'pre'
    
    PreHandler(fileLoader) {
        super(fileLoader)
    }
    
    def doCall(Closure pre) {
        attachDelegate(pre).call()
    }

}