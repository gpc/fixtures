package grails.fixture

import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.apache.commons.lang.StringUtils

abstract class AbstractFixtureBuilderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {
    
    public AbstractFixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    abstract createBeanPostProcessor(parentCtx)
    
    protected GenericApplicationContext createApplicationContext(ApplicationContext parent) {
        def ctx = super.createApplicationContext(parent)
        ctx.beanFactory.addBeanPostProcessor(createBeanPostProcessor(parent))
        ctx
    }
}