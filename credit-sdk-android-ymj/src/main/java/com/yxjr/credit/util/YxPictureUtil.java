package com.yxjr.credit.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

/**
 * All rights Reserved, Designed By ClareShaw
 * 
 * @公司:益芯金融
 * @作者:xiaochangyou
 * @版本:V1.0
 * @创建时间:2016-8-1 下午3:10:35
 * @描述:TODO[图片处理]
 */
public class YxPictureUtil {

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:10:53
	 * @描述:TODO[把bitmap转换成String]
	 * @param filePath
	 *            文件路径
	 * @return String
	 */
	public static String bitmapToString(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);

	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:11:28
	 * @描述:TODO[计算图片的缩放值]
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return int
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:11:59
	 * @描述:TODO[根据路径获得突破并压缩返回bitmap用于显示]
	 * @param filePath
	 * @return Bitmap
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		//如果我们把它设为true，那么BitmapFactory.decodeFile(String path, Options opt)并不会真的返回一个Bitmap给你，仅仅把宽、高取回来
		//这样不会占用太多的内存，也就不会频繁的发生OOM
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:12:10
	 * @描述:TODO[根据路径删除图片]
	 * @param path
	 *            void
	 */
	public static void deleteTempFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:12:16
	 * @描述:TODO[添加到图库]
	 * @param context
	 * @param path
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:12:30
	 * @描述:TODO[获取保存图片的目录]
	 * @return File
	 */
	public static File getAlbumDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getAlbumName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * @作者:xiaochangyou
	 * @创建时间:2016-8-1 下午3:12:39
	 * @描述:TODO[获取保存 隐患检查的图片文件夹名称]
	 * @return String
	 */
	public static String getAlbumName() {
		return "temp";
	}
}
