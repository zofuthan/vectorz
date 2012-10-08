package mikera.matrixx;

import static org.junit.Assert.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.transformz.ATransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

import org.junit.Test;

public class TestMatrixx {
	private void doInverseTest(AMatrix m) {
		assert(m.rowCount()==m.columnCount());
		AVector v = Vectorz.createUniformRandomVector(m.rowCount());
		
		AMatrix mi=m.inverse();
		assertEquals(1.0/m.determinant(),mi.determinant(),0.000001);
		
		AVector mv=m.transform(v);
		AVector mimv=mi.transform(mv);
		
		assertTrue(mimv.epsilonEquals(v));		
		
		// composition of matrix and its inverse should be an identity transform
		ATransform id=m.compose(mi);
		AVector idv=id.transform(v);
		assertTrue(idv.epsilonEquals(v));		
	}
	
	@Test
	public void testRotationMatrix() {
		AVector v=Vectorz.createUniformRandomVector(3);	
		double angle=Math.random();
		Matrix33 rot=Matrixx.createRotationMatrix(v, angle);
		
		AVector r=rot.transform(v);
		assertTrue(r instanceof Vector3);
		assertEquals(v.get(0),r.get(0),0.00001);
		assertEquals(v.get(1),r.get(1),0.00001);
		assertEquals(v.get(2),r.get(2),0.00001);
		assertEquals(v.magnitude(),r.magnitude(),0.00001);
		assertTrue(r.epsilonEquals(v));
		
		
	}
	
	@Test
	public void test180RotationMatrix() {
		AVector v=Vector.of(Math.random(),0,0);	
		double angle=Math.PI;
		Matrix33 rot=Matrixx.createYAxisRotationMatrix(angle);
		
		AVector r=rot.transform(v);
		v.negate();
		assertTrue(v.epsilonEquals(r));
	}
	
	@Test
	public void testRandomRotation() {
		AVector v=Vectorz.createUniformRandomVector(3);	
		AVector axis=Vectorz.createUniformRandomVector(3);	
		double angle=Math.random();
		Matrix33 rot=Matrixx.createRotationMatrix(axis, angle);
		
		AVector r=rot.transform(v);
		assertEquals(v.magnitude(),r.magnitude(),0.00001);
		
		Matrix33 inv=rot.inverse();
		AVector ri=inv.transform(r);
		assertTrue(v.epsilonEquals(ri));
	}
	
	@Test
	public void testInverse() {
		doInverseTest(Matrixx.createRandomSquareMatrix(5));
		doInverseTest(Matrixx.createRandomSquareMatrix(4));
		doInverseTest(Matrixx.createRandomSquareMatrix(3));
		doInverseTest(Matrixx.createRandomSquareMatrix(2));
		doInverseTest(Matrixx.createRandomSquareMatrix(1));
	}
	
	
	@Test
	public void testIdentity() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.createLength(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createIdentityMatrix(i);
			
			m.transform(v, tv);
			
			assertTrue(v.epsilonEquals(tv));
		}
	}
	

	@Test
	public void testScale() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.createLength(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createScaleMatrix(i,2.3);
			
			m.transform(v, tv);
			
			assertEquals(v.magnitude()*2.3,tv.magnitude(),0.0001);
		}
	}
	
	@Test
	public void testBasicDeterminant() {
		VectorMatrixMN mmn=new VectorMatrixMN(2,2);
		mmn.getRow(0).set(Vector.of(2,1));
		mmn.getRow(1).set(Vector.of(1,2));
		assertEquals(3.0,mmn.determinant(),0.0);
	}
	
	@Test
	public void testPermuteDeterminant() {
		VectorMatrixMN mmn=new VectorMatrixMN(3,3);
		mmn.set(0,1,1);
		mmn.set(1,0,1);
		mmn.set(2,2,1);
		assertEquals(-1.0,mmn.determinant(),0.0);
	}
	
	@Test
	public void testEquivalentDeterminant() {
		Matrix33 m33=new Matrix33();
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			m33.set(i,j,Math.random());
		}
		
		VectorMatrixMN mmn=new VectorMatrixMN(3,3);
		mmn.set(m33);
		
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			assertEquals(m33.get(i, j),mmn.get(i, j),0.0);
		}
		
		assertEquals(m33.determinant(),mmn.determinant(),0.00001);

	}

	
	@Test
	public void testCompoundTransform() {
		AVector v=Vector.of(1,2,3);
		
		AMatrix m1=Matrixx.createScaleMatrix(3, 2.0);
		AMatrix m2=Matrixx.createScaleMatrix(3, 1.5);
		ATransform ct = m2.compose(m1);
		
		assertTrue(Vector3.of(3,6,9).epsilonEquals(ct.transform(v)));
	}
	
	void doMutationTest(AMatrix m) {
		m=m.clone();
		AMatrix m2=m.clone();
		assertEquals(m,m2);
		int rc=m.rowCount();
		int cc=m.columnCount();
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				m2.set(i,j,m2.get(i,j)+1.3);
				assertEquals(m2.get(i,j),m2.getRow(i).get(j),0.0);
				assertNotSame(m.get(i,j),m2.get(i, j));
			}
		}
	}
	
	private void doSquareTransposeTest(AMatrix m) {
		if (!m.isSquare()) return;
		
		AMatrix m2=m.clone();

		m2.transposeInPlace();
		
		// two different kinds of transpose should produce same result
		AMatrix tm=m.transpose();
		assertEquals(tm,m2);
		
		m2.transposeInPlace();
		assertEquals(m,m2);
	}
	
	private void doSwapTest(AMatrix m) {
		if ((m.rowCount()<2)||(m.columnCount()<2)) return;
		m=m.clone();
		AMatrix m2=m.clone();
		m2.swapRows(0, 1);
		assert(!m2.equals(m));
		m2.swapRows(0, 1);
		assert(m2.equals(m));
		m2.swapColumns(0, 1);
		assert(!m2.equals(m));
		m2.swapColumns(0, 1);
		assert(m2.equals(m));	
	}

	void doRandomTests(AMatrix m) {
		m=m.clone();
		Matrixx.fillRandomValues(m);
		doSwapTest(m);
		doMutationTest(m);
		doSquareTransposeTest(m);
	}

	private void doCloneSafeTest(AMatrix m) {
		if ((m.rowCount()==0)||(m.columnCount()==0)) return;
		AMatrix m2=m.clone();
		m2.set(0,0,Math.PI);
		assertNotSame(m.get(0,0),m2.get(0,0));
	}

	private void doRowColumnTests(AMatrix m) {
		m=m.clone();
		if ((m.rowCount()==0)||(m.columnCount()==0)) return;
		AVector row=m.getRow(0);
		AVector col=m.getColumn(0);
		assertEquals(m.columnCount(),row.length());
		assertEquals(m.rowCount(),col.length());
		
		row.set(0,1.77);
		assertEquals(1.77,m.get(0,0),0.0);
		
		col.set(0,0.23);
		assertEquals(0.23,m.get(0,0),0.0);
		
		AVector all=m.asVector();
		assertEquals(m.rowCount()*m.columnCount(),all.length());
		all.set(0,0.78);
		assertEquals(0.78,row.get(0),0.0);
		assertEquals(0.78,col.get(0),0.0);
		

	}
	
	void doVectorTest(AMatrix m) {
		m=m.clone();
		AVector v=m.asVector();
		assertEquals(v,m.toVector());
		
		AMatrix m2=Matrixx.createFromVector(v, m.rowCount(), m.columnCount());
		
		assertEquals(m,m2);
	}
	
	void doParseTest(AMatrix m) {
		assertEquals(m,Matrixx.parse(m.toString()));
	}
	
	void doGenericTests(AMatrix m) {
		doVectorTest(m);
		doParseTest(m);
		doRowColumnTests(m);
		doCloneSafeTest(m);
		doMutationTest(m);
		doSquareTransposeTest(m);
		doRandomTests(m);
	}
	



	@Test public void genericTests() {
		// specialised 3x3 matrix
		Matrix33 m33=new Matrix33();
		doGenericTests(m33);
		
		// specialised 2*2 matrix
		Matrix22 m22=new Matrix22();
		doGenericTests(m22);
		
		// specialised Mx3 matrix
		VectorMatrixM3 mm3=new VectorMatrixM3(10);
		doGenericTests(mm3);
	
		// general M*N matrix
		VectorMatrixMN mmn=new VectorMatrixMN(6 ,7);
		doGenericTests(mmn);

		// small 2*2 matrix
		mmn=new VectorMatrixMN(2,2);
		doGenericTests(mmn);
		
		// 0x0 matrix should work
		mmn=new VectorMatrixMN(0 ,0);
		doGenericTests(mmn);

		// square M*M matrix
		mmn=new VectorMatrixMN(6 ,6);
		doGenericTests(mmn);

		MatrixMN am1=new MatrixMN(m33);
		doGenericTests(am1);
		
		MatrixMN am2=new MatrixMN(mmn);
		doGenericTests(am2);

		
	}
}
