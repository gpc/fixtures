package grails.plugin.fixtures

import grails.plugin.fixtures.builder.FixtureBuilder
import grails.plugin.fixtures.exception.UnknownFixtureException
import grails.plugin.fixtures.processor.FixtureProcessorDelegate
import grails.plugin.fixtures.exception.*

import org.springframework.beans.factory.config.RuntimeBeanReference

class Fixture {
    
    final applicationContext
    protected innerFixtures = []
    
    protected grailsApplication
    protected shell
    protected fixtureBuilder
    protected merge
    protected fixtures
    protected postProcessors = []
    
    protected fixtureNameStack = []
    protected currentLoadPattern

    Fixture(applicationContext, innerFixtures = []) {
        def binding = new Binding()
        
        binding.setVariable("fixture", this.&fixture)
        binding.setVariable("require", this.&require)
        binding.setVariable("requireDefinitions", this.&requireDefinitions)
        binding.setVariable("requireBeans", this.&requireBeans)
        binding.setVariable("pre", this.&preProcess)
        binding.setVariable("post", this.&postProcess)
        binding.setVariable("include", { String[] includes -> includes.each { doLoad(it, true) } })
        binding.setVariable("load", this.&innerLoad)
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
        this.innerFixtures = innerFixtures
        
        this.applicationContext = applicationContext
        this.grailsApplication = applicationContext.getBean('grailsApplication')
    }
    
    def createBuilder() {
        new FixtureBuilder(this)
    }

    def resolveLocationPattern(String locationPattern) {
        def prefix = (grailsApplication.warDeployed) ? "" : "file:"
        try {
            applicationContext.getResources("${prefix}fixtures/${locationPattern}.groovy")
        } catch(Exception e) {
            throw new UnknownFixtureException(locationPattern, e)
        }
    }
    
    def load(String[] locationPatterns) {
        preLoad()
        locationPatterns.each {
            currentLoadPattern = it
            doLoad(it, true)
            currentLoadPattern = null
        }
        postLoad()
        this
    }
    
    private doLoad(String locationPattern, merging = false) {
        def resources = resolveLocationPattern(locationPattern)
        if (resources) {
            resources.each {
                if (it.exists()) {
                    fixtureNameStack.push(it.filename)
                    if (!merging) preLoad() 
                    try {
                        shell.evaluate(it.inputStream, it.filename)
                    } catch (Throwable e) {
                        throw new FixtureException("Failed to evaluate ${it.filename} (pattern: '$locationPattern')", e)
                    }
                    if (!merging) postLoad()
                    fixtureNameStack.pop()
                } else {
                    throw new UnknownFixtureException(locationPattern)
                }
            }
        } else {
            throw new UnknownFixtureException(locationPattern)
        }
        
        this
    }
    
    protected preLoad() {
        fixtureBuilder = createBuilder()
    }
    
    protected postLoad() {
        applicationContext = fixtureBuilder.createApplicationContext()
        if (postProcessors) {
            def d = new FixtureProcessorDelegate(this)
            postProcessors.each { 
                it.delegate = d
                it()
            }
            postProcessors.clear()
        }
    }
    
    def load(Closure f) {
        preLoad()
        fixtureNameStack.push(f.class.name)
        fixture(f)
        fixtureNameStack.pop()
        postLoad()
        this
    }
    
    def fixture(Closure fixture) {
        fixtureBuilder.beans(fixture)
    }
    
    def postProcess(Closure postProcess) {
        postProcessors << postProcess.clone()
    }
    
    def preProcess(Closure p) {
        def d = new FixtureProcessorDelegate(this)
        p.delegate = d
        p()
    }

    def require(String[] requirements) {
        requirements.each {
            if (!getBeanOrDefinition(it)) {
                throw new UnsatisfiedFixtureRequirementException(
                    "Fixture '$currentlyLoadingFixtureName' requires '$it' which was not satisfied (load pattern: $currentLoadPattern)",
                    it, 
                    currentlyLoadingFixtureName, 
                    currentLoadPattern
                )
            }
        }
    }

    def requireDefinitions(String[] requirements) {
        requirements.each {
            if (!getBeanDefinition(it)) {
                throw new UnsatisfiedBeanDefinitionRequirementException(it, currentlyLoadingFixtureName, currentLoadPattern)
            }
        }
    }
    
    def requireBeans(String[] requirements) {
        requirements.each {
            if (!getBean(it)) {
                throw new UnsatisfiedBeanRequirementException(it, currentlyLoadingFixtureName, currentLoadPattern)
            }
        }
    }
    
    def innerLoad(Object[] things) {
        if (!things) {
            throw new IllegalArgumentException("load() inside a fixture file must have at least 1 argument")
        }

        // TODO this is bad, we are assuming that we are a Fixture, but not sure of a better way right now
        def innerFixture = this.class.newInstance(applicationContext, innerFixtures.clone())
        
        try {
            innerFixture.load(*things)
        } catch (Exception e) {
            throw new FixtureException("Failed to load inner fixture with $things", e)
        }
        innerFixtures << innerFixture
    }
    
    def propertyMissing(name) {
        getBean(name) ?: super.getProperty(name)
    }
        
    def getBean(name) {
        if (applicationContext.containsBean(name)) {
           applicationContext.getBean(name) 
        } else {
            def bean
            innerFixtures.find { bean = it.getBean(name) }
            bean
        }
    }
    
    def getBeanDefinition(name) {
        def definition = fixtureBuilder.getBeanDefinition(name)
        if (definition) {
            definition
        } else {
            if (applicationContext.containsBean(name)) {
                new RuntimeBeanReference(name, true)
            }
        }
    }

    def getBeanOrDefinition(name) {
        getBean(name) ?: getBeanDefinition(name)
    }
    
    def getCurrentlyLoadingFixtureName() {
        fixtureNameStack ? fixtureNameStack.last() : null
    }
}