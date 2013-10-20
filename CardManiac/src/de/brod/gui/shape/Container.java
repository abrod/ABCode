package de.brod.gui.shape;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.Texture;

public abstract class Container implements Comparable<Container> {

	private final List<Container> lstChildren = new Vector<Container>();

	protected Texture tex;

	private boolean visible = true;

	public void add(int location, Container object) {
		lstChildren.add(location, object);
	}

	@Override
	public int compareTo(Container another) {
		if (sliding != another.sliding) {
			if (!another.sliding) {
				return 1;
			}
			return -1;
		}
		if (moving != another.moving) {
			if (!another.moving) {
				return 1;
			}
			return -1;
		}
		if (isMoveable() != another.isMoveable()) {
			if (!another.isMoveable()) {
				return 1;
			}
			return -1;
		}

		int c = sid - another.sid;
		if (c == 0) {
			c = id - another.id;
		}
		return c;
	}

	private int id, sid;
	private boolean moveable = true;
	private boolean sliding;

	public void setSid(int piSid) {
		sid = piSid;
	}

	public void setId(int pId) {
		id = pId;
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

	public abstract void savePosition();

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	protected boolean moving;

	public abstract boolean slide(float f);

	public abstract boolean resetPosition();

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public boolean isSliding() {
		return sliding;
	}

	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
