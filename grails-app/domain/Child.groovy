class Child {
    
    String name
    Date dob = new Date()
    static belongsTo = Parent
    static hasMany = [parents: Parent]
    Uncle uncle

}