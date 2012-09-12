package jp.mitukiii.tumblife.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jp.mitukiii.tumblife.model.TLPost;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class TLPostParser extends TLParser
{
  public TLPostParser(InputStream input)
    throws IOException, ParseException
  {
    super(input);
  }

  public List<TLPost> parse()
  {
    List<TLPost> posts = new ArrayList<TLPost>(50);
    
    JSONObject response = (JSONObject)root.get("response");
    JSONArray jsonPosts = (JSONArray)response.get("posts");
    
    for (int i = 0; i < jsonPosts.size(); i++) {
    	JSONObject e = (JSONObject)jsonPosts.get(i);
    	
    	TLPost post = new TLPost();
        post.setId((Long)e.get("id"));
        post.setUrl((String)e.get("post_url"));
        post.setUrlWithSlug((String)e.get("post_url"));
        post.setType((String)e.get("type"));
        post.setDateGmt((String)e.get("date"));
        post.setDate((String)e.get("date"));
        post.setUnixTimestamp((Long)e.get("timestamp"));
        post.setFormat((String)e.get("format"));
        post.setReblogKey((String)e.get("reblog_key"));
        post.setSlug("");
        post.setNoteCount((Long)e.get("note_count"));
        //post.setRebloggedFromUrl(parser.getAttributeValue(NAME_SPACE, "reblogged-from-url"));
        //post.setRebloggedFromName(parser.getAttributeValue(NAME_SPACE, "reblogged-from-name"));
        //post.setRebloggedFromTitle(parser.getAttributeValue(NAME_SPACE, "reblogged-from-title"));
        
        //JSONObject b = (JSONObject)e.get("blog");
        post.setTumblelogTitle((String)e.get("blog_name"));
        //post.setTumblelogName((String)b.get("name"));
        //post.setTumblelogUrl((String)b.get("url"));
        //post.setTumblelogTimezone(parser.getAttributeValue(NAME_SPACE, "timezone"));
        
        JSONArray tags = (JSONArray)e.get("tags");
        String tagsString = new String("");
        for (int j = 0; j < tags.size(); j++) {
        	tagsString = tagsString + tags.get(i);
        	if (j + 1 < tags.size()) {
            	tagsString = tagsString + ",";
        	}
        }
        post.setTag(tagsString);
        
        if (post.getType().equals("quote")) {
        	post.setQuoteText((String)e.get("text"));
        	post.setQuoteSource((String)e.get("source"));
        }
        
        if (post.getType().equals("photo")) {
            post.setPhotoCaption((String)e.get("caption"));
            post.setPhotoLinkUrl((String)e.get("link_url"));
            JSONArray photos = (JSONArray)e.get("photos");
            JSONArray sizes = (JSONArray)((JSONObject)photos.get(0)).get("alt_sizes");
            for (int j = 0; j < sizes.size(); j++) {
            	JSONObject s = (JSONObject)sizes.get(j);
                Long maxWidth = (Long)s.get("width");
                if (1280 == maxWidth) {
                  post.setPhotoUrlMaxWidth1280((String)s.get("url"));
                } else if (500 == maxWidth) {
                  post.setPhotoUrlMaxWidth500((String)s.get("url"));
                } else if (400 == maxWidth) {
                  post.setPhotoUrlMaxWidth400((String)s.get("url"));
                } else if (250 == maxWidth) {
                  post.setPhotoUrlMaxWidth250((String)s.get("url"));
                } else if (100 == maxWidth) {
                  post.setPhotoUrlMaxWidth100((String)s.get("url"));
                } else if (75 == maxWidth) {
                  post.setPhotoUrlMaxWidth75((String)s.get("url"));
                } 
            }
        }
        if (post.getPhotoUrlMaxWidth100() == null) {
        	post.setPhotoUrlMaxWidth100(post.getPhotoUrlMaxWidth75());
        }
        if (post.getPhotoUrlMaxWidth250() == null) {
        	post.setPhotoUrlMaxWidth250(post.getPhotoUrlMaxWidth100());
        }
        if (post.getPhotoUrlMaxWidth400() == null) {
        	post.setPhotoUrlMaxWidth400(post.getPhotoUrlMaxWidth250());
        }
        if (post.getPhotoUrlMaxWidth500() == null) {
        	post.setPhotoUrlMaxWidth500(post.getPhotoUrlMaxWidth400());
        }
        if (post.getPhotoUrlMaxWidth1280() == null) {
        	post.setPhotoUrlMaxWidth1280(post.getPhotoUrlMaxWidth500());
        }
        
        if (post.getType().equals("link")) {
        	post.setLinkText((String)e.get("title"));
        	post.setLinkUrl((String)e.get("url"));
        	post.setLinkDescription((String)e.get("description"));
        }
        
        if (post.getType().equals("chat")) {
        	// TODO:
        	/*
        	post.setConversationTitle(parser.nextText());
        	post.setConversationText(parser.nextText());
        	String beforeText = post.getConversation();
        	if (beforeText == null) {
        		beforeText = "";
        	}
        	post.setConversation(beforeText + "<p>" + parser.getAttributeValue(NAME_SPACE, "label") + parser.nextText() + "</p>");
        	*/
        }
        
        if (post.getType().equals("video")) {
        	// TODO:
        	/*
       		post.setVideoCaption(parser.nextText());
        try {
          post.setVideoSource(parser.nextText());
        } catch (XmlPullParserException exception) {
          // Raise error if contains meta info of video by XML.
        }
        post.setVideoPlayer(parser.nextText());
        	 */
        }

        if (post.getType().equals("audio")) {
        	// TODO:
        	/*
        post.setAudioCaption(parser.nextText());
        post.setAudioPlayer(parser.nextText());
        	 */
        }
        
        // what?
        // post.setDownloadUrl(parser.nextText());
        
        if (post.getType().equals("text")) {
        	post.setRegularTitle((String)e.get("title"));
        	post.setRegularBody((String)e.get("body"));
        }

        posts.add(post);
    }
    
    return posts;
  }

}
