package grails.plugin.fixtures.builder

import grails.plugin.fixtures.Fixture

import grails.plugin.fixtures.exception.FixtureException

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

import org.hibernate.LockMode
import org.hibernate.TransientObjectException

import grails.spring.BeanBuilder

class FixtureBuilder extends BeanBuilder {
    
    protected fixture

    FixtureBuilder(Fixture fixture) {
        super(fixture.applicationContext, fixture.class.classLoader)
        this.fixture = fixture
    }

    def getProperty(String name) {
        def parentCtx = getParentCtx()
        if (parentCtx?.containsBean(name)) {
            new RuntimeBeanReference(name, true)
        } else {
            try {
                def property = super.getProperty(name)
                property
            } catch (MissingPropertyException e) {
                def bean = bean(name)
                if (!bean) throw e
                bean
            }
        }
    }
    
    def bean(String name) {
        def bean = fixture.getBean(name)
        if (!bean) {
            throw new IllegalArgumentException("Fixture does not have bean '$name'")
        } 
        bean
    }
    
    def invokeMethod(String name, arg) {
        if (name == "bean") {
            bean(*arg)
        } else {
            super.invokeMethod(name, arg)
        }
    }
    
    protected RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        new FixtureBuilderRuntimeSpringConfiguration(parent, classLoader)
    }
    
    def ApplicationContext createApplicationContext() {
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