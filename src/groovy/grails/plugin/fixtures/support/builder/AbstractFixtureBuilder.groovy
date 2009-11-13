package grails.plugin.fixtures.support.builder

import grails.plugin.fixtures.support.AbstractFixture

import grails.spring.BeanBuilder
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

abstract class AbstractFixtureBuilder extends BeanBuilder {
    
    protected fixture
    
    public AbstractFixtureBuilder(AbstractFixture fixture) {
        super(fixture.applicationContext, fixture.class.classLoader)
        this.fixture = fixture
    }
    
    public getProperty(String name) {
        def parentCtx = getParentCtx()
        if (parentCtx?.containsBean(name)) {
            new RuntimeBeanReference(name, true)
        } else {
            super.getProperty(name)
        }
    }
    
    public bean(String name) {
        def bean = fixture.getBean(name)
        if (!bean) {
            throw new IllegalArgumentException("Fixture does not have bean '$bean'")
        } 
        bean
    }
    
    public invokeMethod(String name, arg) {
        if (name == "bean") {
            bean(*arg)
        } else {
            super.invokeMethod(name, arg)
        }
    }
    
    abstract RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader)
}