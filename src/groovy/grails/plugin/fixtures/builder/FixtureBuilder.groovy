package grails.plugin.fixtures.builder

import grails.plugin.fixtures.support.builder.AbstractFixtureBuilder
import grails.plugin.fixtures.exception.FixtureException

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

class FixtureBuilder extends AbstractFixtureBuilder {
    
    public FixtureBuilder(ApplicationContext parent,ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    public RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        new FixtureBuilderRuntimeSpringConfiguration(parent, classLoader)
    }
    
    public ApplicationContext createApplicationContext() {
        def ctx = super.createApplicationContext()
		def grailsApplication = ctx.getBean("grailsApplication")
        ctx.beanDefinitionNames.each {
            try {
                def bean = ctx.getBean(it)
                if (grailsApplication.isDomainClass(bean.class))
                    bean.refresh()
            }
            catch(Exception e) {
                throw new FixtureException("Error refresh()ing bean '$it'", e)
            }
        }
        ctx
    } 
}