package m2m

class C {
    static belongsTo = [A, B]
    static hasMany = [as: A, bs: B]
}