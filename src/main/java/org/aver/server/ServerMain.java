package org.aver.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.spi.HttpServerProvider;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		System.out.println(ServerMain.class.getResource("").getPath());
//		if(1==1)return;
		HttpServerProvider provider = HttpServerProvider.provider();
		HttpServer httpserver = provider.createHttpServer(
				new InetSocketAddress(19017), 100);// 监听端口19017,能同时接受100个请求
		httpserver.createContext("/", new MyResponseHandler());
		httpserver.setExecutor(null);
		httpserver.start();
		System.out.println("server started");
	}

	public static class MyResponseHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			System.out.println("receive");
			String requestMethod = httpExchange.getRequestMethod();
			if (requestMethod.equalsIgnoreCase("GET")) {// 客户端的请求是get方法
				// 设置服务端响应的编码格式，否则在客户端收到的可能是乱码
				Headers responseHeaders = httpExchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/html;charset=utf-8");

				// 在这里通过httpExchange获取客户端发送过来的消息
				// URI url = httpExchange.getRequestURI();
				// InputStream requestBody = httpExchange.getRequestBody();

				String response = "this is server";
				response = FileUtils.readFileToString(new File("C:/Users/YangJie/Desktop/123.html"));
				httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
						response.getBytes("UTF-8").length);

				OutputStream responseBody = httpExchange.getResponseBody();
				OutputStreamWriter writer = new OutputStreamWriter(
						responseBody, "UTF-8");
				writer.write(response);
				writer.close();
				responseBody.close();
			}

		}
	}
	
}
