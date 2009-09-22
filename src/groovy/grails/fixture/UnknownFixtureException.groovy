package grails.fixture

class UnknownFixtureException extends Exception {

    UnknownFixtureException(name) {
        this(name, null)
    }

    UnknownFixtureException(name, Throwable cause) {
        super("could not load fixtures from pattern '${name}'" as String, cause)
    }
    
}