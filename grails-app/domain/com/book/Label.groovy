package com.book

class Label {
	String name

	static belongsTo = [book: Book]

	static constraints = {
		name blank: false
	}
}
