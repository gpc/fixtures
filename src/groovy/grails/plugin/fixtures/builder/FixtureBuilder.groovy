package grails.plugin.fixtures.builder

import grails.plugin.fixtures.support.builder.AbstractFixtureBuilder
import grails.plugin.fixtures.exception.FixtureException

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

import org.hibernate.LockMode
import org.hibernate.TransientObjectException

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
        def sessionFactory = ctx.getBean("sessionFactory")
        
        ctx.beanDefinitionNames.each {
            try {
                def bean = ctx.getBean(it)
                if (grailsApplication.isDomainClass(bean.class)) {
                    try {
                        // We are merely verifying that the object is not transient here
                        sessionFactory.currentSession.lock(bean, LockMode.NONE)
                        bean.refresh()
                    } catch (TransientObjectException e) {
                        // do nothing
                    }
                }
            }
            catch(Exception e) {
                throw new FixtureException("Error refresh()ing bean '$it'", e)
            }
        }
        ctx
    } 
}