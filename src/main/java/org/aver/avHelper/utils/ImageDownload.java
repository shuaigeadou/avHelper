package org.aver.avHelper.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ImageDownload {
    /**
     * 下载图片
     * @param pictureUrl    图片地址
     * @param localPath     本地文件夹
     */
    public static void download(String pictureUrl, String localPath) {
        URL url = null;
        //从网络上下载一张图片
        InputStream inputStream = null;
        OutputStream outputStream = null;
		// 建立一个网络链接
		HttpURLConnection con = null;
		// /创建代理服务器
		InetSocketAddress addr = new InetSocketAddress("localhost", 1080);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr); // Socket 代理
//		 Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理

		// 图片名称
		String pictureName = pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);

		try {
			url = new URL(pictureUrl);
			 con = (HttpURLConnection) url.openConnection(proxy);
//			con = (HttpURLConnection) url.openConnection();
			// 设置请求信息
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			inputStream = con.getInputStream();
			outputStream = new FileOutputStream(new File(localPath + "/" + pictureName));
			int n = -1;
			byte b[] = new byte[1024];
			while ((n = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, n);
			}
			outputStream.flush();
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 下载图片
     * @param pictureUrl    图片地址
     * @param localFilePath     本地文件
     */
    public static void downloadByProxy(String pictureUrl, String localFilePath) {
    	byte[] picBytes = HttpClientUtil.getWithProxy(pictureUrl, null);
    	OutputStream os = null;
    	try {
			os = new FileOutputStream(localFilePath);
			os.write(picBytes);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

}
