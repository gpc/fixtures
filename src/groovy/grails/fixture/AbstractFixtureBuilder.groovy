package grails.fixture

import grails.spring.BeanBuilder
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

abstract class AbstractFixtureBuilder extends BeanBuilder {
    
    public AbstractFixtureBuilder() {
        super(null, null)
    }
    
    public AbstractFixtureBuilder(ClassLoader classLoader) {
        super(classLoader)
    }
    
    public AbstractFixtureBuilder(ApplicationContext parent) {
        super(parent)
    }
    
    public AbstractFixtureBuilder(ApplicationContext parent,ClassLoader classLoader) {
        super(parent, classLoader)
    }

    public getProperty(String name) {
        def parentCtx = getParentCtx()
        if (parentCtx?.containsBean(name)) {
            new RuntimeBeanReference(name, true)
        } else {
            super.getProperty(name)
        }
    }
    
    abstract RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader)
}