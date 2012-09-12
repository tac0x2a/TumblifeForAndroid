package jp.mitukiii.tumblife.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jp.mitukiii.tumblife.model.TLTumblelog;
import jp.mitukiii.tumblife.model.TLUser;

import org.json.simple.parser.ParseException;

public class TLUserParser extends TLParser
{
  public TLUserParser(InputStream input)
    throws IOException, ParseException
  {
    super(input);
  }

  public TLUser parse()
  {
    TLUser user = new TLUser();
    List<TLTumblelog> tumblelogs = new ArrayList<TLTumblelog>();
    /*
    for (int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
      if (e == XmlPullParser.START_TAG) {
        String tag = parser.getName();
        if ("tumblelog".equals(tag)) {
          TLTumblelog tumblelog = new TLTumblelog();
          tumblelog.setTitle(parser.getAttributeValue(NAME_SPACE, "title"));
          tumblelog.setAdmin("1".equals(parser.getAttributeValue(NAME_SPACE, "is-admin")));
          tumblelog.setTwitterEnabled("1".equals(parser.getAttributeValue(NAME_SPACE, "twitter-enabled")));
          tumblelog.setType(parser.getAttributeValue(NAME_SPACE, "type"));
          if ("private".equals(tumblelog.getType())) {
            tumblelog.setPrimary(false);
          } else if ("public".equals(tumblelog.getType())) {
            tumblelog.setName(parser.getAttributeValue(NAME_SPACE, "name"));
            tumblelog.setUrl(parser.getAttributeValue(NAME_SPACE, "url"));
            tumblelog.setAvatarUrl(parser.getAttributeValue(NAME_SPACE, "avatar-url"));
            tumblelog.setPrimary("yes".equals(parser.getAttributeValue(NAME_SPACE, "is-primary")));
          }
          tumblelogs.add(tumblelog);
        }
      }
    }
    */
    user.setTumblelogs(tumblelogs);
    return user;
  }
}
