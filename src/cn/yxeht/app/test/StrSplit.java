package cn.yxeht.app.test;

import java.net.URLDecoder;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yxeht.app.constants.AmazonCfgInfo;

public class StrSplit {

	public static void main(String[] args) {
		String url = "https://www.amazon.de/?tag=55haistaocn-21";
//		System.out.println(url.substring(url.lastIndexOf("?"), url.length()));
		url = "www.linkhaitao.com/index.php?mod=lhdeal&track=1a79oYhYaFE3_a_b3LdL_bo1OzE4XPBj3OpURqOE6ouY5ClKVlWoLmkKYBp&new=http%3A%2F%2Fwww.gnc.com&tag=mutou";
		System.out.println(url.substring(url.lastIndexOf("http")));
		
		url = "zh.ashford.com/asasd";
		url = "http://www.beautybay.com/bathandbody/argentum/lalotioninfinie/&u1=4ww3oz";
		System.out.println(getHost(url));
		System.out.println(url.substring(0, url.lastIndexOf("&")));
		
//		String[] aa = url.split("http");
//		System.out.println(aa.length);
//		for(int i = 0; i < aa.length; i++){
//			System.out.println("["+i+"]:"+aa[i]);
//			if(i == aa.length - 1){
//				String str = aa[i].substring(aa[i].lastIndexOf("&"));
//				System.err.println(str);
//			}
//		}
	}

	/**
	 * 修改字符串中的unicode码
	 * 
	 * @param s
	 *            源str
	 * @return 修改后的str
	 */
	public static String decode2(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '\\' && chars[i + 1] == 'u') {
				char cc = 0;
				for (int j = 0; j < 4; j++) {
					char ch = Character.toLowerCase(chars[i + 2 + j]);
					if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
						cc |= (Character.digit(ch, 16) << (3 - j) * 4);
					} else {
						cc = 0;
						break;
					}
				}
				if (cc > 0) {
					i += 5;
					sb.append(cc);
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 获取修复后的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String getFixStr(String str) {
		String ret = str;
		Pattern p = Pattern.compile("(\\\\u.{4})");
		Matcher m = p.matcher(ret);
		while (m.find()) {
			String xxx = m.group(0);
			ret = str.replaceAll("\\" + xxx, decode2(xxx));
		}
		return ret;
	}
	
	public static String getHost(String url) {
		if (url == null || url.trim().equals("")) {
			return "";
		}
		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+(:\\d*)?");
		Matcher matcher = p.matcher(url);
		if (matcher.find()) {
			host = matcher.group();
		}
		return host;
	}

}
