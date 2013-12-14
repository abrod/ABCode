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

import de.brod.cm.Card.Colors;
import de.brod.gui.GuiColors;
import de.brod.gui.shape.Sprite;

public interface ICard {

	Hand getHand();

	void setId(int pId);

	void setRotation(float angle);

	void setCovered(boolean b);

	void setHand(Hand hand);

	void moveTo(Hand h);

	Card.Values getValue();

	Colors getColor();

	void setColor(GuiColors iTEM_RED);

	int getValueId();

	boolean isCovered();

	void setPosition(float x, float y);

	void addTo(Sprite sprite);
}
