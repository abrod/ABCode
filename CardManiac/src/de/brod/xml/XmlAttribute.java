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
package de.brod.xml;

public class XmlAttribute {
	private String sAName, sAValue;

	public XmlAttribute(String psName, String psValue) {
		sAName = psName;
		setValue(psValue);
	}

	public boolean equalsName(String psAttributeName) {
		return psAttributeName.equals(sAName);
	}

	public String getValue() {
		return sAValue;
	}

	public boolean setValue(String psAttributeValue) {
		if (sAValue == null || !sAValue.equals(psAttributeValue)) {
			sAValue = psAttributeValue;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sAName);
		sb.append("=\"");
		sb.append(sAValue);
		sb.append("\"");
		return sb.toString();
	}

}
