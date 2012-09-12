package jp.mitukiii.tumblife;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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

import jp.mitukiii.tumblife.model.TLConsumer;
import jp.mitukiii.tumblife.model.TLSetting;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
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
	    		  b = TLSetting.getSharedInstance(context).setToken(context, res[0], res[1]);
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
