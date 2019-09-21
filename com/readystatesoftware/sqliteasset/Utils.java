package com.readystatesoftware.sqliteasset;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Utils {
  private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
  
  public static String convertStreamToString(InputStream paramInputStream) { return (new Scanner(paramInputStream)).useDelimiter("\\A").next(); }
  
  public static ZipInputStream getFileFromZip(InputStream paramInputStream) throws IOException {
    paramInputStream = new ZipInputStream(paramInputStream);
    ZipEntry zipEntry = paramInputStream.getNextEntry();
    if (zipEntry != null) {
      String str = TAG;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("extracting file: '");
      stringBuilder.append(zipEntry.getName());
      stringBuilder.append("'...");
      Log.w(str, stringBuilder.toString());
      return paramInputStream;
    } 
    return null;
  }
  
  public static List<String> splitSqlScript(String paramString, char paramChar) {
    ArrayList arrayList = new ArrayList();
    StringBuilder stringBuilder = new StringBuilder();
    char[] arrayOfChar = paramString.toCharArray();
    byte b1 = 0;
    for (byte b2 = 0; b1 < paramString.length(); b2 = b) {
      StringBuilder stringBuilder1;
      byte b = b2;
      if (arrayOfChar[b1] == '"')
        b = b2 ^ true; 
      if (arrayOfChar[b1] == paramChar && !b) {
        stringBuilder1 = stringBuilder;
        if (stringBuilder.length() > 0) {
          arrayList.add(stringBuilder.toString().trim());
          stringBuilder1 = new StringBuilder();
        } 
      } else {
        stringBuilder.append(arrayOfChar[b1]);
        stringBuilder1 = stringBuilder;
      } 
      b1++;
      stringBuilder = stringBuilder1;
    } 
    if (stringBuilder.length() > 0)
      arrayList.add(stringBuilder.toString().trim()); 
    return arrayList;
  }
  
  public static void writeExtractedFileToDisk(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = new byte[1024];
    while (true) {
      int i = paramInputStream.read(arrayOfByte);
      if (i > 0) {
        paramOutputStream.write(arrayOfByte, 0, i);
        continue;
      } 
      break;
    } 
    paramOutputStream.flush();
    paramOutputStream.close();
    paramInputStream.close();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/readystatesoftware/sqliteasset/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */