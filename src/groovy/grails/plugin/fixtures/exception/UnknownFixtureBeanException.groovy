package grails.plugin.fixtures.exception

class UnknownFixtureBeanException extends Exception {

    UnknownFixtureBeanException(name) {
        super("there is no bean named '${name}' in this fixture" as String)
    }

}