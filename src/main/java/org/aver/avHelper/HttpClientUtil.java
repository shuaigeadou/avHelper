package org.aver.avHelper;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	// 依次是代理地址，代理端口号，用户密码
	private static String proxyHost = "";
	private static int proxyPort = 1080;
	private static String proxyName = "";
	private static String proxyPwd = "";
	private static int timeOutMillis = 10000;

	public static void setVariables(String proxyHost, int proxyPort, String proxyName, String proxyPwd) {
		HttpClientUtil.proxyHost = proxyHost;
		HttpClientUtil.proxyPort = proxyPort;
		HttpClientUtil.proxyName = proxyName;
		HttpClientUtil.proxyPwd = proxyPwd;
	}
	
	public static String getWithProxy(String url, Map<String, String> headers, String charset) {
		try {
			return new String(getWithProxy(url, headers), charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 以字节数组形式返回结果，超时时间10秒，超时后重试10次
	 * @param url
	 * @param headers
	 * @return
	 */
	public static byte[] getWithProxy(String url, Map<String, String> headers) {
		// 失败重试，重试10次
		int count = 0;
		boolean flag = true;
		byte[] result = null;
		Exception tempE = null;
		while (flag && count < 10) {
			try {
				result = getWithProxyWithoutRetry(url, headers);
				flag = false;
			} catch (Exception e) {
				tempE = e;
				System.out.println("重试"+count+":" + url);
				count++;
			}
		}
		if(count == 10){
			tempE.printStackTrace();
		}
		return result;
	}
	
	public static byte[] getWithProxyWithoutRetry(String url, Map<String, String> headers) throws IOException{
		// 用户名和密码验证
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication p = new PasswordAuthentication(proxyName, proxyPwd.toCharArray());
				return p;
			}
		});
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", new MyConnectionSocketFactory())
				.register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
		try {
			InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
			HttpClientContext context = HttpClientContext.create();
			context.setAttribute("socks.address", socksaddr);
			HttpGet httpget = new HttpGet(url);
			if (headers != null) {
				for (String key : headers.keySet()) {
					httpget.setHeader(key, headers.get(key));
				}
			}
			
			//设置超时时间
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeOutMillis)
					.setConnectionRequestTimeout(timeOutMillis).setSocketTimeout(timeOutMillis).build();
			httpget.setConfig(requestConfig);
			
			CloseableHttpResponse response = httpclient.execute(httpget, context);
			try {
				return EntityUtils.toByteArray(response.getEntity());
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
	
	static class FakeDnsResolver implements DnsResolver {
		@Override
		public InetAddress[] resolve(String host) throws UnknownHostException {
			// Return some fake DNS record for every request, we won't be using
			// it
			return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }) };
		}
	}

	static class MyConnectionSocketFactory extends PlainConnectionSocketFactory {
		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		@Override
		public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
				InetSocketAddress localAddress, HttpContext context) throws IOException {
			// Convert address to unresolved
			InetSocketAddress unresolvedRemote = InetSocketAddress.createUnresolved(host.getHostName(),
					remoteAddress.getPort());
			return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
		}
	}

	static class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

		public MySSLConnectionSocketFactory(final SSLContext sslContext) {
			// You may need this verifier if target site's certificate is not
			// secure
			super(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
		}

		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		@Override
		public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
				InetSocketAddress localAddress, HttpContext context) throws IOException {
			// Convert address to unresolved
			InetSocketAddress unresolvedRemote = InetSocketAddress.createUnresolved(host.getHostName(),
					remoteAddress.getPort());
			return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
		}
	}

}