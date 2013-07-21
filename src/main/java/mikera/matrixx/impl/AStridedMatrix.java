package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;

public abstract class AStridedMatrix extends AArrayMatrix {

	protected AStridedMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}

	public abstract int getArrayOffset();

	public abstract int rowStride();
	
	public abstract int columnStride();	
	
	@Override
	public AStridedMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rowStart<0)||(rowStart>=rows)||(colStart<0)||(colStart>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart,colStart));
		if ((rowStart+rows>this.rows)||(colStart+cols>this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart+rows,colStart+cols));
		if ((rows<1)||(cols<1)) throw new IllegalArgumentException(ErrorMessages.illegalSize(rows,cols));
		return StridedMatrix.wrap(data, rows, cols, 
				getArrayOffset()+rowStart*rowStride()+colStart*columnStride(), this.rowStride(), this.columnStride());
	}
	
	@Override
	public AVector getRow(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*rowStride(), cols, columnStride());
	}
	
	@Override
	public AVector getColumn(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*columnStride(), rows, rowStride());
	}
}