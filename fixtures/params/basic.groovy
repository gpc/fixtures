import test.Uncle

// make sure we can access from the script outside the fixture block
assert params instanceof Map

fixture {
	// and inside the fixture block
	u(Uncle, name: params.name ?: 'default name')
	build {
		v(Uncle) {
			// and in here as well
			if (params.name) {
				name = params.name
			}
		}
	}
}
