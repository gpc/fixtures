package grails.fixture

class Book {
    String name
    Set authors
    static hasMany = [authors: Author]

    def validate() {
        true
    }

    def save(Map map) {
        true
    }
}