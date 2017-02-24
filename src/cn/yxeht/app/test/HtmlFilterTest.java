package cn.yxeht.app.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlFilterTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String result = readTxtFromFile("/E/jerry/55_detail_html");
//		System.out.println(result);
		
		Document doc = Jsoup.parse(result);
		Elements pTags = doc.select("p");
		for(Element el : pTags){
			el.select("a").remove();
//			Elements children = el.children();
//			for(Element cel : children){
////				System.out.println(cel.html());
//				if("a".equals(cel.tagName().toLowerCase())){
//					cel.remove();
//				}
//			}
			System.out.println(el.text());
		}
		
	}
	
	public static String readTxtFromFile(String filePath){
		
		String result = "";
		
		try {
			File file = new File(filePath);
			if(file.isFile() && file.exists()){
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
		}
		
		return result;
		
	}

}
