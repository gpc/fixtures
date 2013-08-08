load "beanTest/inner"

fixture {
	assert bean("inner") == "inner"
}
