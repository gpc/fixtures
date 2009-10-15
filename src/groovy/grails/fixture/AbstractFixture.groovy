package grails.fixture

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    protected fixtureBuilder
    protected merge
    
    protected fixtures
    protected postProcessors = []
    
    AbstractFixture() {
        def binding = new Binding()
        
        binding.setVariable("fixture", this.&fixture)
        binding.setVariable("preProcess", this.&preProcess)
        binding.setVariable("postProcess", this.&postProcess)
        binding.setVariable("include", { String[] includes -> includes.each { doLoad(it, true) } })
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
    }
    
    def load(String[] locationPatterns) {
        preLoad()
        locationPatterns.each {
            doLoad(it, true)
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
                    if (!merging) preLoad() 
                    shell.evaluate(it.inputStream)
                    if (!merging) postLoad()
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
    
    def propertyMissing(name) {
        if (applicationContext.containsBean(name)) {
            applicationContext.getBean(name)
        } else {
            super.getProperty(name)
        }
    }
    
    abstract createBuilder()

}