package com.gtcom.janusimport.until;

public class RowkeyUtil {

	public static String rowkey(String key) {
		String rowkey;
		String str = String.valueOf((int) (System.currentTimeMillis() % 20));
		if (str.length() == 3) {
			rowkey = str;
		} else if (str.length() == 2)
			rowkey = "0" + str;
		else if (str.length() == 1)
			rowkey = "00" + str;
		else {
			rowkey = "000";
		}

		rowkey += String.valueOf(System.currentTimeMillis());
		rowkey += Murmurs.hashStr(key);
		return rowkey;
	}
	public static String rowkeyByName(String key) {

		String rowkey="";
		rowkey += Murmurs.hashStr(key);
		rowkey=""+rowkey;
		return rowkey;
	}

/*	public static void main(String[] args) {

		String s1 = "中国";
		String s2 = "哈哈哈,今天吃得好饱,现在好饿呀，救命呀，呜呜uuwuwuuwwuuwu";
//
//		System.out.println(rowkey(s1));
//		
		System.out.println("-------------------------------------");
		System.out.println(rowkeyByName("2"));
	}*/
}
