package android.arch.persistence.db;

public interface SupportSQLiteStatement extends SupportSQLiteProgram {
  void execute();
  
  long executeInsert();
  
  int executeUpdateDelete();
  
  long simpleQueryForLong();
  
  String simpleQueryForString();
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/SupportSQLiteStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */