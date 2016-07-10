package test.optionalowner

class OptionallyOwnedThing {

	String name
	OptionalOwner owner

	static constraints = {
		owner nullable: true
	}
}
