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

import com.book.Author
import com.book.Book

class BuildTestDataTests extends GroovyTestCase {

	def fixtureLoader

	void testSimpleInline() {
		fixtureLoader.build {
			u1(Uncle)
			u2(Uncle)
		}
	}

	void testInlineInnerBuildBlock() {
		fixtureLoader.load {
			build {
				u1(Uncle)
				u2(Uncle)
			}
		}
	}

	void testTurnBuildOff() {
		shouldFail {
			fixtureLoader.build {
				noBuild {
					u1(Uncle)
					u2(Uncle)
				}
			}
		}
	}

	void testSetAssociationViaConstructor() {
		fixtureLoader.build {
			b1(Book)
			b2(Book)
			a(Author, books: [b1, b2])
		}.with {
			assertEquals(2, a.books.size())
		}
	}

	void testSetAssociationViaClosure() {
		fixtureLoader.build {
			b1(Book)
			b2(Book)
			a(Author) {
				books = [b1, b2]
			}
		}.with {
			assertEquals(2, a.books.size())
		}
	}

	void testLoadFile() {
		fixtureLoader.load('buildtestdata/test')
	}
}
