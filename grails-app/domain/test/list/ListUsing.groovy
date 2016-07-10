package test.list

class ListUsing {
	List inLists
	String name
	static hasMany = [inLists: InList]
	static belongsTo = [inLists: InList]
}
