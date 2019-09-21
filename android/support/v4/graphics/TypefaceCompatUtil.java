package android.support.v4.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil {
  private static final String CACHE_FILE_PREFIX = ".font";
  
  private static final String TAG = "TypefaceCompatUtil";
  
  public static void closeQuietly(Closeable paramCloseable) {
    if (paramCloseable != null)
      try {
        paramCloseable.close();
        return;
      } catch (IOException paramCloseable) {
        return;
      }  
  }
  
  @Nullable
  @RequiresApi(19)
  public static ByteBuffer copyToDirectBuffer(Context paramContext, Resources paramResources, int paramInt) {
    file = getTempFile(paramContext);
    if (file == null)
      return null; 
    try {
      boolean bool = copyToFile(file, paramResources, paramInt);
      if (!bool)
        return null; 
      return mmap(file);
    } finally {
      file.delete();
    } 
  }
  
  public static boolean copyToFile(File paramFile, Resources paramResources, int paramInt) {
    try {
      inputStream = paramResources.openRawResource(paramInt);
    } finally {
      paramFile = null;
    } 
    closeQuietly(paramResources);
    throw paramFile;
  }
  
  public static boolean copyToFile(File paramFile, InputStream paramInputStream) {
    File file2 = null;
    arrayOfByte = null;
    try {
      fileOutputStream = new FileOutputStream(paramFile, false);
      try {
        arrayOfByte = new byte[1024];
        return true;
      } catch (IOException paramInputStream) {
      
      } finally {
        InputStream inputStream;
        paramInputStream = null;
        arrayOfByte = fileOutputStream;
      } 
    } catch (IOException paramInputStream) {
      paramFile = file2;
    } finally {}
    File file1 = paramFile;
    StringBuilder stringBuilder = new StringBuilder();
    file1 = paramFile;
    stringBuilder.append("Error copying resource contents to temp file: ");
    file1 = paramFile;
    stringBuilder.append(paramInputStream.getMessage());
    file1 = paramFile;
    Log.e("TypefaceCompatUtil", stringBuilder.toString());
    closeQuietly(paramFile);
    return false;
  }
  
  @Nullable
  public static File getTempFile(Context paramContext) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(".font");
    stringBuilder.append(Process.myPid());
    stringBuilder.append("-");
    stringBuilder.append(Process.myTid());
    stringBuilder.append("-");
    String str = stringBuilder.toString();
    byte b = 0;
    while (true) {
      if (b < 100) {
        file = paramContext.getCacheDir();
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str);
        stringBuilder1.append(b);
        file = new File(file, stringBuilder1.toString());
        try {
          boolean bool = file.createNewFile();
          if (bool)
            return file; 
        } catch (IOException file) {}
        b++;
        continue;
      } 
      return null;
    } 
  }
  
  @Nullable
  @RequiresApi(19)
  public static ByteBuffer mmap(Context paramContext, CancellationSignal paramCancellationSignal, Uri paramUri) {
    contentResolver = paramContext.getContentResolver();
    try {
      parcelFileDescriptor = contentResolver.openFileDescriptor(paramUri, "r", paramCancellationSignal);
      if (parcelFileDescriptor == null) {
        if (parcelFileDescriptor != null)
          parcelFileDescriptor.close(); 
        return null;
      } 
      try {
        fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
        try {
          FileChannel fileChannel = fileInputStream.getChannel();
          long l = fileChannel.size();
          return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, l);
        } catch (Throwable contentResolver) {
          try {
            throw contentResolver;
          } finally {}
        } finally {
          paramCancellationSignal = null;
        } 
        if (fileInputStream != null)
          if (contentResolver != null) {
            try {
              fileInputStream.close();
            } catch (Throwable fileInputStream) {
              contentResolver.addSuppressed(fileInputStream);
            } 
          } else {
            fileInputStream.close();
          }  
        throw paramCancellationSignal;
      } catch (Throwable paramCancellationSignal) {
        try {
          throw paramCancellationSignal;
        } finally {}
      } finally {
        contentResolver = null;
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
      throw contentResolver;
    } catch (IOException contentResolver) {
      return null;
    } 
  }
  
  @Nullable
  @RequiresApi(19)
  private static ByteBuffer mmap(File paramFile) {
    try {
      ContentResolver contentResolver;
      fileInputStream = new FileInputStream(paramFile);
      try {
        FileChannel fileChannel = fileInputStream.getChannel();
        long l = fileChannel.size();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, l);
      } catch (Throwable paramFile) {
        try {
          throw paramFile;
        } finally {}
      } finally {
        contentResolver = null;
      } 
      if (fileInputStream != null)
        if (paramFile != null) {
          try {
            fileInputStream.close();
          } catch (Throwable fileInputStream) {
            paramFile.addSuppressed(fileInputStream);
          } 
        } else {
          fileInputStream.close();
        }  
      throw contentResolver;
    } catch (IOException paramFile) {
      return null;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */