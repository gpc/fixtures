package grails.plugin.fixtures.support.builder

import grails.plugin.fixtures.support.AbstractFixture

import grails.spring.BeanBuilder
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.springframework.beans.factory.config.RuntimeBeanReference

import org.slf4j.LoggerFactory

abstract class AbstractFixtureBuilder extends BeanBuilder {

    static log = LoggerFactory.getLogger(AbstractFixtureBuilder)
    
    protected fixture
    
    public AbstractFixtureBuilder(AbstractFixture fixture) {
        super(fixture.applicationContext, fixture.class.classLoader)
        this.fixture = fixture
    }
    
    public getProperty(String name) {
        def currentFixture = fixture.currentlyLoadingFixtureName
        log.debug("dynamically resolving property '$name' in fixture '$currentFixture'")
        def parentCtx = getParentCtx()
        if (parentCtx?.containsBean(name)) {
            log.debug("resolved property '$name' in fixture '$currentFixture' to bean definition in parent context")
            new RuntimeBeanReference(name, true)
        } else {
            try {
                def property = super.getProperty(name)
                log.debug("resolved property '$name' in fixture '$currentFixture' to bean definition in current context")
                property
            } catch (MissingPropertyException e) {
                def bean = bean(name)
                if (!bean) throw e
                log.debug("resolved property '$name' in fixture '$currentFixture' to bean '$bean'")
                bean
            }
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