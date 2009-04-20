package grails.fixture

import grails.spring.BeanBuilder
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

class FixtureBuilder extends BeanBuilder {
    
    public FixtureBuilder() {
        super(null, null)
    }
    
    public FixtureBuilder(ClassLoader classLoader) {
        super(classLoader)
    }
    
    public FixtureBuilder(ApplicationContext parent) {
        super(parent)
    }
    
    public FixtureBuilder(ApplicationContext parent,ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    protected RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        new FixtureBuilderRuntimeSpringConfiguration(parent, classLoader)
    }
    
    public ApplicationContext createApplicationContext() {
        def ctx = super.createApplicationContext()
        ctx.beanDefinitionNames.each {
            try {
                ctx.getBean(it).refresh()
            }
            catch(Exception e) {
                throw new FixtureException("Error refresh()ing bean '$it'", e)
            }
        }
        ctx
    }
    
    public getProperty(String name) {
        def parentCtx = getParentCtx()
        if (parentCtx.containsBean(name)) {
            new RuntimeBeanReference(name, true)
        } else {
            super.getProperty(name)
        }
    }
}