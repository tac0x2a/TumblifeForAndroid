package jp.mitukiii.tumblife;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import jp.mitukiii.tumblife.exeption.TLAuthenticationFailureException;
import jp.mitukiii.tumblife.model.TLConsumer;
import jp.mitukiii.tumblife.model.TLSetting;
import jp.mitukiii.tumblife.tumblr.TLDashboard;
import jp.mitukiii.tumblife.util.TLConnection;
import jp.mitukiii.tumblife.util.TLLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
	  @Override
	  protected void onCreate(Bundle savedInstanceState)
	  {
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		((Button) findViewById(R.id.regist_button)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
	  }

	  protected void login()
	  {
	    final ProgressDialog progressDialog = new ProgressDialog(this);
	    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    progressDialog.setTitle(R.string.login);
	    progressDialog.setCancelable(false);
	    progressDialog.show();

	    final Handler handler = new Handler();

	    final Activity context = this;
	    final String username = ((EditText)findViewById(R.id.username_edit)).getText().toString();
	    final String password = ((EditText)findViewById(R.id.password_edit)).getText().toString();
	    new Thread() {
	      public void run() {
	    	  boolean b = false;
	    	  String res[] = doLogin(username, password);
	    	  if (res != null) {
	    		  TLSetting setting = TLSetting.getSharedInstance(context);
	    		  b = setting.setToken(context, res[0], res[1]);
	    		  if (setting.getReblogBlog().equals("")) {
	    			  String userinfo_url = "https'//api.tumblr.com/v2/user/info";
	    			  HttpURLConnection con = null;
	    			  try {
	    				  HashMap<String, String> parameters = new HashMap<String, String>();
	    				  con = TLConnection.post(userinfo_url, parameters, TLConsumer.getSharedInstance().getConsumer(context));
	    				  if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
	    					  throw new TLAuthenticationFailureException();
	    				  }
	    				  JSONObject  obj = (JSONObject) JSONValue.parseWithException(new InputStreamReader(con.getInputStream()));
	    				  JSONArray blogs = (JSONArray)((JSONObject)((JSONObject) obj.get("response")).get("user")).get("blogs");
	    				  for (int i = 0; i < blogs.size(); i++) {
	    					  JSONObject blog = (JSONObject) blogs.get(i);
	    					  if ((Boolean)blog.get("primary")) {
	    						  setting.saveReblogBlog(context, (String)blog.get("name"));
	    					  }
	    				  }
	    			  } catch (ParseException e) {
	    				  TLLog.e("Login / get user info", e);
	    			  } catch (MalformedURLException e) {
	    				  // TODO 自動生成された catch ブロック
	    				  e.printStackTrace();
	    			  } catch (IOException e) {
	    				  // TODO 自動生成された catch ブロック
	    				  e.printStackTrace();
	    			  } finally {
	    				  if (con != null) {
	    					  con.disconnect();
	    				  }
	    			  }
	    		  }
	    	  } else {
	    		  b = false;
	    	  }
	    	  final boolean result = b;

	        handler.post(new Runnable() {
	          public void run() {
	            progressDialog.dismiss();
	            if (result) {
	            	context.finish();
	            } else {
	                new AlertDialog.Builder(context)
	                .setTitle(R.string.login_failure_title)
	                .setMessage("")
	                .setPositiveButton(R.string.button_positive, new OnClickListener() {
	                  public void onClick(DialogInterface dialog, int whichButton) {
	                  }
	                })
	                .show();
	            }
	          }
	        });
	      }
	    }.start();
	  }

	  protected String[] doLogin(String username, String password) {
		  String consumer_key = TLConsumer.getSharedInstance().consumer_key;
		  String consumer_secret = TLConsumer.getSharedInstance().consumer_secret;
		  String access_token_url = "https://www.tumblr.com/oauth/access_token";

			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(access_token_url);
			CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);
			List<BasicNameValuePair> params = Arrays.asList(
					new BasicNameValuePair("x_auth_username", username),
					new BasicNameValuePair("x_auth_password", password),
					new BasicNameValuePair("x_auth_mode", "client_auth"));
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("wtf");
			}
			request.setEntity(entity);
			try {
				consumer.sign(request);
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
			HttpResponse response;
			InputStream data = null;
			try {
				response = client.execute(request);
				data = response.getEntity().getContent();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String responseString = null;
		    try
		    {
		      final char[] buffer = new char[0x10000];
		      StringBuilder out = new StringBuilder();
		      Reader in = new InputStreamReader(data, HTTP.UTF_8);
		      int read;
		      do
		      {
		        read = in.read(buffer, 0, buffer.length);
		        if (read > 0)
		        {
		          out.append(buffer, 0, read);
		        }
		      } while (read >= 0);
		      in.close();
		      responseString = out.toString();
		    } catch (IOException ioe)
		    {
		      throw new IllegalStateException("Error while reading response body", ioe);
		    }

		    String res[] = TextUtils.split(responseString, "&");
		    String token = null;
		    String tokenKey = null;
		    for (String s: res) {
			    String split[] = TextUtils.split(s, "=");
			    if (split.length == 2) {
			    	if (split[0].equals("oauth_token")) {
			    		token = split[1];
			    	} else if (split[0].equals("oauth_token_secret")) {
			    		tokenKey = split[1];
			    	}
			    }
		    }

		    if (token != null && tokenKey != null) {
		    	String ret[] = new String[2];
		    	ret[0] = token;
		    	ret[1] = tokenKey;
		    	return ret;
		    } else {
		    	return null;
		    }
		}
}
