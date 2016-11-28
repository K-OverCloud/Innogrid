package com.inno.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComUtil {
	static final Logger log = LoggerFactory.getLogger(ComUtil.class);

	public static String executeArr(ArrayList<String> cmdList, String os) {
		ProcessBuilder b = new ProcessBuilder(cmdList);
		StringBuffer ret = new StringBuffer();
		b.redirectErrorStream(false);
		Process p = null;
		String charSet = "UTF-8";
		
		if(os.equals("linux")){
			charSet = "UTF-8";
		}
		else{//Windows
			charSet = "MS949";
		}
		
		try
		{
			p = b.start();
			InputStreamReader isr =new InputStreamReader(
					new SequenceInputStream(p.getInputStream(),
							p.getErrorStream()), charSet);
			String line = null;
			BufferedReader reader = new BufferedReader(isr);
			while ((line = reader.readLine()) != null) {
				ret.append(line).append("\n");
			}
			reader.close();
			p.destroy();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return ret.toString();
	}
	
	
	 /**
	  * httpGet Get Request ( Using Rest in java )
	  *
	  * @return String
	  */
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line+"\n");
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
	}
	
	/**
	  * autoHttpGetWithAuth Get Request ( Using Rest in java )
	  *
	  * @return String
	  */
	public static String autoHttpGetWithAuth(String urlStr,String username,String password) throws IOException {
		  
		String protocol = "https://";
		String rtnStr = "";
		try{
			if(urlStr.contains(protocol)){
			  rtnStr = ComUtil.httpsGetBasicAuth(urlStr,username,password);
			}else{
			  rtnStr = ComUtil.httpGetBasicAuth(urlStr,username,password);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		  
		return rtnStr;
	}

	
	 /**
	  * httpGetBasicAuth Get Request ( Using Rest in java )
	  *
	  * @return String
	  */
	public static String httpGetBasicAuth(String urlStr,String username,String password) throws IOException {
		
		  URL url = new URL(urlStr);
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		  
		  //Basic Auth Add
		  String authorization = username + ":" + password;
		  byte[] authEncBytes = Base64.encodeBase64(authorization.getBytes());
		  String authStringEnc = new String(authEncBytes);
		  String encodedAuth = "Basic " + authStringEnc;
		  conn.setRequestProperty("Authorization", encodedAuth);
		  
		  /*if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }*/

		  try{
			  if (conn.getResponseCode() != 200) {
				  System.out.println("HTTP_MESSAGE= "+conn.getResponseMessage());
				  return "{ \"error\":\""+conn.getResponseCode()+"\",\"message\":\""+conn.getResponseMessage()+"\"}";
				  //throw new IOException(conn.getResponseMessage());
			  }
		  }
		  catch(Exception e){
			  System.out.println("HTTP_ERROR!!= "+e.getMessage());
			  return "{ \"error\":\"900\",\"message\":\""+e.getMessage()+"\"}";
		  }
		  
		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
	}
	
	 /**
	  * httpsGetBasicAuth Get Request ( Using Rest in java )
	  *
	  * @return String 
	  */
	public static String httpsGetBasicAuth(String urlStr,String username,String password) throws Exception{
		  URL url = new URL(urlStr);
		 
		  //중요 부분 이것때문에 고생함 
		  System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
		  
		  HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		 
		  //Basic Auth Add
		  String authorization = username + ":" + password;
		  byte[] authEncBytes = Base64.encodeBase64(authorization.getBytes());
		  String authStringEnc = new String(authEncBytes);
		  String encodedAuth = "Basic " + authStringEnc;
		  conn.setRequestProperty("Authorization", encodedAuth);
		  
          TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {

                  public java.security.cert.X509Certificate[] getAcceptedIssuers()
                  {
                	  return new java.security.cert.X509Certificate[] {}; 
                  }
                  public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                  {
                      //No need to implement.
                  }
                  public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                  {
                      //No need to implement.
                  }
              }
          };
		  
		  // SSL setting  
		  SSLContext context;
		  try {
			context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new java.security.SecureRandom());  // No validation for now
			conn.setSSLSocketFactory(context.getSocketFactory());  
		  } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		  }
		  
		  // Set Hostname verification  
		  conn.setHostnameVerifier(new HostnameVerifier() {  
			   @Override  
			   public boolean verify(String hostname, SSLSession session) {  
			    // Ignore host name verification. It always returns true.  
			    return true;  
			   }  
		  });
		  
		  try{
			  if (conn.getResponseCode() != 200) {
				  System.out.println("HTTPS_MESSAGE= "+conn.getResponseMessage());
				  return "{ \"error\":\""+conn.getResponseCode()+"\",\"message\":\""+conn.getResponseMessage()+"\"}";
				  //throw new IOException(conn.getResponseMessage());
			  }
		  }
		  catch(Exception e){
			  System.out.println("HTTPS_ERROR!!= "+e.getMessage());
			  return "{ \"error\":\"900\",\"message\":\""+e.getMessage()+"\"}";
		  }
		  
		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line+"\n");
		  }
		  rd.close();
		  conn.disconnect();
		  return sb.toString();
	}
	
	
	
	
	 /**
	  * POST Request ( Using Rest in java )
	  *
	  * @return String
	  */
	public static String httpPost(String urlStr, String[] paramName,
			String[] paramVal) throws Exception {
			  URL url = new URL(urlStr);
			  HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("POST");
			  conn.setDoOutput(true);
			  conn.setDoInput(true);
			  conn.setUseCaches(false);
			  conn.setAllowUserInteraction(false);
			  conn.setRequestProperty("Content-Type",
			      "application/x-www-form-urlencoded");

			  // Create the form content
			  OutputStream out = conn.getOutputStream();
			  Writer writer = new OutputStreamWriter(out, "UTF-8");
			  for (int i = 0; i < paramName.length; i++) {
			    writer.write(paramName[i]);
			    writer.write("=");
			    writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			    writer.write("&");
			  }
			  writer.close();
			  out.close();

			  if (conn.getResponseCode() != 200) {
			    throw new IOException(conn.getResponseMessage());
			  }

			  // Buffer the result into a string
			  BufferedReader rd = new BufferedReader(
			      new InputStreamReader(conn.getInputStream()));
			  StringBuilder sb = new StringBuilder();
			  String line;
			  while ((line = rd.readLine()) != null) {
			    sb.append(line);
			  }
			  rd.close();

			  conn.disconnect();
			  return sb.toString();
	}
	
	/**
	 * POST Request ( Using Rest in java )
	 *
	 * @return String
	 */
	public static String httpsPostBasicAuth(String urlStr,String username,String password,String body) throws Exception{
		URL url = new URL(urlStr);
		 //중요 부분 이것때문에 고생함 
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		
		//Basic Auth Add
		String authorization = username + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authorization.getBytes());
		String authStringEnc = new String(authEncBytes);
		String encodedAuth = "Basic " + authStringEnc;
		conn.setRequestProperty("Authorization", encodedAuth);
		  
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
              	  return new java.security.cert.X509Certificate[] {}; 
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
            }
        };
        
		// SSL setting  
		SSLContext context;
		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new java.security.SecureRandom());  // No validation for now
			conn.setSSLSocketFactory(context.getSocketFactory());  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		  
		// Set Hostname verification  
		conn.setHostnameVerifier(new HostnameVerifier() {  
			@Override  
			public boolean verify(String hostname, SSLSession session) {  
			// Ignore host name verification. It always returns true.  
			   return true;  
			}  
		});
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type","application/json");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		
		// Create the form content
		OutputStream out = conn.getOutputStream();
		out.write(body.getBytes());
		out.flush();
		
		log.info("Post ResponsePre");
		log.info("jsonBody : "+body);
		if (conn.getResponseCode() < 200 || conn.getResponseCode()>=300) {
			log.info("Post ResponseCode:"+conn.getResponseCode());
		    throw new IOException(conn.getResponseMessage());
		}
		
		log.info("Post ResponseCode 200~:"+conn.getResponseCode());

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(
		    new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
		  sb.append(line);
		}
		out.close();
		rd.close();
		conn.disconnect();
		
		
		return sb.toString();
	}

}
