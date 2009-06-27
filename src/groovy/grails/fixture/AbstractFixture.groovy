package grails.fixture

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    protected fixtureBuilder
    protected merge
    
    AbstractFixture() {
        def binding = new Binding()
        binding.setVariable("fixture") {
            fixture(it)
        }
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
    }
    
    def load(String[] fixtures) {
        preLoad()
        fixtures.each {
            load(it, true)
        }
        postLoad()
        this
    }
    
    def load(String fixture, merging = false) {
        def file = new File("fixtures/${fixture}.groovy")
        if (file.exists()) {
            if (!merging) preLoad() 
            shell.evaluate(file)
            if (!merging) postLoad()
        } else {
            throw new UnknownFixtureException(it)
        }
        this
    }
    
    protected preLoad() {
        fixtureBuilder = createBuilder()
    }
    
    protected postLoad() {
        applicationContext = fixtureBuilder.createApplicationContext()
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
    
    def propertyMissing(name) {
        if (applicationContext.containsBean(name)) {
            applicationContext.getBean(name)
        } else {
            super.getProperty(name)
        }
    }
    
    abstract createBuilder()

}