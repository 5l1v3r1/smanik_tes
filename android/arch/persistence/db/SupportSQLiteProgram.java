package android.arch.persistence.db;

import android.annotation.TargetApi;

@TargetApi(19)
public interface SupportSQLiteProgram extends AutoCloseable {
  void bindBlob(int paramInt, byte[] paramArrayOfByte);
  
  void bindDouble(int paramInt, double paramDouble);
  
  void bindLong(int paramInt, long paramLong);
  
  void bindNull(int paramInt);
  
  void bindString(int paramInt, String paramString);
  
  void clearBindings();
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/SupportSQLiteProgram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */