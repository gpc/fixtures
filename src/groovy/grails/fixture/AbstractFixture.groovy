package grails.fixture

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    protected fixtureBuilder
    protected merge
    
    protected postProcessors = []
    
    AbstractFixture() {
        def binding = new Binding()
        binding.setVariable("fixture", this.&fixture)
        binding.setVariable("postProcess", this.&postProcess)
        this.shell = new GroovyShell(this.class.classLoader, binding)
    }
    
    def load(String[] locationPatterns) {
        preLoad()
        locationPatterns.each {
            load(it, true)
        }
        postLoad()
        this
    }
    
    abstract resolveLocationPattern(String locationPattern)
    
    def load(String locationPattern, merging = false) {
        def resources = resolveLocationPattern(locationPattern)
        if (resources) {
            resources.each {
                if (!merging) preLoad() 
                shell.evaluate(it.inputStream)
                if (!merging) postLoad()
            }
        } else {
            throw new UnknownFixtureException(fixture)
        }
        this
    }
    
    protected preLoad() {
        fixtureBuilder = createBuilder()
    }
    
    protected postLoad() {
        applicationContext = fixtureBuilder.createApplicationContext()
        if (postProcessors) {
            def d = new FixturePostProcessorDelegate(applicationContext)
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
    
    def propertyMissing(name) {
        if (applicationContext.containsBean(name)) {
            applicationContext.getBean(name)
        } else {
            super.getProperty(name)
        }
    }
    
    abstract createBuilder()

}