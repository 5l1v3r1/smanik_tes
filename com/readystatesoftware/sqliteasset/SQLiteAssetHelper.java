package com.readystatesoftware.sqliteasset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class SQLiteAssetHelper extends SQLiteOpenHelper {
  private static final String ASSET_DB_PATH = "databases";
  
  private static final String TAG = "SQLiteAssetHelper";
  
  private String mAssetPath;
  
  private final Context mContext;
  
  private SQLiteDatabase mDatabase = null;
  
  private String mDatabasePath;
  
  private final SQLiteDatabase.CursorFactory mFactory;
  
  private int mForcedUpgradeVersion = 0;
  
  private boolean mIsInitializing = false;
  
  private final String mName;
  
  private final int mNewVersion;
  
  private String mUpgradePathFormat;
  
  static  {
  
  }
  
  public SQLiteAssetHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt) { this(paramContext, paramString, null, paramCursorFactory, paramInt); }
  
  public SQLiteAssetHelper(Context paramContext, String paramString1, String paramString2, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt) {
    super(paramContext, paramString1, paramCursorFactory, paramInt);
    if (paramInt < 1) {
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append("Version must be >= 1, was ");
      stringBuilder1.append(paramInt);
      throw new IllegalArgumentException(stringBuilder1.toString());
    } 
    if (paramString1 == null)
      throw new IllegalArgumentException("Database name cannot be null"); 
    this.mContext = stringBuilder1;
    this.mName = paramString1;
    this.mFactory = paramCursorFactory;
    this.mNewVersion = paramInt;
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append("databases/");
    stringBuilder2.append(paramString1);
    this.mAssetPath = stringBuilder2.toString();
    if (paramString2 != null) {
      this.mDatabasePath = paramString2;
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append((stringBuilder1.getApplicationInfo()).dataDir);
      stringBuilder.append("/databases");
      this.mDatabasePath = stringBuilder.toString();
    } 
    StringBuilder stringBuilder1 = new StringBuilder();
    stringBuilder1.append("databases/");
    stringBuilder1.append(paramString1);
    stringBuilder1.append("_upgrade_%s-%s.sql");
    this.mUpgradePathFormat = stringBuilder1.toString();
  }
  
  private void copyDatabaseFromAssets() throws SQLiteAssetException { // Byte code:
    //   0: getstatic com/readystatesoftware/sqliteasset/SQLiteAssetHelper.TAG : Ljava/lang/String;
    //   3: ldc 'copying database from assets...'
    //   5: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   8: pop
    //   9: aload_0
    //   10: getfield mAssetPath : Ljava/lang/String;
    //   13: astore #4
    //   15: new java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial <init> : ()V
    //   22: astore_2
    //   23: aload_2
    //   24: aload_0
    //   25: getfield mDatabasePath : Ljava/lang/String;
    //   28: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: pop
    //   32: aload_2
    //   33: ldc '/'
    //   35: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: pop
    //   39: aload_2
    //   40: aload_0
    //   41: getfield mName : Ljava/lang/String;
    //   44: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: pop
    //   48: aload_2
    //   49: invokevirtual toString : ()Ljava/lang/String;
    //   52: astore_3
    //   53: iconst_0
    //   54: istore_1
    //   55: aload_0
    //   56: getfield mContext : Landroid/content/Context;
    //   59: invokevirtual getAssets : ()Landroid/content/res/AssetManager;
    //   62: aload #4
    //   64: invokevirtual open : (Ljava/lang/String;)Ljava/io/InputStream;
    //   67: astore_2
    //   68: goto -> 162
    //   71: aload_0
    //   72: getfield mContext : Landroid/content/Context;
    //   75: invokevirtual getAssets : ()Landroid/content/res/AssetManager;
    //   78: astore_2
    //   79: new java/lang/StringBuilder
    //   82: dup
    //   83: invokespecial <init> : ()V
    //   86: astore #5
    //   88: aload #5
    //   90: aload #4
    //   92: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: pop
    //   96: aload #5
    //   98: ldc '.zip'
    //   100: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: pop
    //   104: aload_2
    //   105: aload #5
    //   107: invokevirtual toString : ()Ljava/lang/String;
    //   110: invokevirtual open : (Ljava/lang/String;)Ljava/io/InputStream;
    //   113: astore_2
    //   114: iconst_1
    //   115: istore_1
    //   116: goto -> 162
    //   119: aload_0
    //   120: getfield mContext : Landroid/content/Context;
    //   123: invokevirtual getAssets : ()Landroid/content/res/AssetManager;
    //   126: astore_2
    //   127: new java/lang/StringBuilder
    //   130: dup
    //   131: invokespecial <init> : ()V
    //   134: astore #5
    //   136: aload #5
    //   138: aload #4
    //   140: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: pop
    //   144: aload #5
    //   146: ldc '.gz'
    //   148: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: pop
    //   152: aload_2
    //   153: aload #5
    //   155: invokevirtual toString : ()Ljava/lang/String;
    //   158: invokevirtual open : (Ljava/lang/String;)Ljava/io/InputStream;
    //   161: astore_2
    //   162: new java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial <init> : ()V
    //   169: astore #4
    //   171: aload #4
    //   173: aload_0
    //   174: getfield mDatabasePath : Ljava/lang/String;
    //   177: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: pop
    //   181: aload #4
    //   183: ldc '/'
    //   185: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: pop
    //   189: new java/io/File
    //   192: dup
    //   193: aload #4
    //   195: invokevirtual toString : ()Ljava/lang/String;
    //   198: invokespecial <init> : (Ljava/lang/String;)V
    //   201: astore #4
    //   203: aload #4
    //   205: invokevirtual exists : ()Z
    //   208: ifne -> 217
    //   211: aload #4
    //   213: invokevirtual mkdir : ()Z
    //   216: pop
    //   217: iload_1
    //   218: ifeq -> 255
    //   221: aload_2
    //   222: invokestatic getFileFromZip : (Ljava/io/InputStream;)Ljava/util/zip/ZipInputStream;
    //   225: astore_2
    //   226: aload_2
    //   227: ifnonnull -> 240
    //   230: new com/readystatesoftware/sqliteasset/SQLiteAssetHelper$SQLiteAssetException
    //   233: dup
    //   234: ldc 'Archive is missing a SQLite database file'
    //   236: invokespecial <init> : (Ljava/lang/String;)V
    //   239: athrow
    //   240: aload_2
    //   241: new java/io/FileOutputStream
    //   244: dup
    //   245: aload_3
    //   246: invokespecial <init> : (Ljava/lang/String;)V
    //   249: invokestatic writeExtractedFileToDisk : (Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   252: goto -> 267
    //   255: aload_2
    //   256: new java/io/FileOutputStream
    //   259: dup
    //   260: aload_3
    //   261: invokespecial <init> : (Ljava/lang/String;)V
    //   264: invokestatic writeExtractedFileToDisk : (Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   267: getstatic com/readystatesoftware/sqliteasset/SQLiteAssetHelper.TAG : Ljava/lang/String;
    //   270: ldc 'database copy complete'
    //   272: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   275: pop
    //   276: return
    //   277: astore_2
    //   278: new java/lang/StringBuilder
    //   281: dup
    //   282: invokespecial <init> : ()V
    //   285: astore #4
    //   287: aload #4
    //   289: ldc 'Unable to write '
    //   291: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: pop
    //   295: aload #4
    //   297: aload_3
    //   298: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: pop
    //   302: aload #4
    //   304: ldc ' to data directory'
    //   306: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   309: pop
    //   310: new com/readystatesoftware/sqliteasset/SQLiteAssetHelper$SQLiteAssetException
    //   313: dup
    //   314: aload #4
    //   316: invokevirtual toString : ()Ljava/lang/String;
    //   319: invokespecial <init> : (Ljava/lang/String;)V
    //   322: astore_3
    //   323: aload_3
    //   324: aload_2
    //   325: invokevirtual getStackTrace : ()[Ljava/lang/StackTraceElement;
    //   328: invokevirtual setStackTrace : ([Ljava/lang/StackTraceElement;)V
    //   331: aload_3
    //   332: athrow
    //   333: astore_2
    //   334: new java/lang/StringBuilder
    //   337: dup
    //   338: invokespecial <init> : ()V
    //   341: astore_3
    //   342: aload_3
    //   343: ldc 'Missing '
    //   345: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   348: pop
    //   349: aload_3
    //   350: aload_0
    //   351: getfield mAssetPath : Ljava/lang/String;
    //   354: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   357: pop
    //   358: aload_3
    //   359: ldc ' file (or .zip, .gz archive) in assets, or target folder not writable'
    //   361: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: pop
    //   365: new com/readystatesoftware/sqliteasset/SQLiteAssetHelper$SQLiteAssetException
    //   368: dup
    //   369: aload_3
    //   370: invokevirtual toString : ()Ljava/lang/String;
    //   373: invokespecial <init> : (Ljava/lang/String;)V
    //   376: astore_3
    //   377: aload_3
    //   378: aload_2
    //   379: invokevirtual getStackTrace : ()[Ljava/lang/StackTraceElement;
    //   382: invokevirtual setStackTrace : ([Ljava/lang/StackTraceElement;)V
    //   385: aload_3
    //   386: athrow
    //   387: astore_2
    //   388: goto -> 71
    //   391: astore_2
    //   392: goto -> 119
    // Exception table:
    //   from	to	target	type
    //   55	68	387	java/io/IOException
    //   71	114	391	java/io/IOException
    //   119	162	333	java/io/IOException
    //   162	217	277	java/io/IOException
    //   221	226	277	java/io/IOException
    //   230	240	277	java/io/IOException
    //   240	252	277	java/io/IOException
    //   255	267	277	java/io/IOException
    //   267	276	277	java/io/IOException }
  
  private SQLiteDatabase createOrOpenDatabase(boolean paramBoolean) throws SQLiteAssetException {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.mDatabasePath);
    stringBuilder.append("/");
    stringBuilder.append(this.mName);
    if ((new File(stringBuilder.toString())).exists()) {
      SQLiteDatabase sQLiteDatabase = returnDatabase();
    } else {
      stringBuilder = null;
    } 
    if (stringBuilder != null) {
      SQLiteDatabase sQLiteDatabase;
      if (paramBoolean) {
        Log.w(TAG, "forcing database upgrade!");
        copyDatabaseFromAssets();
        sQLiteDatabase = returnDatabase();
      } 
      return sQLiteDatabase;
    } 
    copyDatabaseFromAssets();
    return returnDatabase();
  }
  
  private void getUpgradeFilePaths(int paramInt1, int paramInt2, int paramInt3, ArrayList<String> paramArrayList) {
    if (getUpgradeSQLStream(paramInt2, paramInt3) != null) {
      paramArrayList.add(String.format(this.mUpgradePathFormat, new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }));
      paramInt3 = paramInt2;
      paramInt2--;
    } else {
      paramInt2--;
    } 
    if (paramInt2 < paramInt1)
      return; 
    getUpgradeFilePaths(paramInt1, paramInt2, paramInt3, paramArrayList);
  }
  
  private InputStream getUpgradeSQLStream(int paramInt1, int paramInt2) {
    String str = String.format(this.mUpgradePathFormat, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    try {
      return this.mContext.getAssets().open(str);
    } catch (IOException iOException) {
      String str1 = TAG;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("missing database upgrade script: ");
      stringBuilder.append(str);
      Log.w(str1, stringBuilder.toString());
      return null;
    } 
  }
  
  private SQLiteDatabase returnDatabase() {
    try {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(this.mDatabasePath);
      stringBuilder1.append("/");
      stringBuilder1.append(this.mName);
      SQLiteDatabase sQLiteDatabase = SQLiteDatabase.openDatabase(stringBuilder1.toString(), this.mFactory, 0);
      String str = TAG;
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append("successfully opened database ");
      stringBuilder2.append(this.mName);
      Log.i(str, stringBuilder2.toString());
      return sQLiteDatabase;
    } catch (SQLiteException sQLiteException) {
      String str = TAG;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("could not open database ");
      stringBuilder.append(this.mName);
      stringBuilder.append(" - ");
      stringBuilder.append(sQLiteException.getMessage());
      Log.w(str, stringBuilder.toString());
      return null;
    } 
  }
  
  public void close() throws SQLiteAssetException { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mIsInitializing : Z
    //   6: ifeq -> 19
    //   9: new java/lang/IllegalStateException
    //   12: dup
    //   13: ldc 'Closed during initialization'
    //   15: invokespecial <init> : (Ljava/lang/String;)V
    //   18: athrow
    //   19: aload_0
    //   20: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   23: ifnull -> 48
    //   26: aload_0
    //   27: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   30: invokevirtual isOpen : ()Z
    //   33: ifeq -> 48
    //   36: aload_0
    //   37: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   40: invokevirtual close : ()V
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   48: aload_0
    //   49: monitorexit
    //   50: return
    //   51: astore_1
    //   52: aload_0
    //   53: monitorexit
    //   54: aload_1
    //   55: athrow
    // Exception table:
    //   from	to	target	type
    //   2	19	51	finally
    //   19	48	51	finally }
  
  public SQLiteDatabase getReadableDatabase() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   6: ifnull -> 28
    //   9: aload_0
    //   10: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   13: invokevirtual isOpen : ()Z
    //   16: ifeq -> 28
    //   19: aload_0
    //   20: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   23: astore_1
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: areturn
    //   28: aload_0
    //   29: getfield mIsInitializing : Z
    //   32: ifeq -> 45
    //   35: new java/lang/IllegalStateException
    //   38: dup
    //   39: ldc 'getReadableDatabase called recursively'
    //   41: invokespecial <init> : (Ljava/lang/String;)V
    //   44: athrow
    //   45: aload_0
    //   46: invokevirtual getWritableDatabase : ()Landroid/database/sqlite/SQLiteDatabase;
    //   49: astore_1
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_1
    //   53: areturn
    //   54: astore_1
    //   55: aload_0
    //   56: getfield mName : Ljava/lang/String;
    //   59: ifnonnull -> 64
    //   62: aload_1
    //   63: athrow
    //   64: getstatic com/readystatesoftware/sqliteasset/SQLiteAssetHelper.TAG : Ljava/lang/String;
    //   67: astore_2
    //   68: new java/lang/StringBuilder
    //   71: dup
    //   72: invokespecial <init> : ()V
    //   75: astore_3
    //   76: aload_3
    //   77: ldc 'Couldn't open '
    //   79: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: pop
    //   83: aload_3
    //   84: aload_0
    //   85: getfield mName : Ljava/lang/String;
    //   88: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: pop
    //   92: aload_3
    //   93: ldc_w ' for writing (will try read-only):'
    //   96: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: pop
    //   100: aload_2
    //   101: aload_3
    //   102: invokevirtual toString : ()Ljava/lang/String;
    //   105: aload_1
    //   106: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   109: pop
    //   110: aload_0
    //   111: iconst_1
    //   112: putfield mIsInitializing : Z
    //   115: aload_0
    //   116: getfield mContext : Landroid/content/Context;
    //   119: aload_0
    //   120: getfield mName : Ljava/lang/String;
    //   123: invokevirtual getDatabasePath : (Ljava/lang/String;)Ljava/io/File;
    //   126: invokevirtual getPath : ()Ljava/lang/String;
    //   129: astore_1
    //   130: aload_1
    //   131: aload_0
    //   132: getfield mFactory : Landroid/database/sqlite/SQLiteDatabase$CursorFactory;
    //   135: iconst_1
    //   136: invokestatic openDatabase : (Ljava/lang/String;Landroid/database/sqlite/SQLiteDatabase$CursorFactory;I)Landroid/database/sqlite/SQLiteDatabase;
    //   139: astore_2
    //   140: aload_2
    //   141: invokevirtual getVersion : ()I
    //   144: aload_0
    //   145: getfield mNewVersion : I
    //   148: if_icmpeq -> 219
    //   151: new java/lang/StringBuilder
    //   154: dup
    //   155: invokespecial <init> : ()V
    //   158: astore_3
    //   159: aload_3
    //   160: ldc_w 'Can't upgrade read-only database from version '
    //   163: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: pop
    //   167: aload_3
    //   168: aload_2
    //   169: invokevirtual getVersion : ()I
    //   172: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   175: pop
    //   176: aload_3
    //   177: ldc_w ' to '
    //   180: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: pop
    //   184: aload_3
    //   185: aload_0
    //   186: getfield mNewVersion : I
    //   189: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   192: pop
    //   193: aload_3
    //   194: ldc_w ': '
    //   197: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: pop
    //   201: aload_3
    //   202: aload_1
    //   203: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: pop
    //   207: new android/database/sqlite/SQLiteException
    //   210: dup
    //   211: aload_3
    //   212: invokevirtual toString : ()Ljava/lang/String;
    //   215: invokespecial <init> : (Ljava/lang/String;)V
    //   218: athrow
    //   219: aload_0
    //   220: aload_2
    //   221: invokevirtual onOpen : (Landroid/database/sqlite/SQLiteDatabase;)V
    //   224: getstatic com/readystatesoftware/sqliteasset/SQLiteAssetHelper.TAG : Ljava/lang/String;
    //   227: astore_1
    //   228: new java/lang/StringBuilder
    //   231: dup
    //   232: invokespecial <init> : ()V
    //   235: astore_3
    //   236: aload_3
    //   237: ldc_w 'Opened '
    //   240: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: pop
    //   244: aload_3
    //   245: aload_0
    //   246: getfield mName : Ljava/lang/String;
    //   249: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: aload_3
    //   254: ldc_w ' in read-only mode'
    //   257: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   260: pop
    //   261: aload_1
    //   262: aload_3
    //   263: invokevirtual toString : ()Ljava/lang/String;
    //   266: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   269: pop
    //   270: aload_0
    //   271: aload_2
    //   272: putfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   275: aload_0
    //   276: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   279: astore_1
    //   280: aload_0
    //   281: iconst_0
    //   282: putfield mIsInitializing : Z
    //   285: aload_2
    //   286: ifnull -> 301
    //   289: aload_2
    //   290: aload_0
    //   291: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   294: if_acmpeq -> 301
    //   297: aload_2
    //   298: invokevirtual close : ()V
    //   301: aload_0
    //   302: monitorexit
    //   303: aload_1
    //   304: areturn
    //   305: astore_1
    //   306: goto -> 312
    //   309: astore_1
    //   310: aconst_null
    //   311: astore_2
    //   312: aload_0
    //   313: iconst_0
    //   314: putfield mIsInitializing : Z
    //   317: aload_2
    //   318: ifnull -> 333
    //   321: aload_2
    //   322: aload_0
    //   323: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   326: if_acmpeq -> 333
    //   329: aload_2
    //   330: invokevirtual close : ()V
    //   333: aload_1
    //   334: athrow
    //   335: astore_1
    //   336: aload_0
    //   337: monitorexit
    //   338: aload_1
    //   339: athrow
    // Exception table:
    //   from	to	target	type
    //   2	24	335	finally
    //   28	45	335	finally
    //   45	50	54	android/database/sqlite/SQLiteException
    //   45	50	335	finally
    //   55	64	335	finally
    //   64	110	335	finally
    //   110	140	309	finally
    //   140	219	305	finally
    //   219	280	305	finally
    //   280	285	335	finally
    //   289	301	335	finally
    //   312	317	335	finally
    //   321	333	335	finally
    //   333	335	335	finally }
  
  public SQLiteDatabase getWritableDatabase() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   6: ifnull -> 38
    //   9: aload_0
    //   10: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   13: invokevirtual isOpen : ()Z
    //   16: ifeq -> 38
    //   19: aload_0
    //   20: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   23: invokevirtual isReadOnly : ()Z
    //   26: ifne -> 38
    //   29: aload_0
    //   30: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   33: astore_3
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_3
    //   37: areturn
    //   38: aload_0
    //   39: getfield mIsInitializing : Z
    //   42: ifeq -> 56
    //   45: new java/lang/IllegalStateException
    //   48: dup
    //   49: ldc_w 'getWritableDatabase called recursively'
    //   52: invokespecial <init> : (Ljava/lang/String;)V
    //   55: athrow
    //   56: aconst_null
    //   57: astore #4
    //   59: aload #4
    //   61: astore_3
    //   62: aload_0
    //   63: iconst_1
    //   64: putfield mIsInitializing : Z
    //   67: aload #4
    //   69: astore_3
    //   70: aload_0
    //   71: iconst_0
    //   72: invokespecial createOrOpenDatabase : (Z)Landroid/database/sqlite/SQLiteDatabase;
    //   75: astore #5
    //   77: aload #5
    //   79: astore #4
    //   81: aload #5
    //   83: invokevirtual getVersion : ()I
    //   86: istore_2
    //   87: iload_2
    //   88: istore_1
    //   89: aload #5
    //   91: astore_3
    //   92: iload_2
    //   93: ifeq -> 148
    //   96: iload_2
    //   97: istore_1
    //   98: aload #5
    //   100: astore_3
    //   101: aload #5
    //   103: astore #4
    //   105: iload_2
    //   106: aload_0
    //   107: getfield mForcedUpgradeVersion : I
    //   110: if_icmpge -> 148
    //   113: aload #5
    //   115: astore #4
    //   117: aload_0
    //   118: iconst_1
    //   119: invokespecial createOrOpenDatabase : (Z)Landroid/database/sqlite/SQLiteDatabase;
    //   122: astore #5
    //   124: aload #5
    //   126: astore_3
    //   127: aload #5
    //   129: aload_0
    //   130: getfield mNewVersion : I
    //   133: invokevirtual setVersion : (I)V
    //   136: aload #5
    //   138: astore_3
    //   139: aload #5
    //   141: invokevirtual getVersion : ()I
    //   144: istore_1
    //   145: aload #5
    //   147: astore_3
    //   148: aload_3
    //   149: astore #4
    //   151: iload_1
    //   152: aload_0
    //   153: getfield mNewVersion : I
    //   156: if_icmpeq -> 310
    //   159: aload_3
    //   160: astore #4
    //   162: aload_3
    //   163: invokevirtual beginTransaction : ()V
    //   166: iload_1
    //   167: ifne -> 178
    //   170: aload_0
    //   171: aload_3
    //   172: invokevirtual onCreate : (Landroid/database/sqlite/SQLiteDatabase;)V
    //   175: goto -> 275
    //   178: iload_1
    //   179: aload_0
    //   180: getfield mNewVersion : I
    //   183: if_icmple -> 265
    //   186: getstatic com/readystatesoftware/sqliteasset/SQLiteAssetHelper.TAG : Ljava/lang/String;
    //   189: astore #4
    //   191: new java/lang/StringBuilder
    //   194: dup
    //   195: invokespecial <init> : ()V
    //   198: astore #5
    //   200: aload #5
    //   202: ldc_w 'Can't downgrade read-only database from version '
    //   205: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   208: pop
    //   209: aload #5
    //   211: iload_1
    //   212: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   215: pop
    //   216: aload #5
    //   218: ldc_w ' to '
    //   221: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: pop
    //   225: aload #5
    //   227: aload_0
    //   228: getfield mNewVersion : I
    //   231: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   234: pop
    //   235: aload #5
    //   237: ldc_w ': '
    //   240: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: pop
    //   244: aload #5
    //   246: aload_3
    //   247: invokevirtual getPath : ()Ljava/lang/String;
    //   250: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: pop
    //   254: aload #4
    //   256: aload #5
    //   258: invokevirtual toString : ()Ljava/lang/String;
    //   261: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   264: pop
    //   265: aload_0
    //   266: aload_3
    //   267: iload_1
    //   268: aload_0
    //   269: getfield mNewVersion : I
    //   272: invokevirtual onUpgrade : (Landroid/database/sqlite/SQLiteDatabase;II)V
    //   275: aload_3
    //   276: aload_0
    //   277: getfield mNewVersion : I
    //   280: invokevirtual setVersion : (I)V
    //   283: aload_3
    //   284: invokevirtual setTransactionSuccessful : ()V
    //   287: aload_3
    //   288: astore #4
    //   290: aload_3
    //   291: invokevirtual endTransaction : ()V
    //   294: goto -> 310
    //   297: aload_3
    //   298: astore #4
    //   300: aload_3
    //   301: invokevirtual endTransaction : ()V
    //   304: aload_3
    //   305: astore #4
    //   307: aload #5
    //   309: athrow
    //   310: aload_3
    //   311: astore #4
    //   313: aload_0
    //   314: aload_3
    //   315: invokevirtual onOpen : (Landroid/database/sqlite/SQLiteDatabase;)V
    //   318: aload_0
    //   319: iconst_0
    //   320: putfield mIsInitializing : Z
    //   323: aload_0
    //   324: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   327: astore #4
    //   329: aload #4
    //   331: ifnull -> 341
    //   334: aload_0
    //   335: getfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   338: invokevirtual close : ()V
    //   341: aload_0
    //   342: aload_3
    //   343: putfield mDatabase : Landroid/database/sqlite/SQLiteDatabase;
    //   346: aload_0
    //   347: monitorexit
    //   348: aload_3
    //   349: areturn
    //   350: astore_3
    //   351: goto -> 362
    //   354: astore #5
    //   356: aload_3
    //   357: astore #4
    //   359: aload #5
    //   361: astore_3
    //   362: aload_0
    //   363: iconst_0
    //   364: putfield mIsInitializing : Z
    //   367: aload #4
    //   369: ifnull -> 377
    //   372: aload #4
    //   374: invokevirtual close : ()V
    //   377: aload_3
    //   378: athrow
    //   379: astore_3
    //   380: aload_0
    //   381: monitorexit
    //   382: aload_3
    //   383: athrow
    //   384: astore #4
    //   386: goto -> 341
    //   389: astore #5
    //   391: goto -> 297
    // Exception table:
    //   from	to	target	type
    //   2	34	379	finally
    //   38	56	379	finally
    //   62	67	354	finally
    //   70	77	354	finally
    //   81	87	350	finally
    //   105	113	350	finally
    //   117	124	350	finally
    //   127	136	354	finally
    //   139	145	354	finally
    //   151	159	350	finally
    //   162	166	350	finally
    //   170	175	389	finally
    //   178	265	389	finally
    //   265	275	389	finally
    //   275	287	389	finally
    //   290	294	350	finally
    //   300	304	350	finally
    //   307	310	350	finally
    //   313	318	350	finally
    //   318	329	379	finally
    //   334	341	384	java/lang/Exception
    //   334	341	379	finally
    //   341	346	379	finally
    //   362	367	379	finally
    //   372	377	379	finally
    //   377	379	379	finally }
  
  public final void onConfigure(SQLiteDatabase paramSQLiteDatabase) {}
  
  public final void onCreate(SQLiteDatabase paramSQLiteDatabase) {}
  
  public final void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
    StringBuilder stringBuilder1;
    String str2 = TAG;
    StringBuilder stringBuilder3 = new StringBuilder();
    stringBuilder3.append("Upgrading database ");
    stringBuilder3.append(this.mName);
    stringBuilder3.append(" from version ");
    stringBuilder3.append(paramInt1);
    stringBuilder3.append(" to ");
    stringBuilder3.append(paramInt2);
    stringBuilder3.append("...");
    Log.w(str2, stringBuilder3.toString());
    StringBuilder stringBuilder2 = new ArrayList();
    getUpgradeFilePaths(paramInt1, paramInt2 - 1, paramInt2, stringBuilder2);
    if (stringBuilder2.isEmpty()) {
      String str = TAG;
      stringBuilder2 = new StringBuilder();
      stringBuilder2.append("no upgrade script path from ");
      stringBuilder2.append(paramInt1);
      stringBuilder2.append(" to ");
      stringBuilder2.append(paramInt2);
      Log.e(str, stringBuilder2.toString());
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append("no upgrade script path from ");
      stringBuilder1.append(paramInt1);
      stringBuilder1.append(" to ");
      stringBuilder1.append(paramInt2);
      throw new SQLiteAssetException(stringBuilder1.toString());
    } 
    Collections.sort(stringBuilder2, new VersionComparator());
    for (String str : stringBuilder2) {
      try {
        str3 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("processing upgrade: ");
        stringBuilder.append(str);
        Log.w(str3, stringBuilder.toString());
        str = Utils.convertStreamToString(this.mContext.getAssets().open(str));
        if (str != null)
          for (String str3 : Utils.splitSqlScript(str, ';')) {
            if (str3.trim().length() > 0)
              stringBuilder1.execSQL(str3); 
          }  
      } catch (IOException str) {
        str.printStackTrace();
      } 
    } 
    String str1 = TAG;
    stringBuilder2 = new StringBuilder();
    stringBuilder2.append("Successfully upgraded database ");
    stringBuilder2.append(this.mName);
    stringBuilder2.append(" from version ");
    stringBuilder2.append(paramInt1);
    stringBuilder2.append(" to ");
    stringBuilder2.append(paramInt2);
    Log.w(str1, stringBuilder2.toString());
  }
  
  public void setForcedUpgrade() throws SQLiteAssetException { setForcedUpgrade(this.mNewVersion); }
  
  public void setForcedUpgrade(int paramInt) { this.mForcedUpgradeVersion = paramInt; }
  
  @Deprecated
  public void setForcedUpgradeVersion(int paramInt) { setForcedUpgrade(paramInt); }
  
  public static class SQLiteAssetException extends SQLiteException {
    public SQLiteAssetException() throws SQLiteAssetException {}
    
    public SQLiteAssetException(String param1String) { super(param1String); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/readystatesoftware/sqliteasset/SQLiteAssetHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */