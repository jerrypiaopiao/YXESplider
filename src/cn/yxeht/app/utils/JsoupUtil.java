package cn.yxeht.app.utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.yxeht.app.exception.JsoupDocException;
import cn.yxeht.app.rule.Rule;

public class JsoupUtil {

	public static final boolean IS_LOCAL_WEB_PAGE = false;
	public static final boolean SHOW_JSOUP_LOG = true;
	public static final boolean IS_TEST = false;
	
	/**
	 * 
	 * @param requestType
	 * @param params
	 * 						params[0]:本地网页文件完整路径或远程网页url
	 * @return
	 * @throws IOException
	 * @throws JsoupDocException
	 */
	public static Document createJsoupDocument(int requestType, String... params) throws IOException, JsoupDocException{
		Document doc = null;
		
		if(SHOW_JSOUP_LOG){
			System.out.println("JsoupUtil#createJsoupDocument#url:"+params[0]);
		}
		
		if(IS_LOCAL_WEB_PAGE){
			if(params == null || params.length == 0){
				throw new JsoupDocException("please set local web page in params, e.g. http://www.hello.com");
			}
			String html = TextUtil.readTxtFromFile(params[0]);
			doc = Jsoup.parse(html);
		}else{
			String url = params[0];
			String trueUA = null;
			String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17";
			String ua1 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
			Random random = new Random();
			int randUA = random.nextInt(20);
			if(randUA%2 == 0){
				trueUA = ua;
			}else{
				trueUA = ua1;
			}
			Connection conn = Jsoup.connect(url).ignoreContentType(true).userAgent(
					trueUA);

			if(params.length == 3){
				String httpHeaderParams = params[1];
				String httpHeaderValues = params[2];
				if(!TextUtil.isEmpty(httpHeaderParams) && !TextUtil.isEmpty(httpHeaderValues)){
					if(SHOW_JSOUP_LOG){
						System.out.println("JsoupUtil#createJsoupDocument#httpHeaderParams:"+httpHeaderParams);
						System.out.println("JsoupUtil#createJsoupDocument#httpHeaderValues:"+httpHeaderValues);
					}
					String[] tmpParams = httpHeaderParams.split(",");
					String[] tmpValues = httpHeaderValues.split(",");
					//TODO 这里需要对比tmpParams与tmpValues的长度,如果长度不等则需要抛出异常
					for (int i = 0; i < tmpParams.length; i++) {
						conn.data(tmpParams[i], tmpValues[i]);
					}
				}
			}
			
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}
		}
		
		if(IS_TEST){
			String htmlCode = doc.html();
			try {
				TextUtil.writeTxtFile(htmlCode, new File("/E/jerry/Andy.deng/smzdm/smzdm_code.html"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return doc;
	}
	
}
