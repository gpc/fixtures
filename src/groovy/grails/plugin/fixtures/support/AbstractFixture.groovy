package grails.plugin.fixtures.support

import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.exception.UnknownFixtureException
import grails.plugin.fixtures.exception.UnsatisfiedBeanRequirementException
import grails.plugin.fixtures.exception.UnsatisfiedBeanDefinitionRequirementException

import grails.plugin.fixtures.processor.FixtureProcessorDelegate

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    protected fixtureBuilder
    protected merge
    
    protected fixtures
    protected postProcessors = []
    
    protected fixtureNameStack = []
    protected currentLoadPattern
    protected innerFixtures = []
    
    AbstractFixture() {
        def binding = new Binding()
        
        binding.setVariable("fixture", this.&fixture)
        binding.setVariable("require", this.&require)
        binding.setVariable("requireBeans", this.&requireBeans)
        binding.setVariable("pre", this.&preProcess)
        binding.setVariable("post", this.&postProcess)
        binding.setVariable("include", { String[] includes -> includes.each { doLoad(it, true) } })
        binding.setVariable("load", this.&innerLoad)
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
    }
    
    def getApplicationContext() { 
        applicationContext 
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
    
    abstract resolveLocationPattern(String locationPattern)
    
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
    
    def innerLoad(String[] locationPatterns) {
        // TODO this is bad, we are assuming that we are a Fixture, but not sure of a better way right now
        def innerFixture = this.class.newInstance(applicationContext)
        try {
            innerFixture.load(*locationPatterns)
        } catch (Exception e) {
            throw new FixtureException("Failed to load inner fixture for patterns $locationPatterns", e)
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
        def beanDefinition = fixtureBuilder.getBeanDefinition(name)
        if (beanDefinition) {
            beanDefinition
        } else {
            innerFixtures.find { beanDefinition = it.getBeanDefinition(name) }
            beanDefinition
        }
    }

    def getBeanOrDefinition(name) {
        getBean(name) ?: getBeanDefinition(name)
    }
    
    def getCurrentlyLoadingFixtureName() {
        fixtureNameStack ? fixtureNameStack.last() : null
    }
    
    abstract createBuilder()

}