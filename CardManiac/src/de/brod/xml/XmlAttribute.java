package de.brod.xml;


public class XmlAttribute {
	private String sAName, sAValue;

	public XmlAttribute(String psName, String psValue) {
		sAName = psName;
		setValue(psValue);
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

	public boolean equalsName(String psAttributeName) {
		return psAttributeName.equals(sAName);
	}

	public String getValue() {
		return sAValue;
	}

	public void setValue(String psAttributeValue) {
		sAValue = psAttributeValue;
	}

}
