package com.readystatesoftware.sqliteasset;

import android.util.Log;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VersionComparator extends Object implements Comparator<String> {
  private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
  
  private Pattern pattern = Pattern.compile(".*_upgrade_([0-9]+)-([0-9]+).*");
  
  public int compare(String paramString1, String paramString2) {
    StringBuilder stringBuilder = this.pattern.matcher(paramString1);
    Matcher matcher = this.pattern.matcher(paramString2);
    if (!stringBuilder.matches()) {
      paramString2 = TAG;
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("could not parse upgrade script file: ");
      stringBuilder1.append(paramString1);
      Log.w(paramString2, stringBuilder1.toString());
      throw new SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file");
    } 
    if (!matcher.matches()) {
      paramString1 = TAG;
      stringBuilder = new StringBuilder();
      stringBuilder.append("could not parse upgrade script file: ");
      stringBuilder.append(paramString2);
      Log.w(paramString1, stringBuilder.toString());
      throw new SQLiteAssetHelper.SQLiteAssetException("Invalid upgrade script file");
    } 
    byte b2 = 1;
    byte b1 = 1;
    int i = Integer.valueOf(stringBuilder.group(1)).intValue();
    int j = Integer.valueOf(matcher.group(1)).intValue();
    int k = Integer.valueOf(stringBuilder.group(2)).intValue();
    int m = Integer.valueOf(matcher.group(2)).intValue();
    if (i == j) {
      if (k == m)
        return 0; 
      if (k < m)
        b1 = -1; 
      return b1;
    } 
    b1 = b2;
    if (i < j)
      b1 = -1; 
    return b1;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/readystatesoftware/sqliteasset/VersionComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */