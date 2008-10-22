package grails.fixture

import grails.spring.BeanBuilder
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration

class FixtureBuilder extends BeanBuilder {
    
    public FixtureBuilder() {
        super(null, null)
    }
    
    public FixtureBuilder(ClassLoader classLoader) {
        super(null, classLoader)
    }
    
    public FixtureBuilder(ApplicationContext parent) {
        super(parent, null)
    }
    
    public FixtureBuilder(ApplicationContext parent,ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    protected RuntimeSpringConfiguration createRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        new FixtureLoaderRuntimeSpringConfiguration(parent, classLoader)
    }
}