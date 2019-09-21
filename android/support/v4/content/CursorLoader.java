package android.support.v4.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.CancellationSignal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class CursorLoader extends AsyncTaskLoader<Cursor> {
  CancellationSignal mCancellationSignal;
  
  Cursor mCursor;
  
  final Loader<Cursor>.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver(this);
  
  String[] mProjection;
  
  String mSelection;
  
  String[] mSelectionArgs;
  
  String mSortOrder;
  
  Uri mUri;
  
  public CursorLoader(@NonNull Context paramContext) { super(paramContext); }
  
  public CursorLoader(@NonNull Context paramContext, @NonNull Uri paramUri, @Nullable String[] paramArrayOfString1, @Nullable String paramString1, @Nullable String[] paramArrayOfString2, @Nullable String paramString2) {
    super(paramContext);
    this.mUri = paramUri;
    this.mProjection = paramArrayOfString1;
    this.mSelection = paramString1;
    this.mSelectionArgs = paramArrayOfString2;
    this.mSortOrder = paramString2;
  }
  
  public void cancelLoadInBackground() { // Byte code:
    //   0: aload_0
    //   1: invokespecial cancelLoadInBackground : ()V
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   10: ifnull -> 20
    //   13: aload_0
    //   14: getfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   17: invokevirtual cancel : ()V
    //   20: aload_0
    //   21: monitorexit
    //   22: return
    //   23: astore_1
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: athrow
    // Exception table:
    //   from	to	target	type
    //   6	20	23	finally
    //   20	22	23	finally
    //   24	26	23	finally }
  
  public void deliverResult(Cursor paramCursor) {
    if (isReset()) {
      if (paramCursor != null)
        paramCursor.close(); 
      return;
    } 
    Cursor cursor = this.mCursor;
    this.mCursor = paramCursor;
    if (isStarted())
      super.deliverResult(paramCursor); 
    if (cursor != null && cursor != paramCursor && !cursor.isClosed())
      cursor.close(); 
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mUri=");
    paramPrintWriter.println(this.mUri);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mProjection=");
    paramPrintWriter.println(Arrays.toString(this.mProjection));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSelection=");
    paramPrintWriter.println(this.mSelection);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSelectionArgs=");
    paramPrintWriter.println(Arrays.toString(this.mSelectionArgs));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSortOrder=");
    paramPrintWriter.println(this.mSortOrder);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mCursor=");
    paramPrintWriter.println(this.mCursor);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mContentChanged=");
    paramPrintWriter.println(this.mContentChanged);
  }
  
  @Nullable
  public String[] getProjection() { return this.mProjection; }
  
  @Nullable
  public String getSelection() { return this.mSelection; }
  
  @Nullable
  public String[] getSelectionArgs() { return this.mSelectionArgs; }
  
  @Nullable
  public String getSortOrder() { return this.mSortOrder; }
  
  @NonNull
  public Uri getUri() { return this.mUri; }
  
  public Cursor loadInBackground() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual isLoadInBackgroundCanceled : ()Z
    //   6: ifeq -> 17
    //   9: new android/support/v4/os/OperationCanceledException
    //   12: dup
    //   13: invokespecial <init> : ()V
    //   16: athrow
    //   17: aload_0
    //   18: new android/support/v4/os/CancellationSignal
    //   21: dup
    //   22: invokespecial <init> : ()V
    //   25: putfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_0
    //   31: invokevirtual getContext : ()Landroid/content/Context;
    //   34: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
    //   37: aload_0
    //   38: getfield mUri : Landroid/net/Uri;
    //   41: aload_0
    //   42: getfield mProjection : [Ljava/lang/String;
    //   45: aload_0
    //   46: getfield mSelection : Ljava/lang/String;
    //   49: aload_0
    //   50: getfield mSelectionArgs : [Ljava/lang/String;
    //   53: aload_0
    //   54: getfield mSortOrder : Ljava/lang/String;
    //   57: aload_0
    //   58: getfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   61: invokestatic query : (Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/support/v4/os/CancellationSignal;)Landroid/database/Cursor;
    //   64: astore_1
    //   65: aload_1
    //   66: ifnull -> 98
    //   69: aload_1
    //   70: invokeinterface getCount : ()I
    //   75: pop
    //   76: aload_1
    //   77: aload_0
    //   78: getfield mObserver : Landroid/support/v4/content/Loader$ForceLoadContentObserver;
    //   81: invokeinterface registerContentObserver : (Landroid/database/ContentObserver;)V
    //   86: goto -> 98
    //   89: astore_2
    //   90: aload_1
    //   91: invokeinterface close : ()V
    //   96: aload_2
    //   97: athrow
    //   98: aload_0
    //   99: monitorenter
    //   100: aload_0
    //   101: aconst_null
    //   102: putfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   105: aload_0
    //   106: monitorexit
    //   107: aload_1
    //   108: areturn
    //   109: astore_1
    //   110: aload_0
    //   111: monitorexit
    //   112: aload_1
    //   113: athrow
    //   114: astore_1
    //   115: aload_0
    //   116: monitorenter
    //   117: aload_0
    //   118: aconst_null
    //   119: putfield mCancellationSignal : Landroid/support/v4/os/CancellationSignal;
    //   122: aload_0
    //   123: monitorexit
    //   124: aload_1
    //   125: athrow
    //   126: astore_1
    //   127: aload_0
    //   128: monitorexit
    //   129: aload_1
    //   130: athrow
    //   131: astore_1
    //   132: aload_0
    //   133: monitorexit
    //   134: aload_1
    //   135: athrow
    // Exception table:
    //   from	to	target	type
    //   2	17	131	finally
    //   17	30	131	finally
    //   30	65	114	finally
    //   69	86	89	java/lang/RuntimeException
    //   69	86	114	finally
    //   90	98	114	finally
    //   100	107	109	finally
    //   110	112	109	finally
    //   117	124	126	finally
    //   127	129	126	finally
    //   132	134	131	finally }
  
  public void onCanceled(Cursor paramCursor) {
    if (paramCursor != null && !paramCursor.isClosed())
      paramCursor.close(); 
  }
  
  protected void onReset() {
    super.onReset();
    onStopLoading();
    if (this.mCursor != null && !this.mCursor.isClosed())
      this.mCursor.close(); 
    this.mCursor = null;
  }
  
  protected void onStartLoading() {
    if (this.mCursor != null)
      deliverResult(this.mCursor); 
    if (takeContentChanged() || this.mCursor == null)
      forceLoad(); 
  }
  
  protected void onStopLoading() { cancelLoad(); }
  
  public void setProjection(@Nullable String[] paramArrayOfString) { this.mProjection = paramArrayOfString; }
  
  public void setSelection(@Nullable String paramString) { this.mSelection = paramString; }
  
  public void setSelectionArgs(@Nullable String[] paramArrayOfString) { this.mSelectionArgs = paramArrayOfString; }
  
  public void setSortOrder(@Nullable String paramString) { this.mSortOrder = paramString; }
  
  public void setUri(@NonNull Uri paramUri) { this.mUri = paramUri; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/CursorLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */