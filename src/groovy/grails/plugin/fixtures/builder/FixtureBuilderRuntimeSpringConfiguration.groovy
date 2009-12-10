package grails.plugin.fixtures.builder

import org.springframework.context.ApplicationContext

import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.context.support.GenericApplicationContext


class FixtureBuilderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {

    FixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
        
    def createBeanPostProcessor(parentCtx) {
        new FixtureBeanPostProcessor(parentCtx: parentCtx)
    }
    
    protected GenericApplicationContext createApplicationContext(ApplicationContext parent) {
        def ctx = super.createApplicationContext(parent)
        ctx.addBeanFactoryPostProcessor([
            postProcessBeanFactory: { beanFactory ->
                beanFactory.beanDefinitionNames.each {
                    beanFactory.getBeanDefinition(it).autowireMode = AbstractBeanDefinition.AUTOWIRE_BY_NAME
                }
            }] as BeanFactoryPostProcessor
        )
        ctx.beanFactory.addBeanPostProcessor(createBeanPostProcessor(parent))
        
        ctx
    }
}