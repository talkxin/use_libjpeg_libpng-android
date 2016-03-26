package com.example.testjpg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 操作图片的工具类
 * 
 * @author young
 *
 */
public class ImageTools {
	// 临时文件夹
	private final static String temp = "/sdcard/icbc/temp/";

	private static final long DEFAULT_MAX_BM_SIZE = 1000 * 250;

	/**
	 * 图片质量
	 * 
	 * @author young
	 *
	 */
	public enum Quality {
		BIG(1), THUM(2), PORTRAIT(3), SMALL(4);

		private int q;

		private Quality(int q) {
			this.q = q;
		}

		public int getQuality() {
			return q;
		}
	}

	/**
	 * 压缩图片获取BitMap数据
	 * 
	 * @param oldimage
	 *            原图
	 * @param q
	 *            图片质量枚举
	 * @return
	 */
	public static Bitmap getCommpressImage(Bitmap oldimage, Quality q) {
		Bitmap bit = imageRule(oldimage, q);
		File dirFile = new File(temp);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
		NativeUtil.compressBitmap(bit, q.getQuality(), jpegTrueFile.getAbsolutePath(), true, 5);
		Bitmap nImage = BitmapFactory.decodeFile(jpegTrueFile.getAbsolutePath());
		jpegTrueFile.delete();
		return nImage;
	}

	/**
	 * 压缩图片获取字节流
	 * 
	 * @param oldimage
	 * @param q
	 * @return
	 */
	public static byte[] getCommpressImage2Byte(Bitmap oldimage, Quality q) {
		Bitmap bit = imageRule(oldimage, q);
		File dirFile = new File(temp);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
		NativeUtil.compressBitmap(bit, q.getQuality(), jpegTrueFile.getAbsolutePath(), true, 5);
		byte[] newf = getBytesFromFile(jpegTrueFile);
		jpegTrueFile.delete();

		if (oldimage != null && !oldimage.isRecycled()) {
			oldimage.recycle();
			oldimage = null;
		}
		if (bit != null && !bit.isRecycled()) {
			bit.recycle();
			bit = null;
		}
		return newf;
	}

	/**
	 * 存储压缩图片
	 * 
	 * @param oldimage
	 *            原图
	 * @param q
	 *            图片质量枚举
	 * @param filename
	 *            存储文件名
	 * @return
	 */
	public static void saveCommpressImage(Bitmap oldimage, Quality q, String filename) {
		Bitmap bit = imageRule(oldimage, q);
		NativeUtil.compressBitmap(bit, q.getQuality(), filename, true, 5);
	}

	/**
	 * 压缩图片获取字节流
	 * 
	 * @param oldimage
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static byte[] getCommpressImage2Byte(Uri uri, Context context, Quality q) throws Exception {
		System.gc();
		Bitmap bit = getCompressedImage(context, uri, 0);
		if (bit == null) {
			throw new Exception("bit is null");
		}
		byte[] r = getCommpressImage2Byte(bit, q);
		System.gc();
		return r;
	}

	/**
	 * 压缩图片获取字节流
	 * 
	 * @param oldimage
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static byte[] getCommpressImage2Byte(String filePath, Quality q) throws Exception {
		Bitmap bit = BitmapFactory.decodeFile(filePath);
		if (bit == null) {
			throw new Exception("bit is null");
		}
		return getCommpressImage2Byte(bit, q);
	}

	/**
	 * 压缩图片获取BitMap数据
	 * 
	 * @param uri
	 * @param context
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getCommpressImage(Uri uri, Context context, Quality q) throws Exception {
		Bitmap bit = getCompressedImage(context, uri, 0);
		if (bit == null) {
			throw new Exception("bit is null");
		}
		return getCommpressImage(bit, q);
	}

	/**
	 * 存储压缩图片
	 * 
	 * @param uri
	 * @param context
	 * @param q
	 * @param filename
	 * @throws Exception
	 */
	public static void saveCommpressImage(Uri uri, Context context, Quality q, String filename) throws Exception {
		Bitmap bit = getCompressedImage(context, uri, 0);
		if (bit == null) {
			throw new Exception("bit is null");
		}
		saveCommpressImage(bit, q, filename);
	}

	/**
	 * 存储压缩图片
	 * 
	 * @param uri
	 * @param context
	 * @param q
	 * @param filename
	 * @throws Exception
	 */
	public static void saveCommpressImagegetCommpressImage2Byte(String inPath, Quality q, String outPath)
			throws Exception {
		Bitmap bit = BitmapFactory.decodeFile(inPath);
		if (bit == null) {
			throw new Exception("bit is null");
		}
		saveCommpressImage(bit, q, outPath);
	}

	/**
	 * 图片缩放规则
	 * 
	 * @param oldimage
	 * @return
	 */
	private static Bitmap imageRule(Bitmap oldimage, Quality q) {
		// 获取这个图片的宽和高
		float width = oldimage.getWidth();
		float height = oldimage.getHeight();
		switch (q) {
		case BIG:
			// 16:9
			if (width / 16 == height / 9) {
				return zoomImage(oldimage, 1280, 720);
			} else if (height / 16 == width / 9) {
				return zoomImage(oldimage, 720, 1280);
			}
			// 16:10
			else if (width / 16 == height / 10) {
				return zoomImage(oldimage, 1280, 800);
			} else if (height / 16 == width / 10) {
				return zoomImage(oldimage, 800, 1280);
			}
			// 4:3
			else if (width / 4 == height / 3) {
				return zoomImage(oldimage, 1280, 960);
			} else if (height / 4 == width / 3) {
				return zoomImage(oldimage, 960, 1280);
			}
			break;

		case THUM:
			// 16:9
			if (width / 16 == height / 9) {
				return zoomImage(oldimage, 640, 360);
			} else if (height / 16 == width / 9) {
				return zoomImage(oldimage, 360, 640);
			}
			// 16:10
			else if (width / 16 == height / 10) {
				return zoomImage(oldimage, 640, 400);
			} else if (height / 16 == width / 10) {
				return zoomImage(oldimage, 400, 640);
			}
			// 4:3
			else if (width / 4 == height / 3) {
				return zoomImage(oldimage, 640, 480);
			} else if (height / 4 == width / 3) {
				return zoomImage(oldimage, 480, 640);
			}
			break;
		case PORTRAIT:
			return oldimage;
		case SMALL:
			return zoomImage(oldimage, 115, 115);
		}

		return oldimage;
	}

	/**
	 * 按比例缩放图片
	 * 
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 文件转化为字节数组
	 * 
	 * @param file
	 * @return
	 */
	private static byte[] getBytesFromFile(File file) {
		byte[] ret = null;
		try {
			if (file == null) {
				// log.error("helper:the file is null!");
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] b = new byte[4096];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			in.close();
			out.close();
			ret = out.toByteArray();
		} catch (IOException e) {
			// log.error("helper:get bytes from file process error!");
			e.printStackTrace();
		}
		return ret;
	}

	private static synchronized Bitmap getCompressedImage(Context context, Uri imageUri, int maxPixels) {
		System.gc();
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {
			final int IMAGE_MAX_SIZE = (maxPixels == 0) ? 1000000 : maxPixels;

			String uriContent = imageUri.toString();
			Uri uri = null;
			if (uriContent.indexOf("://") <= 0) {
				uri = Uri.fromFile(new File(uriContent));
			} else {
				uri = imageUri;
			}
			in = context.getContentResolver().openInputStream(uri);
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();
			int scale = 1;

			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}

			Bitmap b = null;
			in = context.getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields
				// an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);
				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				double x = (y / height) * width;
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				// b.recycle();
				b = scaledBitmap;
			} else {
				o = new BitmapFactory.Options();
				o.inSampleSize = 1;
				b = BitmapFactory.decodeStream(in, null, o);
				System.gc();
			}
			in.close();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
