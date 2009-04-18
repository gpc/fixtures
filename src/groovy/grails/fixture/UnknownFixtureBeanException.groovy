package grails.fixture

class UnknownFixtureBeanException extends Exception {

    UnknownFixtureBeanException(name) {
        super("there is no bean named '${name}' in this fixture" as String)
    }

}