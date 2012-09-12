package jp.mitukiii.tumblife.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class TLConnection
{
  public static String POST = "POST";
  public static String GET  = "GET";

  public static HttpURLConnection request(String _url, String method, Map<String, String> parameters, Map<String, String> headers, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    TLLog.i("TLConnection / request : url / " + _url);

    HttpURLConnection con = null;
    OutputStreamWriter writer = null;

    try {
      StringBuilder sb = new StringBuilder();
      Iterator<String> pit = parameters.keySet().iterator();
      while (pit.hasNext()) {
        String parameterKey = (String)pit.next();
        String parameterValue = parameters.get(parameterKey);
        if (sb.length() > 0) {
          sb.append("&");
        }
        sb.append(parameterKey);
        sb.append("=");
        sb.append(URLEncoder.encode(parameterValue));
      }

      if (POST.equals(method)) {
        con = (HttpURLConnection) new URL(_url).openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod(method);
        writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(sb.toString());
        writer.flush();
      } else {
        con = (HttpURLConnection) new URL(_url + '?' + sb.toString()).openConnection();
        con.setDoInput(true);
        con.setRequestMethod(method);
      }

      Iterator<String> hit = headers.keySet().iterator();
      while (hit.hasNext()) {
        String field = (String)hit.next();
        String value = headers.get(field);
        con.setRequestProperty(field, value);
      }

      if (consumer != null) {
    	  try {
			consumer.sign(con);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      
      con.connect();

      return con;
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        TLLog.i("TLConnection / request", e);
      }
    }
  }

  public static HttpURLConnection request(String _url, String method, Map<String, String> parameters, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, method, parameters, new HashMap<String, String>(), consumer);
  }

  public static HttpURLConnection request(String _url, String method, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, method, new HashMap<String, String>(), new HashMap<String, String>(), consumer);
  }

  public static HttpURLConnection post(String _url, Map<String, String> parameters, Map<String, String> headers, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, POST, parameters, headers, consumer);
  }

  public static HttpURLConnection post(String _url, Map<String, String> parameters, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, POST, parameters, new HashMap<String, String>(), consumer);
  }

  public static HttpURLConnection post(String _url, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, POST, new HashMap<String, String>(), new HashMap<String, String>(), consumer);
  }

  public static HttpURLConnection get(String _url, Map<String, String> parameters, Map<String, String> headers, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, GET, parameters, headers, consumer);
  }

  public static HttpURLConnection get(String _url, Map<String, String> parameters, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, GET, parameters, new HashMap<String, String>(), consumer);
  }

  public static HttpURLConnection get(String _url, OAuthConsumer consumer)
    throws MalformedURLException, IOException
  {
    return request(_url, GET, new HashMap<String, String>(), new HashMap<String, String>(), consumer);
  }
}
