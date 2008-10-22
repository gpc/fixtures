package grails.fixture

import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext

class FixtureLoaderRuntimeSpringConfiguration extends DefaultRuntimeSpringConfiguration {

    public FixtureLoaderRuntimeSpringConfiguration(ApplicationContext parent, ClassLoader classLoader) {
        super(parent, classLoader)
    }
    
    protected GenericApplicationContext createApplicationContext(ApplicationContext parent) {
        def ctx = super.createApplicationContext(parent)
        def messageSource = parent.getBean("messageSource")
        ctx.beanFactory.addBeanPostProcessor(
            [
                postProcessBeforeInitialization: { Object bean, String beanName ->
                    bean
                },
                postProcessAfterInitialization: { Object bean, String beanName ->
                    if (!bean.validate()) {
                        def errorcodes = bean.errors.allErrors.collect { "'${messageSource.getMessage(it, null)}'" }
                        throw new IllegalStateException("fixture bean '$beanName' has errors: ${errorcodes.join(', ')}")
                    }
                    if (!bean.save(flush: true)) {
                        throw new Error("failed to save fixture bean '$beanName'")
                    }
                },
            ] as BeanPostProcessor
        )
        ctx
    }
}