package grails.plugin.fixtures.builder

import grails.plugin.fixtures.support.builder.AbstractFixtureBeanPostProcessor

class FixtureBeanPostProcessor extends AbstractFixtureBeanPostProcessor {

    def getDomainClass(clazz) {
        this.parentCtx.getBean('grailsApplication').getDomainClass(clazz.name)
    }
    
    def getMessageSource() {
        this.parentCtx.getBean('messageSource')
    }
}