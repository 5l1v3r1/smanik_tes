package com.google.android.vending.licensing;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class ResponseData {
  public String extra;
  
  public int nonce;
  
  public String packageName;
  
  public int responseCode;
  
  public long timestamp;
  
  public String userId;
  
  public String versionCode;
  
  public static ResponseData parse(String paramString) {
    String str2;
    String str1;
    int i = paramString.indexOf(':');
    if (-1 == i) {
      str1 = "";
      str2 = paramString;
    } else {
      str2 = paramString.substring(0, i);
      if (i >= paramString.length()) {
        paramString = "";
      } else {
        paramString = paramString.substring(i + 1);
      } 
      str1 = paramString;
    } 
    String[] arrayOfString = TextUtils.split(str2, Pattern.quote("|"));
    if (arrayOfString.length < 6)
      throw new IllegalArgumentException("Wrong number of fields."); 
    ResponseData responseData = new ResponseData();
    responseData.extra = str1;
    Integer.parseInt(arrayOfString[0]);
    responseData.responseCode = 0;
    responseData.nonce = Integer.parseInt(arrayOfString[1]);
    responseData.packageName = arrayOfString[2];
    responseData.versionCode = arrayOfString[3];
    responseData.userId = arrayOfString[4];
    responseData.timestamp = Long.parseLong(arrayOfString[5]);
    return responseData;
  }
  
  public String toString() { return TextUtils.join("|", new Object[] { Integer.valueOf(this.responseCode), Integer.valueOf(this.nonce), this.packageName, this.versionCode, this.userId, Long.valueOf(this.timestamp) }); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/ResponseData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */