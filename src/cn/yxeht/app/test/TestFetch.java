package cn.yxeht.app.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.core.HaiTaoSpliderService;

public class TestFetch {

	public static void main(String[] args) {
		
//		try {
//			
//			URL url = new URL("http://localhost:8080/YXESplider/testDecode?detail_link=http://go.smzdm.com/f5c6566101289c58/ca_aa_ht_57_6267811_597_2151_133"); // 把字符串转换为URL请求地址
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
//			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
//			connection.connect();// 连接会话
//			// 获取输入流
//			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//			String line;
//			StringBuilder sb = new StringBuilder();
//			while ((line = br.readLine()) != null) {// 循环读取流
//				sb.append(line);
//			}
//			
//			System.out.println(sb.toString());
//			br.close();// 关闭流
//			connection.disconnect();// 断开连接
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		String url = "http://www.smzdm.com/p/6278118/";//zhufenlei
		url = "http://www.smzdm.com/p/6276853/";//faxian
		url = "http://www.smzdm.com/p/6280354/";//日亚
		url = "http://www.smzdm.com/p/6286512/";//6pm
		url = "http://www.smzdm.com/p/6307262/";
		url = "http://www.smzdm.com/p/6336943/";//stp
		url = "http://www.smzdm.com/p/6413441/";//windln
//		GoodDetail gd = HaiTaoSpliderService.fetchSMZDMGoodDetail(url, "");
//		gd = HaiTaoSpliderService.fetchGDGoodDetail("www.amazon.com", "http://guangdiu.com/detail.php?id=3005655", BaseRule.GET);
//		System.out.println(gd);
		
		System.out.println(new Date(1472895098000l));
		
//		String u = "http://faxian.smzdm.com/mall/%E6%97%A5%E6%9C%AC%E4%BA%9A%E9%A9%AC%E9%80%8A/";
//		String u1 = "http://faxian.smzdm.com/mall/%C8%D5%B1%BE%D1%C7%C2%ED%D1%B7";
//		System.out.println(URLDecoder.decode(u)+"\n"+URLDecoder.decode(u1));
//		try {
//			System.out.println(URLDecoder.decode(u, "UTF-8")+"\n"+URLDecoder.decode(u1, "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
//		String uChinese = "http://faxian.smzdm.com/mall/日本亚马逊";
//		System.out.println(URLEncoder.encode(uChinese));
//		String encode = URLEncoder.encode("日本亚马逊");
//		System.out.println(encode);
//		System.out.println(URLDecoder.decode(encode));
//		System.out.println(URLDecoder.decode("%E6%97%A5%E6%9C%AC%E4%BA%9A%E9%A9%AC%E9%80%8A"));
	
		url = "http://www.meidebi.com/out/1500575.html";//https://www.amazon.com/dp/B0058PKPNE?tag=dmbd-20
		medebi("没得比", url);
		url = "http://go.smzdm.com/f46541a9309a36b1/ca_aa_ht_191_6745726_305_765_199";//http://www.ebay.com/itm/112060475988?rmvSB=true
		medebi("什么之得买(e-bay)", url);
	}
	
	private static void medebi(String name, String url){
		//http://www.meidebi.com/out/1500575.html
		//https://www.amazon.com/dp/B0058PKPNE?tag=dmbd-20
		try {
			Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").get();
			String baseUri = doc.baseUri();
			String location = doc.location();
			System.out.println("["+name+"]baseUri:"+baseUri);
			System.out.println("["+name+"]location:"+location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
