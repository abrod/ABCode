package de.brod.opengl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.microedition.khronos.opengles.GL10;

public class Shapes extends ArrayList<Shape> {

	private static final Comparator<? super Shape>	compareShape		= new Comparator<Shape>() {
																			@Override
																			public int compare(Shape lhs, Shape rhs) {
																				float diff = rhs.getPosition()
																						- lhs.getPosition();
																				if (diff < 0) {
																					return -1;
																				} else if (diff > 0) {
																					return 1;
																				}
																				return 0;
																			}
																		};

	/**
	 *
	 */
	private static final long						serialVersionUID	= 8434418783314671925L;

	public void draw(GL10 gl) {
		for (Shape mesh : this) {
			mesh.draw(gl);
		}
	}

	public void sort() {
		Collections.sort(this, compareShape);
	}
}
