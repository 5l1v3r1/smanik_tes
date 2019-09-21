package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class TreeDocumentFile extends DocumentFile {
  private Context mContext;
  
  private Uri mUri;
  
  TreeDocumentFile(DocumentFile paramDocumentFile, Context paramContext, Uri paramUri) {
    super(paramDocumentFile);
    this.mContext = paramContext;
    this.mUri = paramUri;
  }
  
  private static void closeQuietly(AutoCloseable paramAutoCloseable) {
    if (paramAutoCloseable != null)
      try {
        paramAutoCloseable.close();
        return;
      } catch (RuntimeException paramAutoCloseable) {
        throw paramAutoCloseable;
      } catch (Exception paramAutoCloseable) {
        return;
      }  
  }
  
  private static Uri createFile(Context paramContext, Uri paramUri, String paramString1, String paramString2) {
    try {
      return DocumentsContract.createDocument(paramContext.getContentResolver(), paramUri, paramString1, paramString2);
    } catch (Exception paramContext) {
      return null;
    } 
  }
  
  public boolean canRead() { return DocumentsContractApi19.canRead(this.mContext, this.mUri); }
  
  public boolean canWrite() { return DocumentsContractApi19.canWrite(this.mContext, this.mUri); }
  
  public DocumentFile createDirectory(String paramString) {
    Uri uri = createFile(this.mContext, this.mUri, "vnd.android.document/directory", paramString);
    return (uri != null) ? new TreeDocumentFile(this, this.mContext, uri) : null;
  }
  
  public DocumentFile createFile(String paramString1, String paramString2) {
    Uri uri = createFile(this.mContext, this.mUri, paramString1, paramString2);
    return (uri != null) ? new TreeDocumentFile(this, this.mContext, uri) : null;
  }
  
  public boolean delete() {
    try {
      return DocumentsContract.deleteDocument(this.mContext.getContentResolver(), this.mUri);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public boolean exists() { return DocumentsContractApi19.exists(this.mContext, this.mUri); }
  
  public String getName() { return DocumentsContractApi19.getName(this.mContext, this.mUri); }
  
  public String getType() { return DocumentsContractApi19.getType(this.mContext, this.mUri); }
  
  public Uri getUri() { return this.mUri; }
  
  public boolean isDirectory() { return DocumentsContractApi19.isDirectory(this.mContext, this.mUri); }
  
  public boolean isFile() { return DocumentsContractApi19.isFile(this.mContext, this.mUri); }
  
  public boolean isVirtual() { return DocumentsContractApi19.isVirtual(this.mContext, this.mUri); }
  
  public long lastModified() { return DocumentsContractApi19.lastModified(this.mContext, this.mUri); }
  
  public long length() { return DocumentsContractApi19.length(this.mContext, this.mUri); }
  
  public DocumentFile[] listFiles() { // Byte code:
    //   0: aload_0
    //   1: getfield mContext : Landroid/content/Context;
    //   4: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
    //   7: astore_3
    //   8: aload_0
    //   9: getfield mUri : Landroid/net/Uri;
    //   12: aload_0
    //   13: getfield mUri : Landroid/net/Uri;
    //   16: invokestatic getDocumentId : (Landroid/net/Uri;)Ljava/lang/String;
    //   19: invokestatic buildChildDocumentsUriUsingTree : (Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   22: astore #6
    //   24: new java/util/ArrayList
    //   27: dup
    //   28: invokespecial <init> : ()V
    //   31: astore #5
    //   33: iconst_0
    //   34: istore_1
    //   35: aconst_null
    //   36: astore #4
    //   38: aconst_null
    //   39: astore_2
    //   40: aload_3
    //   41: aload #6
    //   43: iconst_1
    //   44: anewarray java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: ldc 'document_id'
    //   51: aastore
    //   52: aconst_null
    //   53: aconst_null
    //   54: aconst_null
    //   55: invokevirtual query : (Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   58: astore_3
    //   59: aload_3
    //   60: invokeinterface moveToNext : ()Z
    //   65: ifeq -> 93
    //   68: aload_3
    //   69: iconst_0
    //   70: invokeinterface getString : (I)Ljava/lang/String;
    //   75: astore_2
    //   76: aload #5
    //   78: aload_0
    //   79: getfield mUri : Landroid/net/Uri;
    //   82: aload_2
    //   83: invokestatic buildDocumentUriUsingTree : (Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    //   86: invokevirtual add : (Ljava/lang/Object;)Z
    //   89: pop
    //   90: goto -> 59
    //   93: aload_3
    //   94: invokestatic closeQuietly : (Ljava/lang/AutoCloseable;)V
    //   97: goto -> 174
    //   100: astore #4
    //   102: aload_3
    //   103: astore_2
    //   104: aload #4
    //   106: astore_3
    //   107: goto -> 230
    //   110: astore #4
    //   112: goto -> 126
    //   115: astore_3
    //   116: goto -> 230
    //   119: astore_2
    //   120: aload #4
    //   122: astore_3
    //   123: aload_2
    //   124: astore #4
    //   126: aload_3
    //   127: astore_2
    //   128: new java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial <init> : ()V
    //   135: astore #6
    //   137: aload_3
    //   138: astore_2
    //   139: aload #6
    //   141: ldc 'Failed query: '
    //   143: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: pop
    //   147: aload_3
    //   148: astore_2
    //   149: aload #6
    //   151: aload #4
    //   153: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   156: pop
    //   157: aload_3
    //   158: astore_2
    //   159: ldc 'DocumentFile'
    //   161: aload #6
    //   163: invokevirtual toString : ()Ljava/lang/String;
    //   166: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   169: pop
    //   170: aload_3
    //   171: invokestatic closeQuietly : (Ljava/lang/AutoCloseable;)V
    //   174: aload #5
    //   176: aload #5
    //   178: invokevirtual size : ()I
    //   181: anewarray android/net/Uri
    //   184: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
    //   187: checkcast [Landroid/net/Uri;
    //   190: astore_2
    //   191: aload_2
    //   192: arraylength
    //   193: anewarray android/support/v4/provider/DocumentFile
    //   196: astore_3
    //   197: iload_1
    //   198: aload_2
    //   199: arraylength
    //   200: if_icmpge -> 228
    //   203: aload_3
    //   204: iload_1
    //   205: new android/support/v4/provider/TreeDocumentFile
    //   208: dup
    //   209: aload_0
    //   210: aload_0
    //   211: getfield mContext : Landroid/content/Context;
    //   214: aload_2
    //   215: iload_1
    //   216: aaload
    //   217: invokespecial <init> : (Landroid/support/v4/provider/DocumentFile;Landroid/content/Context;Landroid/net/Uri;)V
    //   220: aastore
    //   221: iload_1
    //   222: iconst_1
    //   223: iadd
    //   224: istore_1
    //   225: goto -> 197
    //   228: aload_3
    //   229: areturn
    //   230: aload_2
    //   231: invokestatic closeQuietly : (Ljava/lang/AutoCloseable;)V
    //   234: aload_3
    //   235: athrow
    // Exception table:
    //   from	to	target	type
    //   40	59	119	java/lang/Exception
    //   40	59	115	finally
    //   59	90	110	java/lang/Exception
    //   59	90	100	finally
    //   128	137	115	finally
    //   139	147	115	finally
    //   149	157	115	finally
    //   159	170	115	finally }
  
  public boolean renameTo(String paramString) {
    try {
      Uri uri = DocumentsContract.renameDocument(this.mContext.getContentResolver(), this.mUri, paramString);
      if (uri != null) {
        this.mUri = uri;
        return true;
      } 
      return false;
    } catch (Exception paramString) {
      return false;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/provider/TreeDocumentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */