package jp.mitukiii.tumblife.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

abstract public class TLParser
{
  protected JSONObject root;

  public static final String NAME_SPACE = null;

  public TLParser(InputStream input)
    throws IOException, ParseException
  {
	  Object obj = JSONValue.parseWithException(new InputStreamReader(input));
	  root = (JSONObject)obj;
  }
}
