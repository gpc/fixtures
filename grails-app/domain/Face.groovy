class Face {

	static hasMany = [eyes: Eye]
	Nose nose
	
	static constraints = {
		nose(nullable: true)
	}
}