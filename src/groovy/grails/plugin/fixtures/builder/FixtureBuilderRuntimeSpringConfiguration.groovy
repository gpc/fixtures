package grails.plugin.fixtures.builder

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.context.support.GenericApplicationContext

import grails.plugin.fixtures.builder.processor.FixtureBeanPostProcessor
import grails.plugin.fixtures.builder.processor.FixtureBeanAutoAutowireConfigurer

class FixtureBuilderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {

    FixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }

    protected GenericApplicationContext createApplicationContext(ApplicationContext parent) {
        def ctx = super.createApplicationContext(parent)
        ctx.addBeanFactoryPostProcessor(new FixtureBeanAutoAutowireConfigurer())
        ctx.beanFactory.addBeanPostProcessor(new FixtureBeanPostProcessor(parentCtx: parent))
        ctx
    }
}