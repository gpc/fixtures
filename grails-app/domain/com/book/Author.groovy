package com.book

class Author {

	String name

	static hasMany = [books: Book]

	static constraints = {
		name(blank: false, maxSize: 50)
	}

	String toString() { name }
}
