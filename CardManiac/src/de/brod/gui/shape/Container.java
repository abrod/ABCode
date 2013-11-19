/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.gui.shape;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.GuiColors;
import de.brod.gui.Texture;

public class Container implements Comparable<Container> {

	private final List<Container> lstChildren = new Vector<Container>();

	protected Texture tex;

	private boolean visible = true;

	private int id, sid;

	private boolean moveable = true;

	private boolean sliding;
	protected boolean moving;

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(Container object) {
		return lstChildren.add(object);
	}

	public void add(int location, Container object) {
		lstChildren.add(location, object);
	}

	/**
	 *
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		lstChildren.clear();
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

	public boolean isMoveable() {
		return moveable;
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean isSliding() {
		return sliding;
	}

	public boolean isVisible() {
		return visible;
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
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return lstChildren.remove(object);
	}

	public boolean resetPosition() {
		return false;
	}

	public void savePosition() {
	}

	public void setColor(float red, float green, float blue, float alpha) {
		for (Container child : lstChildren) {
			child.setColor(red, green, blue, alpha);
		}
	}

	public void setColor(GuiColors pColor) {
		for (Container child : lstChildren) {
			child.setColor(pColor);
		}
	}

	public void setColors(float[] colors) {
		for (Container child : lstChildren) {
			child.setColors(colors);
		}
	}

	public void setId(int pId) {
		id = pId;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setSid(int piSid) {
		sid = piSid;
	}

	public void setSliding(boolean pbSliding) {
		if (this.sliding != pbSliding) {
			this.sliding = pbSliding;
			if (!pbSliding) {
				moving = false;
			}
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size() {
		return lstChildren.size();
	}

	public boolean slide(float f) {
		return false;
	}

	public boolean touches(float eventX, float eventY) {
		return false;
	}

}
