package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;
import mikera.vectorz.util.VectorzException;

/**
 * Indexed sparse vector. Mutable only in the elements included in the index.
 * 
 * Index must be distinct and sorted.
 * 
 * @author Mike
 *
 */
public class SparseIndexedVector extends ASparseVector {
	private static final long serialVersionUID = 750093598603613879L;

	private final int length;
	public final Index index;
	public final double[] data;
	
	
	private SparseIndexedVector(int length, Index index) {
		this(length,index,new double[index.length()]);
	}
	
	private SparseIndexedVector(int length, Index index, double[] data) {
		this.length=length;
		this.index=index;
		this.data=data;
	}
	
	private SparseIndexedVector(int length, Index index, AVector data) {
		this.length=length;
		this.index=index;
		this.data=new double[index.length()];
		data.copyTo(this.data, 0);
	}
	
	/**
	 * Creates a SparseIndexedVector with the specified index and data values.
	 * Performs no checking - Index must be distinct and sorted.
	 */
	public static SparseIndexedVector wrap(int length, Index index, double[] data) {
		assert(index.length()==data.length);
		assert(index.isDistinctSorted());
		return new SparseIndexedVector(length, index,data);
	}
	
	public static SparseIndexedVector create(int length, Index index, double[] data) {
		if (!index.isDistinctSorted()) {
			throw new VectorzException("Index must be sorted and distinct");
		}
		if (!(index.length()==data.length)) {
			throw new VectorzException("Length of index: mismatch woth data");			
		}
		return new SparseIndexedVector(length, index,data);
	}
	
	public static SparseIndexedVector create(int length, Index index, AVector data) {
		SparseIndexedVector sv= create(length, index, new double[index.length()]);
		data.copyTo(sv.data, 0);
		return sv;
	}
	
	@Override
	public int nonSparseElementCount() {
		return data.length;
	}

	
	@Override
	public int length() {
		return length;
	}
	
	/**
	 * Looks up an index in the index vector, returning its position or -1 if not found
	 * @param i
	 * @return
	 */
	public int indexPosition(int i) {
		int min=0; int max=data.length;
		while (min<max) {
			int mid=(min+max)>>1;
			int mi=index.data[mid];
			if (i==mi) return mid;
			if (i<mi) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return -1;
	}
	
	@Override
	public double get(int i) {
		int ip=indexPosition(i);
		if (ip<0) return 0.0;
		return data[ip];
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			result+=data[i];
		}
		return result;
	}
	
	
	@Override
	public double dotProduct(AVector v) {
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			result+=data[i]*v.get(index.data[i]);
		}
		return result;
	}
	
	public double dotProduct(ArrayVector v) {
		double[] array=v.getArray();
		int offset=v.getArrayOffset();
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			result+=data[i]*array[offset+index.data[i]];
		}
		return result;
	}
	
	@Override
	public void set(int i, double value) {
		int ip=indexPosition(i);
		if (ip<0) {
			throw new VectorzException("Can't set SparseIndexedVector at non-indexed position: "+i);
		}
		data[ip]=value;
	}

}
