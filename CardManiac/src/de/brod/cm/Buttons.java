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
package de.brod.cm;

import java.util.ArrayList;
import java.util.List;

import de.brod.gui.shape.Button;
import de.brod.gui.shape.Sprite;

public class Buttons extends CardContainer {

	private List<Button> lstButtons = new ArrayList<Button>();

	public Buttons(int piId) {
		super(piId, -1, -1, 1, 1);
	}

	public void add(Button button) {
		lstButtons.add(button);
	}

	@Override
	public void addAllSpritesTo(Sprite sprite) {
		for (Button c : lstButtons) {
			sprite.add(c);
		}
		super.addAllSpritesTo(sprite);
	}

	@Override
	public void clear() {
		// make nothing
	}

	@Override
	public String getName() {
		return "Buttons";
	}

	@Override
	public void organize() {
		// make nothing
	}

}
