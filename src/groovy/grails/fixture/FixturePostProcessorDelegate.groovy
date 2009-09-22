package grails.fixture
 
class FixturePostProcessorDelegate {

    final ctx
    
    FixturePostProcessorDelegate(ctx) {
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