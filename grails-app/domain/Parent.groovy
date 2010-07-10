class Parent {

	static belongsTo = Grandparent
	String name
	static hasMany = [children: Child, parents: Grandparent]
	Uncle brother

}