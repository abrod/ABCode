package de.brod.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class IOTools {

	public static void writeBitmap(Bitmap bitmap, Context activity,
			String sFileName) {

		//     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

		OutputStream fOut = null;

		File file = new File("/mnt/sdcard", sFileName);
		try {
			fOut = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
			fOut.flush();
			fOut.close();

			MediaStore.Images.Media.insertImage(activity.getContentResolver(),
					file.getAbsolutePath(), file.getName(), file.getName());
			System.out.println("Save File " + file.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
