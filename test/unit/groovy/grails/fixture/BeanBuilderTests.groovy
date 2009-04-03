package grails.fixture
/**
 * Created by IntelliJ IDEA.
 * User: llavandowska
 * Date: Mar 13, 2009
 * Time: 1:50:07 PM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.commons.dbcp.BasicDataSource
import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean
import org.springframework.context.ApplicationContext

public class BeanBuilderTests extends GroovyTestCase {

	void testBeanBuilder() {
		def bb = new grails.spring.BeanBuilder()
		bb.beans{
			bart(Person) {
				name = "Bart"
				age = 11
			}
			lisa(Person) {
				name = "Lisa"
				age = 9
				//parent = ref("marge", true)
			}
			marge(Person.class) {
				name = "marge"
				husband =  { Person p ->
							 name = "homer"
							 age = 45
				}
				children = [bart, lisa]
			}
		}
		ApplicationContext appContext = bb.createApplicationContext()
		assertNotNull appContext.marge
	}
}