package de.brod.gui.shape;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.Texture;

public class Container {

	private final List<Container> lstChildren = new Vector<Container>();

	protected Texture tex;

	public void add(int location, Container object) {
		lstChildren.add(location, object);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public Container remove(int location) {
		return lstChildren.remove(location);
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size() {
		return lstChildren.size();
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return lstChildren.remove(object);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(Container object) {
		return lstChildren.add(object);
	}

	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		lstChildren.clear();
	}

	public void draw(GL10 gl) {
		// ... end new part.
		// draw the children
		for (Container child : lstChildren) {
			child.draw(gl);
		}
	}

	public Container get(int location) {
		return lstChildren.get(location);
	}

	protected void getAllContainers(List<Container> plstContainers) {
		for (Container child : lstChildren) {
			if (child.tex != null) {
				plstContainers.add(child);
			}
			child.getAllContainers(plstContainers);
		}
	}

	public List<Container> getChildren() {
		return lstChildren;
	}

	public void setColor(float red, float green, float blue, float alpha) {
		for (Container child : lstChildren) {
			child.setColor(red, green, blue, alpha);
		}
	}

	public void setColors(float[] colors) {
		for (Container child : lstChildren) {
			child.setColors(colors);
		}
	}

	public boolean touches(float eventX, float eventY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void getSprites(List<Sprite> lstSprites) {
		for (Container cont : lstChildren) {
			if (cont instanceof Sprite) {
				lstSprites.add((Sprite) cont);
			}
			cont.getSprites(lstSprites);
		}
	}
}
