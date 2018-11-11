package org.aver.avHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlDownload {

	/**
	 * 下载HTML源码
	 * 
	 * @param urlString
	 *            地址
	 * @return
	 */
	public static String download(String urlString) {
		String resultHtml = null;
		URL url = null;
		InputStream inputStream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 建立一个网络链接
		HttpURLConnection con = null;

		// /创建代理服务器
		// InetSocketAddress addr = new InetSocketAddress("localhost", 1080);
		// Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr); // Socket 代理
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
		try {
			url = new URL(urlString);
			// con = (HttpURLConnection) url.openConnection(proxy);
			con = (HttpURLConnection) url.openConnection();
			// 设置请求信息
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			con.setRequestProperty("Cookie", "existmag=all");
			con.setConnectTimeout(10000);
			inputStream = con.getInputStream();
			int n = -1;
			byte b[] = new byte[1024];
			while ((n = inputStream.read(b)) != -1) {
				baos.write(b, 0, n);
			}
			resultHtml = new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultHtml;

	}

	/**
	 *  获取影片地址
	 * @param webSite
	 * @param properties
	 * @return
	 */
	public static Document getDocBySite(String webSite, Map<String, String> properties) {
		String htmlString = HttpClientUtil.getWithProxy(webSite, properties, "utf-8");
		Document document = Jsoup.parse(htmlString);
		return document;
	}

}
