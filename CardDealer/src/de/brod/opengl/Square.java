package de.brod.opengl;

public class Square extends Mesh {

	// default order for quares.
	private final short[] indices = { 0, 1, 2, 0, 2, 3 };

	public Square(float width, float height) {

		float wd = width / 2;
		float hg = height / 2;

		float vertices[] = { -wd, hg, 0.0f, // 0, Top Left
				-wd, -hg, 0.0f, // 1, Bottom Left
				wd, -hg, 0.0f, // 2, Bottom Right
				wd, hg, 0.0f, // 3, Top Right
		};

		setVertices(vertices);
		setIndices(indices);
		setColors(1, 1, 0, 1, //
				0, 1, 0, 1, //
				0, 0, 1, 1, //
				1, 0, 0, 1//
		);
	}

}
