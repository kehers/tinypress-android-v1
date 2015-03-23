package co.tinypress.android;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * Helper class used to communicate with the demo server.
 */
public final class API {

    public void get(String endpoint, String token, ResponseCallback callback) throws IOException {
    	get(endpoint, token, new HashMap<String, String>(), callback);
    }
    
    public void get(String endpoint, String token, HashMap<String, String> additionalHeaders,
    						ResponseCallback callback) throws IOException {
	   URL url = new URL(endpoint);
	   HttpURLConnection conn = null;
	   try {
		   conn = (HttpURLConnection) url.openConnection();
           if (token != null)
              	conn.setRequestProperty("Authorization", "token "+token);
	       conn.setRequestProperty("Content-Type","application/json");
	       if (additionalHeaders.get("Accept") == null)
	    	   conn.setRequestProperty("Accept","application/json");
	       conn.setRequestProperty("User-Agent","Tinypress for Android");
	       
	       // Additional headers
           for (String key : additionalHeaders.keySet()) {
        	   conn.setRequestProperty(key, additionalHeaders.get(key));
           }
           
			InputStream is = null;
            try {
	            is = conn.getInputStream();
            }
            catch (IOException e) {
            	//e.printStackTrace();
				// Hack for 4xx http headers
	            is = conn.getErrorStream();
	            if (is == null) {
	            	callback.handleResponse(0, "There has been a network error.");
    	            return;
	            }
			}
            
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
			String line;
			while((line = rd.readLine()) != null) {
			  response.append(line).append("\n");
			}
			rd.close();	
            
            int status = conn.getResponseCode();
            callback.handleResponse(status, response.toString());
	   }
	    finally {
            if (conn != null) {
                conn.disconnect();
            }
	   }
    }
        
    public void delete(String endpoint, String token, JSONObject params, ResponseCallback callback)
          throws IOException {
    	try {
            HttpEntity entity = new StringEntity(params.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(endpoint);
            httpDeleteWithBody.setHeader("Authorization", "token "+token);
            httpDeleteWithBody.setHeader("Content-Type", "application/json");
            httpDeleteWithBody.setHeader("Accept", "application/json");
            httpDeleteWithBody.setHeader("User-Agent", "Tinypress for Android");
            httpDeleteWithBody.setEntity(entity);
            httpClient.execute(httpDeleteWithBody);
        } catch (UnsupportedEncodingException e) {
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
    	
      /*URL url = new URL(endpoint);
      Log.e("Dev", endpoint);
      String body = params.toString();

      byte[] bytes = body.getBytes();
      HttpURLConnection conn = null;
      try {
          conn = (HttpURLConnection) url.openConnection();
          conn.setUseCaches(false);
          conn.setFixedLengthStreamingMode(bytes.length);
          conn.setRequestMethod("DELETE");
          conn.setRequestProperty("Authorization", "token "+token);
          //conn.setRequestProperty("X-HTTP-Method-Override", "DELETE");
          conn.setRequestProperty("Content-Type", "application/json");
          conn.setRequestProperty("Accept", "application/json"); 
          conn.setRequestProperty("User-Agent", "Tinypress for Android");
          
          OutputStream out = conn.getOutputStream();
          out.write(bytes);
          out.close();            

		  InputStream is = null;
          try {
	            is = conn.getInputStream();
          }
          catch (IOException e) {
          	//e.printStackTrace();
				// Hack for 4xx http headers
	            is = conn.getErrorStream();
	            if (is == null) {
	            	callback.handleResponse(0, "There has been a network error.");
  	            return;
	            }
			}
          
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          StringBuffer response = new StringBuffer();
			String line;
			while((line = rd.readLine()) != null) {
			  response.append(line).append("\n");;
			}
			rd.close();	
          
          int status = conn.getResponseCode();
          callback.handleResponse(status, response.toString());

      } finally {
          if (conn != null) {
              conn.disconnect();
          }
      }//*/
    }
  
    public void post(String endpoint, String token, JSONObject params, ResponseCallback callback)
            throws IOException {
    
        URL url = new URL(endpoint);
        
        String body = params.toString();
        
        Log.d("URL", endpoint+" "+body);

        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            if (token != null)
            	conn.setRequestProperty("Authorization", "token "+token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json"); 
            conn.setRequestProperty("User-Agent", "Tinypress for Android");
            
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();            

			InputStream is = null;
            try {
	            is = conn.getInputStream();
            }
            catch (IOException e) {
            	//e.printStackTrace();
				// Hack for 4xx http headers
	            is = conn.getErrorStream();
	            if (is == null) {
	            	callback.handleResponse(0, "There has been a network error.");
    	            return;
	            }
			}
            
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
			String line;
			while((line = rd.readLine()) != null) {
			  response.append(line).append("\n");;
			}
			rd.close();	
            
            int status = conn.getResponseCode();
            callback.handleResponse(status, response.toString());

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    public void put(String endpoint, String token, JSONObject params, ResponseCallback callback)
            throws IOException {
    
        URL url = new URL(endpoint);
        String body = params.toString();
        //Log.e("BODY", body);

        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("PUT");
        	conn.setRequestProperty("Authorization", "token "+token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json"); 
            conn.setRequestProperty("User-Agent", "Tinypress for Android");
            
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();            

			InputStream is = null;
            try {
	            is = conn.getInputStream();
            }
            catch (IOException e) {
	            is = conn.getErrorStream();
	            if (is == null) {
	            	callback.handleResponse(0, "There has been a network error.");
    	            return;
	            }
			}
            
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
			String line;
			while((line = rd.readLine()) != null) {
			  response.append(line).append("\n");;
			}
			rd.close();	
            
            int status = conn.getResponseCode();
            callback.handleResponse(status, response.toString());

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
}

