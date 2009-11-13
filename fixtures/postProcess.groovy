fixture {
    u(Uncle, name: "a")
}

post {
    u.name = "changed"
    u.save()
}