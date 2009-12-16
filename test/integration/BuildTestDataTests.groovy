import com.book.*

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
            b1(Book, author: null)
            b2(Book, author: null)
            a(Author, books: [b1, b2])
        }.with {
            assertEquals(2, a.books.size())
        }
    }
    
    void testSetAssociationViaClosure() {
        fixtureLoader.build {
            b1(Book, author: null)
            b2(Book, author: null)
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