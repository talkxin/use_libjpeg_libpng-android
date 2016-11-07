package com.example.use_libjpeg_libpng_android;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.allstar.cinclient.tools.image.ImageNativeUtil;
import com.allstar.cinclient.tools.image.ImageTools;
import com.allstar.cinclient.tools.image.ImageTools.Quality;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testJpeg();
    }

    public synchronized static byte[] getCompressedRebuildImage(Context context, Uri imageUri, int qul, int widthLimit, int heightLimit) {
        byte[] ret = null;
        InputStream in = null;
        ByteArrayOutputStream os = null;
        try {

            String uriContent = imageUri.toString();
            Uri uri = null;
            if (uriContent.indexOf("://") <= 0) {
                uri = Uri.fromFile(new File(uriContent));
            } else {
                uri = imageUri;
            }
            in = context.getContentResolver().openInputStream(uri);
            // Decode image size
            Bitmap b = BitmapFactory.decodeStream(in, null, null);
            in.close();

            // resize to desired dimensions
            int outHeight = b.getHeight();
            int outWidth = b.getWidth();
            if (!(outWidth < widthLimit && outHeight < heightLimit)) {
                float sx = new BigDecimal(widthLimit).divide(new BigDecimal(outWidth), 4, BigDecimal.ROUND_DOWN).floatValue();
                float sy = new BigDecimal(heightLimit).divide(new BigDecimal(outHeight), 4, BigDecimal.ROUND_DOWN).floatValue();
                sx = (sx < sy ? sx : sy);
                sy = sx;
                Matrix matrix = new Matrix();
                matrix.postScale(sx, sy);
                b = Bitmap.createBitmap(b, 0, 0, outWidth, outHeight, matrix, true);
            }
            //b.recycle();
            os = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, qul, os);
            if (!b.isRecycled()) {
                b.recycle();
                b = null;
            }
            ret = os.toByteArray();
            return ret;
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

            private Selector selector;
            private SocketChannel Channel;
            private InetSocketAddress _host;

            public void run() {
                try {
                    String file = "/sdcard/test/yasuoBIGx.jpg";
                    Log.i("liuxin", "start");
                    ImageTools.saveCommpressImage(file, Quality.BIG, "/sdcard/test/yasuoBIG.jpg");
                    Log.i("liuxin", "end");
                    getFileFromBytes(getCompressedRebuildImage(MainActivity.this, Uri.fromFile(new File(file)), 65, 1280, 720), "/sdcard/test/yasuoBIG1.jpg");
                    Log.i("liuxin", "end");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }
}