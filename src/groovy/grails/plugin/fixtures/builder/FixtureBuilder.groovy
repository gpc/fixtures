package grails.plugin.fixtures.builder

import grails.plugin.fixtures.Fixture
import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.builder.processor.*

import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.config.RuntimeBeanReference

import org.hibernate.LockMode
import org.hibernate.TransientObjectException

import grails.spring.BeanBuilder

import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

import grails.plugin.fixtures.buildtestdata.*

class FixtureBuilder extends BeanBuilder {
        
    def build = false // controls whether build-test-data is used

    protected fixture
    
    protected buildTestDataTranslator
    protected buildTestDataPluginInstalled
    
    protected definining = false // are we in the middle of a call to beans() ?
    
    FixtureBuilder(Fixture fixture) {
        super(fixture.applicationContext, fixture.class.classLoader)
        this.fixture = fixture
        this.buildTestDataTranslator = new BuildTestDataBeanDefinitionTranslator(grailsApplication: fixture.grailsApplication)
        registerPostProcessors()
        lookForBuildTestDataPlugin()
    }

    protected lookForBuildTestDataPlugin() {
        buildTestDataPluginInstalled = PluginManagerHolder.pluginManager.hasGrailsPlugin('build-test-data')
    }
    
    protected registerPostProcessors() {
        beans {
            autoAutoWirer(FixtureBeanAutoAutowireConfigurer)
            fixtureBeanPostProcessor(FixtureBeanPostProcessor)
        }
    }
    
    def getProperty(String name) {
        def parentCtx = getParentCtx()
        if (parentCtx?.containsBean(name)) {
            parentCtx.getBean(name)
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
    
    def build(Closure definitions) {
        def previousBuild = this.build
        build = true
        beans(definitions)
        build = previousBuild
        this
    }
    
    def noBuild(Closure definitions) {
        def previousBuild = this.build
        build = false
        beans(definitions)
        build = previousBuild
        this
    }
    
    def bean(String name) {
        def bean = fixture.getBean(name)
        if (!bean) {
            throw new IllegalArgumentException("Fixture does not have bean '$name'")
        } 
        bean
    }

    BeanBuilder beans(Closure definition) {
        if (!definining) {
            definining = true
            super.beans(definition)
            definining = false
        } else {
            definition.delegate = this
            definition.call()
        }
        this
    }
    
    def invokeMethod(String name, args) {
        def mm = this.metaClass.getMetaMethod(name, *args)
        if (mm) {
            mm.invoke(this, *args)
        } else {
            translateToBuild(name, *args) ?: super.invokeMethod(name, args)
        }
    }    
    
    protected translateToBuild(String name, Object[] args) {
        isBuildTestDataActive() ? buildTestDataTranslator.translate(this, name, *args) : null
    }
    
    protected isBuildTestDataActive() {
        buildTestDataPluginInstalled && this.build
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