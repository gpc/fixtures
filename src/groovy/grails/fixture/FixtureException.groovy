package grails.fixture

class FixtureException extends Exception {

    FixtureException(message, cause) {
        super(message as String, cause)
    }

}