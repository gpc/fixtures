class FixtureTests extends GroovyTestCase {
    
    def fixtureLoader

    void testPostProcess() {
        fixtureLoader.load("postProcess").with {
            assertEquals("changed", u.name)
        }
    }
    
    void testLoadFixtureFiles() {
        fixtureLoader.load("testFixture1", "testFixture2")
    }
    
    void testLoadClosures() {
        fixtureLoader.load {
            u2(Uncle, name: "2") {
            }
        }.load {
            c2(Child, name: "2", uncle: u2) 
        }
    }
    
    void testLoadPartial() {
        fixtureLoader.load("books/*", "authors/*")
    }
    
    void testLoadUnknownFixtures() {
        ["books", "xxx*"].each { pattern ->
            shouldFail(grails.fixture.UnknownFixtureException) {
                fixtureLoader.load(pattern)
            }
        }
        
    }
    
    void testGetObjectsFromFixture() {
        def f = fixtureLoader.load {
            u3(Uncle, name: "u3")
        }
        assertNotNull(f.u3)
        assertEquals("u3", f.u3.name)
    }
    
    void testBadFixture() {
        shouldFail(org.springframework.beans.factory.BeanCreationException) {
            fixtureLoader.load {
                c(Child, name: "c") // no uncle property
            }
        }
    }
    
    void testComplexFixture1() {
        def f = fixtureLoader.load {
            c1(Child, name: "c1", uncle: ref("u1"))
            c2(Child, name: "c2", uncle: ref("u1"))
            u1(Uncle, name: "u1")
            p1(Parent, name: "p1", children: [c1,c2], brother: u1)
            p2(Parent, name: "p2", children: [c1,c2], brother: u1)
            gp1(Grandparent, name: "gp1", children: [p1,p2])
        }
        assertEquals(2, f.c1.parents.size())
        assertEquals(2, f.c2.parents.size())
        assertNotNull(f.p1.parents.find { it.is(f.gp1) })
    }

	void testAutowiring() {
		def f = fixtureLoader.load {
            partner(String, "value")
            am(AutowireMe)
        }
        assertNotNull(f.am)
        assertEquals(f.am.partner, "value")
	}
	
	void testIncludes() {
	   fixtureLoader.load("includeTest").with {
	       assertNotNull(u1)
	   }
	}
}
