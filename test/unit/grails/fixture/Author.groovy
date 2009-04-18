package grails.fixture

class Author {
    String name
    Set books
    Book firstBook

    def validate() {
        true
    }

    def save(Map map) {
        true
    }
}