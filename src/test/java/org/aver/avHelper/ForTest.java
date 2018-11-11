package org.aver.avHelper;

import java.io.File;
import java.io.IOException;

public class ForTest {
	public static void main(String[] args) throws IOException {
		String path = "D:\\360极速浏览器下载/now_printing.jpg";
		
		System.out.println(new File(path).length() == 2732);
	}
}
