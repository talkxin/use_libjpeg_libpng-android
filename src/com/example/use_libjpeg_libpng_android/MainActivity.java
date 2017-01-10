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
                    String file = "/sdcard/test/1111.jpg";
                    Log.i("liuxin", "start");
                    ImageTools.saveCommpressImage(file, Quality.BIG, "/sdcard/test/yasuoBIG.jpg");
                    Log.i("liuxin", "end");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }
}