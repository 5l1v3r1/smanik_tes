package android.support.v4.content;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

public final class ContentResolverCompat {
  public static Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, CancellationSignal paramCancellationSignal) {
    if (Build.VERSION.SDK_INT >= 16) {
      if (paramCancellationSignal != null)
        try {
          object = paramCancellationSignal.getCancellationSignalObject();
          return paramContentResolver.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, (CancellationSignal)object);
        } catch (Exception paramContentResolver) {
          if (paramContentResolver instanceof android.os.OperationCanceledException)
            throw new OperationCanceledException(); 
          throw paramContentResolver;
        }  
    } else {
      if (object != null)
        object.throwIfCanceled(); 
      return paramContentResolver.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    } 
    Object object = null;
    return paramContentResolver.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, (CancellationSignal)object);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/ContentResolverCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */