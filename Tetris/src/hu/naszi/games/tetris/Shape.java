package hu.naszi.games.tetris;

public class Shape {
	enum Tetrominoes {
		NOSHAPE, ZSHAPE, SSHAPE, LINESHAPE, TSHAPE, SQUARESHAPE, LSHAPE, MIRROREDLSHAPE
	};
	
	private Tetrominoes pieceShape;
	private int coords[][];
	private int [][][] coordsTable;
	
	public Shape() {
		coords = new int[4][2];
		setShape(Tetrominoes.NOSHAPE);
	}

	private void setShape(Tetrominoes shape) {
		coordsTable = new int[][][] {
				{{0, 0}, {0, 0}, {0, 0}, {0, 0}},
				{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
				{{0, -1}, {0, 0}, {1, 0}, {1, 1}},
				{{0, -1}, {0, 0}, {0, 1}, {0, 2}},
				{{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
				{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
				{{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
				{{1, -1}, {0, -1}, {0, 0}, {0, 1}},
		};
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				coords[i][j] = coordsTable[shape.ordinal()][i][j];
			}
		}
		pieceShape = shape;
	}

}
