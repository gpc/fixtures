package grails.fixture

class Fixture {

    private applicationContext
    private shell
    
    Fixture(applicationContext) {
        this.applicationContext = applicationContext
        
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
        fixtureBuilder = new FixtureBuilder(applicationContext, this.class.classLoader)
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

}