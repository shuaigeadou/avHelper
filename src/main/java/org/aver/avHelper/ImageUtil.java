package org.aver.avHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {

	/**
	 * Description: 根据原图与裁切size截取局部图片
	 * 
	 * @param srcImg
	 *            源图片
	 * @param output
	 *            图片输出流
	 * @param rect
	 *            需要截取部分的坐标和大小
	 */
	public static void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect) {
		if (srcImg.exists()) {
			java.io.FileInputStream fis = null;
			ImageInputStream iis = null;
			try {
				fis = new FileInputStream(srcImg);
				// ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
				// JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
				String suffix = null;
				// 获取图片后缀
				if (srcImg.getName().indexOf(".") > -1) {
					suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
				} // 类型和图片后缀全部小写，然后判断后缀是否合法
				if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
					System.out.println("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
					return;
				}
				// 将FileInputStream 转换为ImageInputStream
				iis = ImageIO.createImageInputStream(fis);
				// 根据图片类型获取该种类型的ImageReader
				ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
				reader.setInput(iis, true);
				ImageReadParam param = reader.getDefaultReadParam();
				param.setSourceRegion(rect);
				BufferedImage bi = reader.read(0, param);
				ImageIO.write(bi, suffix, output);
//				System.out.println("图片生成成功，请到目录下查看");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null)
						fis.close();
					if (iis != null)
						iis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("the src image is not exist.");
		}
	}

	/**
	 * 切割图片
	 * @param srcImg
	 * @param destImg
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void cutImage(String srcImg, String destImg, int x, int y, int width, int height) {
		try {
			cutImage(new File(srcImg), new java.io.FileOutputStream(destImg), new java.awt.Rectangle(x, y, width, height));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取图片的长宽
	 * @param picture
	 * @return
	 */
	public static int[] getImageSize(File picture) {
		BufferedImage sourceImg = null;
		try {
			sourceImg = ImageIO.read(new FileInputStream(picture));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new int[] { sourceImg.getWidth(), sourceImg.getHeight() };
	}

}
