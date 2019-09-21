package android.arch.persistence.db.framework;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.database.sqlite.SQLiteStatement;

class FrameworkSQLiteStatement implements SupportSQLiteStatement {
  private final SQLiteStatement mDelegate;
  
  FrameworkSQLiteStatement(SQLiteStatement paramSQLiteStatement) { this.mDelegate = paramSQLiteStatement; }
  
  public void bindBlob(int paramInt, byte[] paramArrayOfByte) { this.mDelegate.bindBlob(paramInt, paramArrayOfByte); }
  
  public void bindDouble(int paramInt, double paramDouble) { this.mDelegate.bindDouble(paramInt, paramDouble); }
  
  public void bindLong(int paramInt, long paramLong) { this.mDelegate.bindLong(paramInt, paramLong); }
  
  public void bindNull(int paramInt) { this.mDelegate.bindNull(paramInt); }
  
  public void bindString(int paramInt, String paramString) { this.mDelegate.bindString(paramInt, paramString); }
  
  public void clearBindings() { this.mDelegate.clearBindings(); }
  
  public void close() { this.mDelegate.close(); }
  
  public void execute() { this.mDelegate.execute(); }
  
  public long executeInsert() { return this.mDelegate.executeInsert(); }
  
  public int executeUpdateDelete() { return this.mDelegate.executeUpdateDelete(); }
  
  public long simpleQueryForLong() { return this.mDelegate.simpleQueryForLong(); }
  
  public String simpleQueryForString() { return this.mDelegate.simpleQueryForString(); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/framework/FrameworkSQLiteStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */