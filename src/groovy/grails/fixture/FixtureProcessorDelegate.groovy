package grails.fixture
 
class FixtureProcessorDelegate {

    final ctx
    
    FixtureProcessorDelegate(ctx) {
        this.ctx = ctx
    }

    def propertyMissing(name) {
        if (ctx.containsBean(name)) {
            ctx.getBean(name)
        } else {
            super.getProperty(name)
        }
    }

}