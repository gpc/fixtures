package grails.plugin.fixtures.exception

class FixtureException extends Exception {

    FixtureException(message) {
        super(message as String)
    }
    
    FixtureException(message, cause) {
        super(message as String, cause)
    }

}