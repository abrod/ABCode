package de.brod.opengl;

public class Rectangle extends ShapeBase {

	// default order for quares.
	private final short[] indices = { 0, 1, 2, 0, 2, 3 };

	public Rectangle(float width, float height) {

		float wd = width / 2;
		float hg = height / 2;

		float vertices[] = { -wd, hg, 0.0f, // 0, Top Left
				-wd, -hg, 0.0f, // 1, Bottom Left
				wd, -hg, 0.0f, // 2, Bottom Right
				wd, hg, 0.0f, // 3, Top Right
		};

		setVertices(vertices);
		setIndices(indices);

	}

	public Rectangle(float width, float height, float x, float y, float z) {
		this(width, height);
		setPosition(x, y, z);
	}

	@Override
	protected float[] getTextureCoords(float xMin, float yMin, float xMax, float yMax) {
		// TODO Auto-generated method stub
		float textureCoordinates[] = { xMin, yMin, //
				xMin, yMax, //
				xMax, yMax, //
				xMax, yMin //
		};
		return textureCoordinates;
	}

}
