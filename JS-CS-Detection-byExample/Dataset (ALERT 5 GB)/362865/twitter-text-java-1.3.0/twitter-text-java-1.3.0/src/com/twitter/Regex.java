
package com.twitter;

import java.util.*;
import java.util.regex.*;

public class Regex {
  private static final String[] RESERVED_ACTION_WORDS = {"twitter","lists",
  "retweet","retweets","following","followings","follower","followers",
  "with_friend","with_friends","statuses","status","activity","favourites",
  "favourite","favorite","favorites"};

  private static final String HASHTAG_CHARACTERS = "[a-z0-9_\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u00ff]";

  /* URL related hash regex collection */
  private static final String URL_VALID_PRECEEDING_CHARS = "(?:[^\\-/\"':!=A-Z0-9_@＠]+|^|\\:)";
  private static final String URL_VALID_DOMAIN = "(?:[^\\p{Punct}\\s][\\.-](?=[^\\p{Punct}\\s])|[^\\p{Punct}\\s]){1,}\\.[a-z]{2,}(?::[0-9]+)?";

  private static final String URL_VALID_GENERAL_PATH_CHARS = "[a-z0-9!\\*';:=\\+\\$/%#\\[\\]\\-_,~]";
  /** Allow URL paths to contain balanced parens
   *  1. Used in Wikipedia URLs like /Primer_(film)
   *  2. Used in IIS sessions like /S(dfd346)/
  **/
  private static final String URL_BALANCE_PARENS = "(?:\\(" + URL_VALID_GENERAL_PATH_CHARS + "+\\))";
  private static final String URL_VALID_URL_PATH_CHARS = "(?:" +
    URL_BALANCE_PARENS +
    "|@" + URL_VALID_GENERAL_PATH_CHARS + "+/" +
    "|[\\.,]?" + URL_VALID_GENERAL_PATH_CHARS + "+" +
  ")";

  /** Valid end-of-path chracters (so /foo. does not gobble the period).
   *   2. Allow =&# for empty URL parameters and other URL-join artifacts
  **/
  private static final String URL_VALID_URL_PATH_ENDING_CHARS = "(?:[a-z0-9=_#/]+|"+URL_BALANCE_PARENS+")";
  private static final String URL_VALID_URL_QUERY_CHARS = "[a-z0-9!\\*'\\(\\);:&=\\+\\$/%#\\[\\]\\-_\\.,~]";
  private static final String URL_VALID_URL_QUERY_ENDING_CHARS = "[a-z0-9_&=#/]";
  private static final String VALID_URL_PATTERN_STRING =
  "(" +                                                            //  $1 total match
    "(" + URL_VALID_PRECEEDING_CHARS + ")" +                       //  $2 Preceeding chracter
    "(" +                                                          //  $3 URL
      "(https?://)" +                                              //  $4 Protocol
      "(" + URL_VALID_DOMAIN + ")" +                               //  $5 Domain(s) and optional port number
      "(/" +
        "(?:" +
          URL_VALID_URL_PATH_CHARS + "+" +
            URL_VALID_URL_PATH_ENDING_CHARS + "|" +                //     1+ path chars and a valid last char
          URL_VALID_URL_PATH_CHARS + "+" +
            URL_VALID_URL_PATH_ENDING_CHARS + "?|" +               //     Optional last char to handle /@foo/ case
          URL_VALID_URL_PATH_ENDING_CHARS +                        //     Just a # case
        ")?" +
      ")?" +                                                       //  $6 URL Path and anchor
      "(\\?" + URL_VALID_URL_QUERY_CHARS + "*" +                   //  $7 Query String
              URL_VALID_URL_QUERY_ENDING_CHARS + ")?" +
    ")" +
  ")";

  private static String AT_SIGNS_CHARS = "@\uFF20";
  private static String LATIN_ACCENTS_CHARS = "\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u00ff";



  /* Begin public constants */
  public static final Pattern AT_SIGNS = Pattern.compile("[" + AT_SIGNS_CHARS + "]");

  public static final Pattern SCREEN_NAME_MATCH_END = Pattern.compile("^(?:[" + AT_SIGNS_CHARS + LATIN_ACCENTS_CHARS + "]|://)");

  public static final Pattern AUTO_LINK_HASHTAGS = Pattern.compile("(^|[^0-9A-Z&/]+)(#|\uFF03)([0-9A-Z_]*[A-Z_]+" + HASHTAG_CHARACTERS + "*)", Pattern.CASE_INSENSITIVE);
  public static final int AUTO_LINK_HASHTAGS_GROUP_BEFORE = 1;
  public static final int AUTO_LINK_HASHTAGS_GROUP_HASH = 2;
  public static final int AUTO_LINK_HASHTAGS_GROUP_TAG = 3;

  public static final Pattern AUTO_LINK_USERNAMES_OR_LISTS = Pattern.compile("([^a-z0-9_]|^|RT:?)(" + AT_SIGNS + "+)([a-z0-9_]{1,20})(/[a-z][a-z0-9_\\-]{0,24})?", Pattern.CASE_INSENSITIVE);
  public static final int AUTO_LINK_USERNAME_OR_LISTS_GROUP_BEFORE = 1;
  public static final int AUTO_LINK_USERNAME_OR_LISTS_GROUP_AT = 2;
  public static final int AUTO_LINK_USERNAME_OR_LISTS_GROUP_USERNAME = 3;
  public static final int AUTO_LINK_USERNAME_OR_LISTS_GROUP_LIST = 4;

  public static final Pattern VALID_URL = Pattern.compile(VALID_URL_PATTERN_STRING, Pattern.CASE_INSENSITIVE);
  public static final int VALID_URL_GROUP_ALL          = 1;
  public static final int VALID_URL_GROUP_BEFORE       = 2;
  public static final int VALID_URL_GROUP_URL          = 3;
  public static final int VALID_URL_GROUP_PROTOCOL     = 4;
  public static final int VALID_URL_GROUP_DOMAIN       = 5;
  public static final int VALID_URL_GROUP_PATH         = 6;
  public static final int VALID_URL_GROUP_QUERY_STRING = 7;

  public static final Pattern EXTRACT_MENTIONS = Pattern.compile("(^|[^a-z0-9_])" + AT_SIGNS + "([a-z0-9_]{1,20})(?=(.|$))", Pattern.CASE_INSENSITIVE);
  public static final int EXTRACT_MENTIONS_GROUP_BEFORE = 1;
  public static final int EXTRACT_MENTIONS_GROUP_USERNAME = 2;
  public static final int EXTRACT_MENTIONS_GROUP_AFTER = 3;

  public static final Pattern EXTRACT_REPLY = Pattern.compile("^(?:[" + com.twitter.regex.Spaces.getCharacterClass() + "])*" + AT_SIGNS + "([a-z0-9_]{1,20}).*", Pattern.CASE_INSENSITIVE);
  public static final int EXTRACT_REPLY_GROUP_USERNAME = 1;
}
