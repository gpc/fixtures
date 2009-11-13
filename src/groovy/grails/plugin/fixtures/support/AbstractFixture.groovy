package grails.plugin.fixtures.support

import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.exception.UnknownFixtureException
import grails.plugin.fixtures.exception.UnsatisfiedFixtureRequirementException

import grails.plugin.fixtures.processor.FixtureProcessorDelegate

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    protected fixtureBuilder
    protected merge
    
    protected fixtures
    protected postProcessors = []
    
    protected fixtureResourceStack = []
    protected currentLoadPattern
    
    AbstractFixture() {
        def binding = new Binding()
        
        binding.setVariable("fixture", this.&fixture)
        binding.setVariable("require", this.&require)
        binding.setVariable("preProcess", this.&preProcess)
        binding.setVariable("postProcess", this.&postProcess)
        binding.setVariable("include", { String[] includes -> includes.each { doLoad(it, true) } })
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
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
                    fixtureResourceStack.push(it)
                    if (!merging) preLoad() 
                    try {
                        shell.evaluate(it.inputStream)
                    } catch (Throwable e) {
                        throw new FixtureException("Failed to evaluate ${it.filename} (pattern: '$locationPattern')", e)
                    }
                    if (!merging) postLoad()
                    fixtureResourceStack.pop()
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
            def d = new FixtureProcessorDelegate(applicationContext)
            postProcessors.each { 
                it.delegate = d
                it()
            }
            postProcessors.clear()
        }
    }
    
    def load(Closure f) {
        preLoad()
        fixture(f)
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
        def d = new FixtureProcessorDelegate(applicationContext)
        p.delegate = d
        p()
    }

    def require(String[] requirements) {
        requirements.each {
            if (!getBeanOrDefinition(it)) {
                throw new UnsatisfiedFixtureRequirementException(it, fixtureResourceStack.last(), currentLoadPattern)
            }
        }
    }
    
    def propertyMissing(name) {
        getBeanOrDefinition(name) ?: super.getProperty(name)
    }
    
    def getBeanOrDefinition(name) {
        applicationContext.containsBean(name) ? applicationContext.getBean(name) : fixtureBuilder.getBeanDefinition(name)
    }
    abstract createBuilder()

}