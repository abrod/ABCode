package de.brod.carddealer;

import android.util.Log;

public class Util {

	public static final boolean DEBUG = true;
	public static final String LOG_TAG = "CardDealer";

	private static String getString(float f) {
		String valueOf = String.valueOf(Math.round(f * 100) / 100f);
		if (!valueOf.startsWith("-")) {
			valueOf = " " + valueOf;
		}
		while (valueOf.length() < 5) {
			valueOf += "0";
		}
		return valueOf;
	}

	static void print(String string, float[] mViewMatrix2) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mViewMatrix2.length; i++) {
			sb.append(getString(mViewMatrix2[i]));
			if (i % 4 == 3) {
				sb.append("\n");
			} else {
				sb.append(", ");
			}
		}
		Log.d(string, sb.toString());
	}

}
