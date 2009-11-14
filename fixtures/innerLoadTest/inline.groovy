load {
    inner(String, "inner")
}

require "inner"

post {
    assert inner == "inner" // make sure we can get at beans from inners
}