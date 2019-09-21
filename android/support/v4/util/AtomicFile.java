package android.support.v4.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AtomicFile {
  private final File mBackupName;
  
  private final File mBaseName;
  
  public AtomicFile(@NonNull File paramFile) {
    this.mBaseName = paramFile;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramFile.getPath());
    stringBuilder.append(".bak");
    this.mBackupName = new File(stringBuilder.toString());
  }
  
  private static boolean sync(@NonNull FileOutputStream paramFileOutputStream) {
    try {
      paramFileOutputStream.getFD().sync();
      return true;
    } catch (IOException paramFileOutputStream) {
      return false;
    } 
  }
  
  public void delete() {
    this.mBaseName.delete();
    this.mBackupName.delete();
  }
  
  public void failWrite(@Nullable FileOutputStream paramFileOutputStream) {
    if (paramFileOutputStream != null) {
      sync(paramFileOutputStream);
      try {
        paramFileOutputStream.close();
        this.mBaseName.delete();
        this.mBackupName.renameTo(this.mBaseName);
        return;
      } catch (IOException paramFileOutputStream) {
        Log.w("AtomicFile", "failWrite: Got exception:", paramFileOutputStream);
      } 
    } 
  }
  
  public void finishWrite(@Nullable FileOutputStream paramFileOutputStream) {
    if (paramFileOutputStream != null) {
      sync(paramFileOutputStream);
      try {
        paramFileOutputStream.close();
        this.mBackupName.delete();
        return;
      } catch (IOException paramFileOutputStream) {
        Log.w("AtomicFile", "finishWrite: Got exception:", paramFileOutputStream);
      } 
    } 
  }
  
  @NonNull
  public File getBaseFile() { return this.mBaseName; }
  
  @NonNull
  public FileInputStream openRead() throws FileNotFoundException {
    if (this.mBackupName.exists()) {
      this.mBaseName.delete();
      this.mBackupName.renameTo(this.mBaseName);
    } 
    return new FileInputStream(this.mBaseName);
  }
  
  @NonNull
  public byte[] readFully() throws IOException {
    fileInputStream = openRead();
    try {
      arrayOfByte = new byte[fileInputStream.available()];
      int i = 0;
      while (true) {
        int j = fileInputStream.read(arrayOfByte, i, arrayOfByte.length - i);
        if (j <= 0)
          return arrayOfByte; 
        j = i + j;
        int k = fileInputStream.available();
        i = j;
        if (k > arrayOfByte.length - j) {
          byte[] arrayOfByte1 = new byte[k + j];
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, j);
          arrayOfByte = arrayOfByte1;
          i = j;
        } 
      } 
    } finally {
      fileInputStream.close();
    } 
  }
  
  @NonNull
  public FileOutputStream startWrite() throws IOException {
    if (this.mBaseName.exists())
      if (!this.mBackupName.exists()) {
        if (!this.mBaseName.renameTo(this.mBackupName)) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Couldn't rename file ");
          stringBuilder.append(this.mBaseName);
          stringBuilder.append(" to backup file ");
          stringBuilder.append(this.mBackupName);
          Log.w("AtomicFile", stringBuilder.toString());
        } 
      } else {
        this.mBaseName.delete();
      }  
    try {
      return new FileOutputStream(this.mBaseName);
    } catch (FileNotFoundException fileNotFoundException) {
      if (!this.mBaseName.getParentFile().mkdirs()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't create directory ");
        stringBuilder.append(this.mBaseName);
        throw new IOException(stringBuilder.toString());
      } 
      try {
        return new FileOutputStream(this.mBaseName);
      } catch (FileNotFoundException fileNotFoundException) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't create ");
        stringBuilder.append(this.mBaseName);
        throw new IOException(stringBuilder.toString());
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/AtomicFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */