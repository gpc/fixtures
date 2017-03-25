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
import grails.plugin.fixtures.exception.FixtureException
import grails.plugin.fixtures.exception.UnknownFixtureBeanException
import grails.plugin.fixtures.exception.UnknownFixtureException
import grails.plugin.fixtures.exception.UnsatisfiedBeanDefinitionRequirementException
import grails.plugin.fixtures.exception.UnsatisfiedBeanRequirementException
import grails.plugin.fixtures.exception.UnsatisfiedFixtureRequirementException
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.BeanCreationException
import spock.lang.Specification

import test.book.Author
import test.book.Book
import test.book.Label
import test.Child
import test.Uncle
import test.Parent
import test.Grandparent
import test.AutowireMe


@Integration
@Rollback
class FixtureTests extends Specification {

	def fixtureLoader
	def preProcessTestService

	void testPostProcess() {
		expect:	fixtureLoader.load("postProcess").with {
			"changed" == u.name
		}
	}

	void testLoadFixtureFiles() {
		expect:	fixtureLoader.load("testFixture1", "testFixture2")
	}

	void testLoadClosures() {
		expect:	fixtureLoader.load {
			u2(Uncle, name: "2") {
			}
		}.load {
			c2(Child, name: "2", uncle: u2)
		}
	}

	void testLoadPartial() {
		expect:	fixtureLoader.load("books/*", "authors/*")
	}

	void testLoadUnknownFixtures() {
		when:	["books", "xxx*"].each { pattern ->
				fixtureLoader.load(pattern)
			}
        then: thrown(UnknownFixtureException)
	}

	void testGetObjectsFromFixture() {
		given:	def f = fixtureLoader.load {
					u3(Uncle, name: "u3")
				}
		expect:
		f.u3 != null
		"u3"== f.u3.name
	}

	void testBadFixture() {
		when:fixtureLoader.load {
				c(Child, name: "c") // no uncle property
			}
        then:thrown(BeanCreationException)
	}

	void testComplexFixture1() {
		given:	def f = fixtureLoader.load {
					c1(Child, name: "c1", uncle: ref("u1"))
					c2(Child, name: "c2", uncle: ref("u1"))
					u1(Uncle, name: "u1")
					p1(Parent, name: "p1", children: [c1,c2], brother: u1)
					p2(Parent, name: "p2", children: [c1,c2], brother: u1)
					gp1(Grandparent, name: "gp1", children: [p1,p2])
				}
		expect:
		2== f.c1.parents.size()
		2== f.c2.parents.size()
		f.p1.parents.find { it.is(f.gp1) }!=null
	}

	void testNestedBidirectionalOneToMany() {
		when:   def f = fixtureLoader.load {
			    king(Author, name: "Stephen King",
                        books: [new Book(title: "Misery" , labels: [ new Label(name: "a"), new Label(name: "b") ]),
                                new Book(title: "Carrie" , labels: [ new Label(name: "c") ])]
			)
		}
		then: 2== f.king.books.size()
		and : 2== f.king.books.find { it.title == "Misery" }.labels.size()
		and : 1== f.king.books.find { it.title == "Carrie" }.labels.size()
	}

	void testAutowiring() {
		given:def f = fixtureLoader.load {
			partner(String, "value")
			am(AutowireMe)
		}
		f.am!=null
		f.am.partner== "value"
	}

	void testTemplating() {
		given:def f = fixtureLoader.load {
			c1(Child, name: "c1") { it.abstract = true }
			u1(Uncle, name: "u1")
			c2(Child, uncle: ref("u1")) { it.parent = c1 }
			c3(Child, uncle: ref("u1")) { it.parent = c1 }
		}

		"c1"== f.c2.name
		"c1" == f.c3.name
	}

	void testIncludes() {
		expect:fixtureLoader.load("includeTest").with {
			u1!=null
		}
	}

	void testPreProcess() {
		given:fixtureLoader.load('preProcess')
		expect:"changed" == preProcessTestService.v
	}

	void testNamed() {
		when:fixtureLoader['test'].load("testFixture1", "testFixture2")
		then:'a'== fixtureLoader['test'].u1.name

		when:fixtureLoader.testClosure.load {
			u(Uncle, name: "u")
		}
		then:'u'==fixtureLoader.testClosure.u.name
	}

	void testRequireGood() {
        when: def fix=fixtureLoader.load "requireTest/good"
		then: fix.goodLoad=='blah'
    }
    void testRequireBad() {
        when:fixtureLoader.load "requireTest/bad"
		then:FixtureException fex = thrown()
			fex.cause.class == UnsatisfiedFixtureRequirementException

	}

	void testRequireBeansGood() {
        expect: fixtureLoader.load "requireBeansTest/good"
    }
    void testRequireBeansBad() {
		when:fixtureLoader.load "requireBeansTest/bad"
        then: FixtureException fex = thrown()
		      fex.cause.class == UnsatisfiedBeanRequirementException
	}

	void testRequireDefinitionsGood() {
        expect:
        fixtureLoader.load "requireDefinitionsTest/good"
    }
    void testRequireDefinitionsBad() {
        when:fixtureLoader.load "requireDefinitionsTest/bad"
        then: FixtureException fex = thrown()
		      fex.cause.class == UnsatisfiedBeanDefinitionRequirementException
	}

	void testInnerLoad() {
		expect:fixtureLoader.load("innerLoadTest/outer").with {
			inner!=null
		}
	}

	void testInnerInlineLoad() {
		expect:fixtureLoader.load("innerLoadTest/inline").with {
			inner!=null
		}
	}

	void testBeanInFixture() {
		expect:fixtureLoader.load("beanTest/outer")
	}

	void testSettingRelationshipsViaReverse() {
		expect:fixtureLoader.load("reverse/children")
	}

	void testSettingRelationshipsViaReverse2() {
		expect:fixtureLoader.load("reverse2")
	}

	void testRetrievingUnknownBeanShouldThrowUnknownFixtureBeanException() {
		shouldFail(UnknownFixtureBeanException) {
			fixtureLoader.load({}).someUnknownThing
		}
	}
}
