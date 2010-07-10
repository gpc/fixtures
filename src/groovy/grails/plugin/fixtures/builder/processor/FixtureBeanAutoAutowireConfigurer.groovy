package grails.plugin.fixtures.builder.processor

import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

class FixtureBeanAutoAutowireConfigurer implements BeanFactoryPostProcessor {

	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		beanFactory.beanDefinitionNames.each {
			beanFactory.getBeanDefinition(it).autowireMode = ConfigurableListableBeanFactory.AUTOWIRE_BY_NAME
		}
	}

}

