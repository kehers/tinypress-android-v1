package co.tinypress.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

public class Tinypress extends Application {

	private static String mToken;
	private static int mId;
	private static String mUsername;
	private static String mAvatar;
	private static String mPageRepo;

	public static String getToken(){
		return mToken;
	}
	
	public static void setToken(String token){
		mToken = token;
	}

	public static String getAvatar(){
		return mAvatar;
	}
	
	public static void setAvatar(String avatar){
		mAvatar = avatar;
	}

	public static String getPageRepo(){
		return mPageRepo;
	}
	
	public static void setPageRepo(String repo){
		mPageRepo = repo;
	}

	public static String getUsername(){
		return mUsername;
	}
	
	public static void setUsername(String username){
		mUsername = username;
	}


	public static int getId(){
		return mId;
	}
	
	public static void setId(int id){
		mId = id;
	}

	public static String urlfy(String s){
		s = s
				.toLowerCase(Locale.US)
				.trim()
				.replaceAll("[^a-z0-9._]", "-")
				.replaceAll("-+", "-")
				.replaceAll("^-", "")
				.replaceAll("-$", "")
				;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		s = format.format(new Date())+"-"+s+".markdown";
		return s;
	}
	
	public static String[] getTitleAndDate(String name){
		String [] returnValue = new String[2];

		Pattern pattern = Pattern.compile("([0-9]{4})\\-([0-9]{2})\\-([0-9]{2})\\-(.*)");
		Matcher m = pattern.matcher(name);
		 if (!m.matches()) {
			 return returnValue;
		 }
		 
		 String year = m.group(1);
		 String month = m.group(2);
		 String day = m.group(3);
		 String title = m.group(4);
		 
		 SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd", Locale.US);
		 SimpleDateFormat targetFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
		 Date date = new Date();
		 try {
			date = format.parse(year+" "+month+" "+day);
		 } catch (ParseException e) {}
		 returnValue[0] = targetFormat.format(date);
		 
		 StringBuilder yearB = new StringBuilder();
		 yearB.append(year).append("/").append(month).append("/").append(day).append("/").append(title);
		 returnValue[1] = yearB.toString();
		 
		 return returnValue;
	}
	
	public static HashMap<String, String> formatBody(String body) {
		 Pattern pattern = Pattern.compile("^\\-\\-\\-(.*)\\-\\-\\-(.*)", Pattern.DOTALL);
		 Matcher m = pattern.matcher(body);
		 if (!m.matches()) {
			 Log.e("DEBUG", "No body match");
			 
			 return null;
		 }
		 
		 String yaml = m.group(1);
		 body = m.group(2);
		 
		 String [] yamlArray = yaml.split("\\r?\\n");
		 
		 String listKey = null;
		 ArrayList<String> list = new ArrayList<String>();
		 HashMap<String, String> format = new HashMap<String, String>();
		 pattern = Pattern.compile("^\\-\\s*(.*)");
		 for (String line : yamlArray){
			 if (line.startsWith("#") || line.trim().equals(""))	continue;
			 
			 if (listKey != null) {
				 line = line.trim();
				 m = pattern.matcher(line);
				 if (m.matches()) {
					 list.add(m.group(1));
				 }
				 else {
					 format.put(listKey, TextUtils.join(", ", list));
					 listKey = null;
					 list.clear();
				 }
			 }
			 
			 String [] kv = line.split(":\\s?", 2);
			 if (kv[1] == null)	kv[1] = "";
			 format.put(kv[0].trim(), kv[1].trim().replaceAll("'\"", ""));
			 
			 if (kv[1].equals(""))	listKey = kv[1];
		 }
		 
		 format.put("body", body.trim());
		// Category, categeories
		if (format.get("categories") != null)
			format.put("categories", format.get("categories").replaceAll("\\[\\]", "").trim());
		else if (format.get("category") != null)
			format.put("categories", format.get("category"));
		
		if (format.get("tags") != null)
			format.put("tags", format.get("tags").replaceAll("\\[\\]", "").trim());
		 
		return format;
	}
}
