package grails.plugin.fixtures.files.shell.handler

class PostHandler extends ProcessorHandlerSupport {

    final name = 'post'
    
    PostHandler(fileLoader) {
        super(fileLoader)
    }
    
    def doCall(Closure post) {
        this.addPost(attachDelegate(post))
    }

}