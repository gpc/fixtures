package grails.fixture

class UnknownFixtureException extends Exception {

    UnknownFixtureException(name) {
        super("could not find fixture with name '${name}'" as String)
    }

}