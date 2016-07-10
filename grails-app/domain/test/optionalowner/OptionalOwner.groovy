package test.optionalowner

class OptionalOwner {

	String name

	static hasMany = [things: OptionallyOwnedThing]
}
