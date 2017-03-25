import test.Face
import test.Nose
import test.Eye



fixture {
	f1(Face)
	n1(Nose, face: f1)
	e1(Eye, face: f1)
	e2(Eye, face: f1)
}
