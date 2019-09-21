package android.support.v4.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.provider.FontsContractCompat;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RequiresApi(21)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl {
  private static final String TAG = "TypefaceCompatApi21Impl";
  
  private File getFile(ParcelFileDescriptor paramParcelFileDescriptor) {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("/proc/self/fd/");
      stringBuilder.append(paramParcelFileDescriptor.getFd());
      String str = Os.readlink(stringBuilder.toString());
      return OsConstants.S_ISREG((Os.stat(str)).st_mode) ? new File(str) : null;
    } catch (ErrnoException paramParcelFileDescriptor) {
      return null;
    } 
  }
  
  public Typeface createFromFontInfo(Context paramContext, CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt) {
    if (paramArrayOfFontInfo.length < 1)
      return null; 
    FontsContractCompat.FontInfo fontInfo = findBestInfo(paramArrayOfFontInfo, paramInt);
    ContentResolver contentResolver = paramContext.getContentResolver();
    try {
      parcelFileDescriptor = contentResolver.openFileDescriptor(fontInfo.getUri(), "r", paramCancellationSignal);
      try {
        file = getFile(parcelFileDescriptor);
        if (file == null || !file.canRead()) {
          fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
          try {
            return createFromInputStream(paramContext, fileInputStream);
          } catch (Throwable file) {
            try {
              throw file;
            } finally {}
          } finally {
            paramContext = null;
          } 
          if (fileInputStream != null)
            if (file != null) {
              try {
                fileInputStream.close();
              } catch (Throwable fileInputStream) {
                file.addSuppressed(fileInputStream);
              } 
            } else {
              fileInputStream.close();
            }  
          throw paramContext;
        } 
        return Typeface.createFromFile(file);
      } catch (Throwable paramCancellationSignal) {
        try {
          throw paramCancellationSignal;
        } finally {}
      } finally {
        paramContext = null;
      } 
      if (parcelFileDescriptor != null)
        if (paramCancellationSignal != null) {
          try {
            parcelFileDescriptor.close();
          } catch (Throwable parcelFileDescriptor) {
            paramCancellationSignal.addSuppressed(parcelFileDescriptor);
          } 
        } else {
          parcelFileDescriptor.close();
        }  
      throw paramContext;
    } catch (IOException paramContext) {
      return null;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatApi21Impl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */