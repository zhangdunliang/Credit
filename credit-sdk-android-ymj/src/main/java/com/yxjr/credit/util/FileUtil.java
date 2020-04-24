package com.yxjr.credit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

public class FileUtil {

	public static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yxjrCredit/";
	public static String PICTURES_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/yxjrCredit/";

	public static Bitmap saveBase64ToFile(String base64) {
		//将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(base64, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static String saveBitmap2Jpg(String fileName, Bitmap bitmap) {
		FileOutputStream out = null;
		try {
			File file = new File(FileUtil.PICTURES_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(PICTURES_PATH + fileName + ".jpg");
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
