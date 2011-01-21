package optionalowner

class OptionalOwner {

	String name
	
	static hasMany = [things: OptionallyOwnedThing]
	
}