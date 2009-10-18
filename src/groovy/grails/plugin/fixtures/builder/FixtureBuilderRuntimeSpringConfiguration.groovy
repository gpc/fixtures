package grails.plugin.fixtures.builder

import org.springframework.context.ApplicationContext

import grails.plugin.fixtures.support.builder.AbstractFixtureBuilderRuntimeSpringConfiguration

class FixtureBuilderRuntimeSpringConfiguration extends AbstractFixtureBuilderRuntimeSpringConfiguration {

    public FixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
        
    def createBeanPostProcessor(parentCtx) {
        new FixtureBeanPostProcessor(parentCtx: parentCtx)
    }
}