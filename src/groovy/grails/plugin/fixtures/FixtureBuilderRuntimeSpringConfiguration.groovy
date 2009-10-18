package grails.plugin.fixtures

import org.springframework.context.ApplicationContext

class FixtureBuilderRuntimeSpringConfiguration extends AbstractFixtureBuilderRuntimeSpringConfiguration {

    public FixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
        
    def createBeanPostProcessor(parentCtx) {
        new FixtureBeanPostProcessor(parentCtx: parentCtx)
    }
}