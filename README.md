# android调用libjpeg与libpng的简单实例

实现jpeg的压缩
与png转jpeg并压缩的功能

有关升级libjpeg与libpng参考我的博客：
http://blog.csdn.net/talkxin

*新增协议修改，参照微信图片分辨率
*升级libjpeg库增加对jpeg图片的压缩速度参数
*新增ssl库

jpegthumbnail.c中包含了图片压缩的具体规则：
1、大图压缩规则以720p的缩放准则进行压缩，质量为65
2、缩略图压缩规则以480p的缩放准则进行压缩，质量为65
3、微博长途的压缩规则以长宽比超过1:4来进行排重

其中Java_com_allstar_cinclient_tools_image_ImageNativeUtil_zoomcompress方法为入口方法：
在android项目需要对应包名修改其对应的路径

编译：
进入jni目录，使用"android-ndk-r10e"进行 ndk-build编译生成两个so文件，其中libjpegcompressjni.so是基于图片压缩能力的so文件
（libdescode是基于3des加密的扩展包，可以在Android.MK中将其注释）

在IOS项目中引用说明在补充中。。。