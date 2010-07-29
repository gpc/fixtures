/*
 * Copyright 2010 Grails Plugin Collective
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import grails.plugin.fixtures.exception.UnknownFixtureException
import grails.plugin.fixtures.exception.UnknownFixtureBeanException
import grails.plugin.fixtures.exception.UnsatisfiedFixtureRequirementException
import grails.plugin.fixtures.exception.UnsatisfiedBeanRequirementException
import grails.plugin.fixtures.exception.UnsatisfiedBeanDefinitionRequirementException
import org.springframework.beans.factory.BeanCreationException

class FixtureTests extends GroovyTestCase {
	
	def fixtureLoader
	def preProcessTestService
	
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
			shouldFail(UnknownFixtureException) {
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
		shouldFail(BeanCreationException) {
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
	
	void testPreProcess() {
	   fixtureLoader.load('preProcess')
	   assertEquals("changed", preProcessTestService.v)
	}
	
	void testNamed() {
		fixtureLoader['test'].load("testFixture1", "testFixture2")
		assertEquals('a', fixtureLoader['test'].u1.name)
		
		fixtureLoader.testClosure.load {
			u(Uncle, name: "u")
		}

		assertEquals('u', fixtureLoader.testClosure.u.name)
	}
	
	void testRequire() {
		fixtureLoader.load "requireTest/good"
		
		shouldFailWithCause(UnsatisfiedFixtureRequirementException) {
			fixtureLoader.load "requireTest/bad"
		}
	}
	
	void testRequireBeans() {
		fixtureLoader.load "requireBeansTest/good"
		
		shouldFailWithCause(UnsatisfiedBeanRequirementException) {
			fixtureLoader.load "requireBeansTest/bad"
		}
	}

	void testRequireDefinitions() {
		fixtureLoader.load "requireDefinitionsTest/good"
		
		shouldFailWithCause(UnsatisfiedBeanDefinitionRequirementException) {
			fixtureLoader.load "requireDefinitionsTest/bad"
		}
	}
	
	void testInnerLoad() {
		fixtureLoader.load("innerLoadTest/outer").with {
			assertNotNull(inner)
		}
	}

	void testInnerInlineLoad() {
		fixtureLoader.load("innerLoadTest/inline").with {
			assertNotNull(inner)
		}
	}
	
	void testBeanInFixture() {
		fixtureLoader.load("beanTest/outer")
	}
	
	void testSettingRelationshipsViaReverse() {
		fixtureLoader.load("reverse/children")
	}
	
	void testSettingRelationshipsViaReverse2() {
		fixtureLoader.load("reverse2")
	}
	
	void testRetrievingUnknownBeanShouldThrowUnknownFixtureBeanException() {
		shouldFail(UnknownFixtureBeanException) {
			fixtureLoader.load({}).someUnknownThing
		}
	}
}
