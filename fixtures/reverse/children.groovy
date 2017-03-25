import test.Nose
import test.Eye

load "reverse/face"

fixture {
	n1(Nose, face: f1)
	e1(Eye, face: f1)
	e2(Eye, face: f1)
}
