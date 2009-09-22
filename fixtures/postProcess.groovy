fixture {
    u(Uncle, name: "a")
}

postProcess {
    u.name = "changed"
    u.save()
}