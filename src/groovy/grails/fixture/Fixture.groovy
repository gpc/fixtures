package grails.fixture
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.NoSuchBeanDefinitionException

class Fixture {

    ApplicationContext applicationContext
    
    def getAt(String name) {
        try {
            applicationContext.getBean(name)
        }
        catch(NoSuchBeanDefinitionException e) {
            throw new UnknownFixtureBeanException(name)
        }
    }
    
    def getProperty(String name) {
        getAt(name)
    }

}