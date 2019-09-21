package com.blogspot.scqq.b0x.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {
  static final String TAG = "com.blogspot.scqq.b0x.Util.FileHelper";
  
  static final String fileName = "data.txt";
  
  static final String path;
  
  static  {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Environment.getExternalStorageDirectory().getAbsolutePath());
    stringBuilder.append("/instinctcoder/readwrite/");
    path = stringBuilder.toString();
  }
  
  public static String ReadFile(Context paramContext) {
    String str1;
    Context context2 = null;
    Object object = null;
    str2 = null;
    paramContext = context2;
    Context context1 = object;
    try {
      StringBuilder stringBuilder2 = new StringBuilder();
      paramContext = context2;
      context1 = object;
      stringBuilder2.append(path);
      paramContext = context2;
      context1 = object;
      stringBuilder2.append("data.txt");
      paramContext = context2;
      context1 = object;
      FileInputStream fileInputStream = new FileInputStream(new File(stringBuilder2.toString()));
      paramContext = context2;
      context1 = object;
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
      paramContext = context2;
      context1 = object;
      StringBuilder stringBuilder1 = new StringBuilder();
      context1 = str2;
      while (true) {
        String str;
        paramContext = context1;
        str2 = bufferedReader.readLine();
        if (str2 != null) {
          try {
            str1 = new StringBuilder();
            str1.append(str2);
            str1.append(System.getProperty("line.separator"));
            stringBuilder1.append(str1.toString());
            str = str2;
            continue;
          } catch (FileNotFoundException paramContext) {
            str = str2;
          } catch (IOException paramContext) {
            str = str2;
            Log.d(TAG, paramContext.getMessage());
            return str;
          } 
        } else {
          fileInputStream.close();
          String str3 = stringBuilder1.toString();
          str1 = str3;
          str = str3;
          bufferedReader.close();
          return str3;
        } 
        Log.d(TAG, str1.getMessage());
        return str;
      } 
    } catch (FileNotFoundException paramContext) {
    
    } catch (IOException str2) {
      context1 = paramContext;
      str1 = str2;
      Log.d(TAG, str1.getMessage());
      return context1;
    } 
    Log.d(TAG, str1.getMessage());
    return context1;
  }
  
  public static boolean saveToFile(String paramString) {
    try {
      (new File(path)).mkdir();
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(path);
      stringBuilder1.append("data.txt");
      File file = new File(stringBuilder1.toString());
      if (!file.exists())
        file.createNewFile(); 
      FileOutputStream fileOutputStream = new FileOutputStream(file, true);
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append(paramString);
      stringBuilder2.append(System.getProperty("line.separator"));
      fileOutputStream.write(stringBuilder2.toString().getBytes());
      return true;
    } catch (FileNotFoundException paramString) {
      Log.d(TAG, paramString.getMessage());
    } catch (IOException paramString) {
      Log.d(TAG, paramString.getMessage());
    } 
    return false;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/Util/FileHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */