package cn.yxeht.app.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

public class TextUtil {
	
	private static Logger log = Logger.getLogger(TextUtil.class);
	
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String checkMerchantUrl(String urlUncheck) {

		if (TextUtil.isEmpty(urlUncheck)) {
			return urlUncheck;
		}

		String checkResult = urlUncheck.trim();

		if (urlUncheck.startsWith("http://")) {
			checkResult = checkResult.replaceFirst("http://", "");
		}

		if (urlUncheck.startsWith("https://")) {
			checkResult = checkResult.replaceFirst("https://", "");
		}

		if (urlUncheck.endsWith("/")) {
			checkResult = checkResult.substring(0, checkResult.lastIndexOf("/"));
		}

		return checkResult;
	}

	public static String[] replaceFirstHttpInUrl(String url) {

		String afterReplace = url.trim();
		String replacement = "http://";

		if (url.startsWith("http://")) {
			afterReplace = afterReplace.replaceFirst("http://", "");
			replacement = "http://";
		} else if (url.startsWith("https://")) {
			afterReplace = afterReplace.replaceFirst("https://", "");
			replacement = "https://";
		}

		String[] arr = new String[2];
		arr[0] = replacement;
		arr[1] = afterReplace;

		return arr;

	}
	
	public static String[] replaceLastHttpInUrl(String url){
		String afterReplace = url.trim();
		String replacement = "http://";

		if (url.contains("http://")) {
			afterReplace = afterReplace.substring("http://".length());
			replacement = "http://";
		} else if (url.startsWith("https://")) {
			afterReplace = afterReplace.substring("https://".length());
			replacement = "https://";
		}

		String[] arr = new String[2];
		arr[0] = replacement;
		arr[1] = afterReplace;

		return arr;
	}

	public static String readTxtFromFile(String filePath) {

		String result = "";

		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				StringBuilder sb = new StringBuilder();
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				result = sb.toString();
				bufferedReader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
		}

		return result;

	}

	public static boolean writeTxtFile(String content, File fileName) throws Exception {
		RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream o = null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("utf-8"));
			o.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
		} finally {
			if (mm != null) {
				mm.close();
			}
		}
		return flag;
	}

}
