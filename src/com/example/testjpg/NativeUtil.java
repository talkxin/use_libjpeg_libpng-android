package com.example.testjpg;
/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.example.testjpg.ImageTools.Quality;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class NativeUtil {
	private static int DEFAULT_QUALITY = 95;

	public static void compressBitmap(Bitmap bit, String fileName, boolean optimize, int level) {
		compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize, level);

	}

	public static void compressBitmap(Bitmap bit, int quality, String fileName, boolean optimize, int level) {
		Log.d("native", "compress of native");
		if (bit.getConfig() != Config.ARGB_8888) {
			Bitmap result = null;

			result = Bitmap.createBitmap(bit.getWidth() / level, bit.getHeight() / level, Config.ARGB_8888);
			Canvas canvas = new Canvas(result);
			Rect rect = new Rect(0, 0, bit.getWidth() / level, bit.getHeight() / level);
			canvas.drawBitmap(bit, null, rect, null);
			saveBitmap(result, quality, fileName, optimize);
			result.recycle();
		} else {
			saveBitmap(bit, quality, fileName, optimize);
		}

	}

	public static long compressBitmap(String input, String output, Quality q) {
		return compressBitmap2(input.getBytes(), output.getBytes(), true, q.getQuality());
	}

	private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
	}

	private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes,
			boolean optimize);

	private static native long compressBitmap2(byte[] input, byte[] output, boolean optimize, int quality);

	static {
		// System.loadLibrary("jpegcompress");
		System.loadLibrary("jpegcompressjni");

	}

}
