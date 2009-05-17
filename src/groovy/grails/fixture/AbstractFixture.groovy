package grails.fixture

abstract class AbstractFixture {

    protected shell
    protected applicationContext
    
    AbstractFixture() {
        def binding = new Binding()
        binding.setVariable("fixture") {
            load(it)
        }
        
        this.shell = new GroovyShell(this.class.classLoader, binding)
    }
    
    def load(String[] fixtures) {
        fixtures.each {
            load(it)
        }
        this
    }
    
    def load(String fixture) {
        def file = new File("fixtures/${fixture}.groovy")
        if (file.exists()) {
            shell.evaluate(file)
        } else {
            throw new UnknownFixtureException(it)
        }
        this
    }
    
    def load(Closure fixture) {
        def fixtureBuilder = createBuilder()
        fixtureBuilder.beans(fixture)
        this.applicationContext = fixtureBuilder.createApplicationContext()
        this
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