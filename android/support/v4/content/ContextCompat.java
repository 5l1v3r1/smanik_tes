package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat {
  private static final String TAG = "ContextCompat";
  
  private static final Object sLock = new Object();
  
  private static TypedValue sTempValue;
  
  private static File buildPath(File paramFile, String... paramVarArgs) {
    int i = paramVarArgs.length;
    byte b = 0;
    File file;
    for (file = paramFile; b < i; file = paramFile) {
      String str = paramVarArgs[b];
      if (file == null) {
        paramFile = new File(str);
      } else {
        paramFile = file;
        if (str != null)
          paramFile = new File(file, str); 
      } 
      b++;
    } 
    return file;
  }
  
  public static int checkSelfPermission(@NonNull Context paramContext, @NonNull String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("permission is null"); 
    return paramContext.checkPermission(paramString, Process.myPid(), Process.myUid());
  }
  
  @Nullable
  public static Context createDeviceProtectedStorageContext(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 24) ? paramContext.createDeviceProtectedStorageContext() : null; }
  
  private static File createFilesDir(File paramFile) { // Byte code:
    //   0: ldc android/support/v4/content/ContextCompat
    //   2: monitorenter
    //   3: aload_0
    //   4: invokevirtual exists : ()Z
    //   7: ifne -> 70
    //   10: aload_0
    //   11: invokevirtual mkdirs : ()Z
    //   14: ifne -> 70
    //   17: aload_0
    //   18: invokevirtual exists : ()Z
    //   21: istore_1
    //   22: iload_1
    //   23: ifeq -> 31
    //   26: ldc android/support/v4/content/ContextCompat
    //   28: monitorexit
    //   29: aload_0
    //   30: areturn
    //   31: new java/lang/StringBuilder
    //   34: dup
    //   35: invokespecial <init> : ()V
    //   38: astore_2
    //   39: aload_2
    //   40: ldc 'Unable to create files subdir '
    //   42: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: pop
    //   46: aload_2
    //   47: aload_0
    //   48: invokevirtual getPath : ()Ljava/lang/String;
    //   51: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: pop
    //   55: ldc 'ContextCompat'
    //   57: aload_2
    //   58: invokevirtual toString : ()Ljava/lang/String;
    //   61: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   64: pop
    //   65: ldc android/support/v4/content/ContextCompat
    //   67: monitorexit
    //   68: aconst_null
    //   69: areturn
    //   70: ldc android/support/v4/content/ContextCompat
    //   72: monitorexit
    //   73: aload_0
    //   74: areturn
    //   75: astore_0
    //   76: ldc android/support/v4/content/ContextCompat
    //   78: monitorexit
    //   79: aload_0
    //   80: athrow
    // Exception table:
    //   from	to	target	type
    //   3	22	75	finally
    //   31	65	75	finally }
  
  public static File getCodeCacheDir(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 21) ? paramContext.getCodeCacheDir() : createFilesDir(new File((paramContext.getApplicationInfo()).dataDir, "code_cache")); }
  
  @ColorInt
  public static int getColor(@NonNull Context paramContext, @ColorRes int paramInt) { return (Build.VERSION.SDK_INT >= 23) ? paramContext.getColor(paramInt) : paramContext.getResources().getColor(paramInt); }
  
  @Nullable
  public static ColorStateList getColorStateList(@NonNull Context paramContext, @ColorRes int paramInt) { return (Build.VERSION.SDK_INT >= 23) ? paramContext.getColorStateList(paramInt) : paramContext.getResources().getColorStateList(paramInt); }
  
  @Nullable
  public static File getDataDir(@NonNull Context paramContext) {
    if (Build.VERSION.SDK_INT >= 24)
      return paramContext.getDataDir(); 
    String str = (paramContext.getApplicationInfo()).dataDir;
    return (str != null) ? new File(str) : null;
  }
  
  @Nullable
  public static Drawable getDrawable(@NonNull Context paramContext, @DrawableRes int paramInt) {
    if (Build.VERSION.SDK_INT >= 21)
      return paramContext.getDrawable(paramInt); 
    if (Build.VERSION.SDK_INT >= 16)
      return paramContext.getResources().getDrawable(paramInt); 
    synchronized (sLock) {
      if (sTempValue == null)
        sTempValue = new TypedValue(); 
      paramContext.getResources().getValue(paramInt, sTempValue, true);
      paramInt = sTempValue.resourceId;
      return paramContext.getResources().getDrawable(paramInt);
    } 
  }
  
  @NonNull
  public static File[] getExternalCacheDirs(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 19) ? paramContext.getExternalCacheDirs() : new File[] { paramContext.getExternalCacheDir() }; }
  
  @NonNull
  public static File[] getExternalFilesDirs(@NonNull Context paramContext, @Nullable String paramString) { return (Build.VERSION.SDK_INT >= 19) ? paramContext.getExternalFilesDirs(paramString) : new File[] { paramContext.getExternalFilesDir(paramString) }; }
  
  @Nullable
  public static File getNoBackupFilesDir(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 21) ? paramContext.getNoBackupFilesDir() : createFilesDir(new File((paramContext.getApplicationInfo()).dataDir, "no_backup")); }
  
  @NonNull
  public static File[] getObbDirs(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 19) ? paramContext.getObbDirs() : new File[] { paramContext.getObbDir() }; }
  
  public static boolean isDeviceProtectedStorage(@NonNull Context paramContext) { return (Build.VERSION.SDK_INT >= 24) ? paramContext.isDeviceProtectedStorage() : 0; }
  
  public static boolean startActivities(@NonNull Context paramContext, @NonNull Intent[] paramArrayOfIntent) { return startActivities(paramContext, paramArrayOfIntent, null); }
  
  public static boolean startActivities(@NonNull Context paramContext, @NonNull Intent[] paramArrayOfIntent, @Nullable Bundle paramBundle) {
    if (Build.VERSION.SDK_INT >= 16) {
      paramContext.startActivities(paramArrayOfIntent, paramBundle);
    } else {
      paramContext.startActivities(paramArrayOfIntent);
    } 
    return true;
  }
  
  public static void startActivity(@NonNull Context paramContext, @NonNull Intent paramIntent, @Nullable Bundle paramBundle) {
    if (Build.VERSION.SDK_INT >= 16) {
      paramContext.startActivity(paramIntent, paramBundle);
      return;
    } 
    paramContext.startActivity(paramIntent);
  }
  
  public static void startForegroundService(@NonNull Context paramContext, @NonNull Intent paramIntent) {
    if (Build.VERSION.SDK_INT >= 26) {
      paramContext.startForegroundService(paramIntent);
      return;
    } 
    paramContext.startService(paramIntent);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/ContextCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */