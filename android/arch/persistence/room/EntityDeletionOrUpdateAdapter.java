package android.arch.persistence.room;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.support.annotation.RestrictTo;
import java.util.Iterator;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public abstract class EntityDeletionOrUpdateAdapter<T> extends SharedSQLiteStatement {
  public EntityDeletionOrUpdateAdapter(RoomDatabase paramRoomDatabase) { super(paramRoomDatabase); }
  
  protected abstract void bind(SupportSQLiteStatement paramSupportSQLiteStatement, T paramT);
  
  protected abstract String createQuery();
  
  public final int handle(T paramT) {
    supportSQLiteStatement = acquire();
    try {
      bind(supportSQLiteStatement, paramT);
      return supportSQLiteStatement.executeUpdateDelete();
    } finally {
      release(supportSQLiteStatement);
    } 
  }
  
  public final int handleMultiple(Iterable<T> paramIterable) {
    supportSQLiteStatement = acquire();
    i = 0;
    try {
      Iterator iterator = paramIterable.iterator();
      while (iterator.hasNext()) {
        bind(supportSQLiteStatement, iterator.next());
        int j = supportSQLiteStatement.executeUpdateDelete();
        i += j;
      } 
      return i;
    } finally {
      release(supportSQLiteStatement);
    } 
  }
  
  public final int handleMultiple(T[] paramArrayOfT) {
    supportSQLiteStatement = acquire();
    try {
      int j = paramArrayOfT.length;
      byte b = 0;
      i = 0;
      while (b < j) {
        bind(supportSQLiteStatement, paramArrayOfT[b]);
        int k = supportSQLiteStatement.executeUpdateDelete();
        i += k;
        b++;
      } 
      return i;
    } finally {
      release(supportSQLiteStatement);
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/EntityDeletionOrUpdateAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */