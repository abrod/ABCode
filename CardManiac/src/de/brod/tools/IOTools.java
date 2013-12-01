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
package de.brod.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class IOTools {

	public static void writeBitmap(Bitmap bitmap, Context activity,
			String sFileName) throws IOException {

		// <uses-permission
		// android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
		File file= new File("/mnt/sdcard", sFileName);

		OutputStream fOut = new FileOutputStream(file);

		bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
		fOut.flush();
		fOut.close();
		// publish this file
		MediaStore.Images.Media.insertImage(activity.getContentResolver(),
				file.getAbsolutePath(), file.getName(), file.getName());

	}

	public static void write(File pFile, byte[] bytes) throws IOException {
		FileOutputStream out = new FileOutputStream(pFile);
		out.write(bytes);
		out.close();
	}

	public static byte[] read(File pFile) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				pFile));
		copyStream(in, out);
		in.close();
		return out.toByteArray();
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[4096];
		int count;
		while ((count = in.read(b)) > 0) {
			out.write(b, 0, count);
		}
	}

}
