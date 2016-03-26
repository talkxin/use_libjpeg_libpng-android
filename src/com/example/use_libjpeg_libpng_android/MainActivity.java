package com.example.use_libjpeg_libpng_android;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.testjpg.ImageTools.Quality;
import com.example.testjpg.NativeUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		testJpeg();
	}

	private void testJpeg() {
		new Thread(new Runnable() {
			/**
			 * 把字节数组保存为一个文件
			 * 
			 * @param b
			 * @param outputFile
			 * @return
			 */
			public File getFileFromBytes(byte[] b, String outputFile) {
				File ret = null;
				BufferedOutputStream stream = null;
				try {
					ret = new File(outputFile);
					FileOutputStream fstream = new FileOutputStream(ret);
					stream = new BufferedOutputStream(fstream);
					stream.write(b);
				} catch (Exception e) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							// log.error("helper:get file from byte process
							// error!");
							e.printStackTrace();
						}
					}
				}
				return ret;
			}

			public void run() {
				try {
					String file = "/sdcard/test/test.JPG";
					Log.i("test", NativeUtil.compressBitmap(file, "/sdcard/test/yasuoBIG.jpg", Quality.BIG) + "");
					Log.i("test", NativeUtil.compressBitmap(file, "/sdcard/test/yasuoTHUM.jpg", Quality.THUM) + "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}
}