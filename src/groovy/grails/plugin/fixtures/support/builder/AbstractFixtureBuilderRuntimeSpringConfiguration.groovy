package grails.plugin.fixtures.support.builder

import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.apache.commons.lang.StringUtils

abstract class AbstractFixtureBuilderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {
    
    public AbstractFixtureBuilderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    abstract createBeanPostProcessor(parentCtx)
    
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