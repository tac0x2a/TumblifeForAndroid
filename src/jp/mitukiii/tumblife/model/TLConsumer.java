package jp.mitukiii.tumblife.model;

import jp.mitukiii.tumblife.util.TLLog;
import android.content.Context;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

public class TLConsumer {
	// fill the below keys.
	public final String consumer_key = "";
	public final String consumer_secret = "";

	protected static TLConsumer sharedInstance = null;

	public static TLConsumer getSharedInstance()
	{
		if (sharedInstance == null) {
			sharedInstance = new TLConsumer();
		}
		return sharedInstance;
	}

	public OAuthConsumer getConsumer(Context context) {
	    TLLog.i("TLConsumer / consumer_key: " + consumer_key + ", consumer_secret: " + consumer_secret + ", token: " + TLSetting.getSharedInstance(context).getToken() + ", token_secret: " + TLSetting.getSharedInstance(context).getTokenSecret());
		OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);
		consumer.setTokenWithSecret(TLSetting.getSharedInstance(context).getToken(), TLSetting.getSharedInstance(context).getTokenSecret());
		return consumer;
	}
}
