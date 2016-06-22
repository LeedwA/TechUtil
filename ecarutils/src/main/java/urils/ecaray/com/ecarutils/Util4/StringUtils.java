package urils.ecaray.com.ecarutils.Util4;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * 验证邮箱地址是否正确
	 * 
	 * @param email
	 * @return true 邮箱名正确
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			Log.e("tag", "验证邮箱地址错误");
			flag = false;
		}

		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return true手机号正确
	 */
	public static boolean isMobileNO1(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^((13)|(15)|(18))\\d{9}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			Log.e("tag", "验证手机号码错误");
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证车牌号(除前二位)是否合法
	 * 
	 * @功能：
	 * @param：车牌号
	 * @return：true 合法
	 * @throws Exception
	 */
	public static boolean checkPlateNumber(String plateNumber) {
		boolean flag = false;
		try {
			String check1 = "[a-zA-Z_0-9]{4}[a-zA-Z_0-9]?[a-zA-Z_0-9_\\u4e00-\\u9fa5]$|^[a-zA-Z]{2}\\d{7}$";
			Pattern regex1 = Pattern.compile(check1);
			Matcher matcher1 = regex1.matcher(plateNumber);
			flag = matcher1.matches();
		} catch (Exception e) {
			Log.e("tag", "验证车牌号错误");
			flag = false;
		}
		return flag;
	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static String urlParse(String url) {
		// url = url.replaceAll(" ", "%20").replaceAll("<", "%3C")
		// .replaceAll(">", "%3E");
		// return url;
		int len1 = url.indexOf("&t=");
		int len2 = url.indexOf("&method=");
		if (len1 != -1 && len2 != -1) {
			String str1 = url.substring(len1 + 3, len2);
			String str2 = url.substring(len1, len2);
			url = url.replaceAll(str2, "");
			int len3 = url.indexOf("Index.aspx?");
			url = url.substring(0, len3) + str1 + "api/" + url.substring(len3);
		}
		url = url.replaceAll(" ", "%20").replaceAll("<", "%3C")
				.replaceAll(">", "%3E");
		return url;
	}

	// 将字符串转换成二进制字符串，以空格相隔
	public static String strToBinaryStr(String str) {
		char[] strChar = str.toCharArray();
		String result = "";
		for (int i = 0; i < strChar.length; i++) {
			result += Integer.toBinaryString(strChar[i]) + " ";
		}
		return result;
	}

	// 将二进制字符串转换成Unicode字符串
	public static String binaryStrToStr(String binStr) {
		String[] tempStr = strToStrArray(binStr);
		char[] tempChar = new char[tempStr.length];
		for (int i = 0; i < tempStr.length; i++) {
			tempChar[i] = binaryStrToChar(tempStr[i]);
		}
		return String.valueOf(tempChar);
	}

	// 将二进制字符串转换为char
	public static char binaryStrToChar(String binaryStr) {
		int[] temp = binaryStrToIntArray(binaryStr);
		int sum = 0;
		for (int i = 0; i < temp.length; i++) {
			sum += temp[temp.length - 1 - i] << i;
		}
		return (char) sum;
	}

	// 将初始二进制字符串转换成字符串数组，以空格相隔
	public static String[] strToStrArray(String str) {
		return str.split(" ");
	}

	// 将二进制字符串转换成int数组
	public static int[] binaryStrToIntArray(String binStr) {
		char[] temp = binStr.toCharArray();
		int[] result = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			result[i] = temp[i] - 48;
		}
		return result;
	}

	/**
	 * @功能：高亮显示搜索关键词
	 * @param：textView:需要设置的textView searchKey:关键词 color:高亮显示的颜色 text:需要设置的句子
	 * @return：
	 * @throws Exception
	 */
	public static void setSearchKey(TextView textView, String text,
									String searchKey, int color) {
		SpannableString s = new SpannableString(text);
		if (searchKey.equals("*")) {
			searchKey = "\\*";
		} else if (searchKey.contains("*") || searchKey.contains(".")) {
			searchKey = searchKey.replaceAll("\\*", "");
			searchKey = searchKey.replaceAll("\\.", "");
		}
		Pattern p = Pattern.compile(searchKey);
		Matcher m = p.matcher(s);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			s.setSpan(new ForegroundColorSpan(color), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textView.setText(s);

	}


	/**
	 * @功能：匹配是否为中文
	 * @param：
	 * @return：
	 * @throws Exception
	 */
	public static boolean checkIsChines(String str) {
		boolean flag = false;
		try {
			String check = "^[\u4e00-\u9fa5]+$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(str);
			flag = matcher.matches();
		} catch (Exception e) {
			Log.e("tag", "验证邮箱地址错误");
			flag = false;
		}

		return flag;
	}

	private static boolean isCurrentAccount(char account) {
		return ((int) account >= 0 && (int) account <= 47)
				|| ((int) account >= 58 && (int) account <= 64)
				|| ((int) account >= 91 && (int) account <= 96)
				|| ((int) account >= 123 && (int) account <= 127)
				|| (int) account == 65292 || (int) account == 12290
				|| (int) account == 65311 || (int) account == 65281
				|| (int) account == 8220 || (int) account == 65306
				|| (int) account == 65307;
	}

	public static boolean isAccount(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (isCurrentAccount(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}