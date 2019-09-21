package android.arch.persistence.room.paging;

import android.arch.paging.TiledDataSource;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.util.List;
import java.util.Set;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public abstract class LimitOffsetDataSource<T> extends TiledDataSource<T> {
  private final String mCountQuery;
  
  private final RoomDatabase mDb;
  
  private final boolean mInTransaction;
  
  private final String mLimitOffsetQuery;
  
  private final InvalidationTracker.Observer mObserver;
  
  private final RoomSQLiteQuery mSourceQuery;
  
  protected LimitOffsetDataSource(RoomDatabase paramRoomDatabase, RoomSQLiteQuery paramRoomSQLiteQuery, boolean paramBoolean, String... paramVarArgs) {
    this.mDb = paramRoomDatabase;
    this.mSourceQuery = paramRoomSQLiteQuery;
    this.mInTransaction = paramBoolean;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT COUNT(*) FROM ( ");
    stringBuilder.append(this.mSourceQuery.getSql());
    stringBuilder.append(" )");
    this.mCountQuery = stringBuilder.toString();
    stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT * FROM ( ");
    stringBuilder.append(this.mSourceQuery.getSql());
    stringBuilder.append(" ) LIMIT ? OFFSET ?");
    this.mLimitOffsetQuery = stringBuilder.toString();
    this.mObserver = new InvalidationTracker.Observer(paramVarArgs) {
        public void onInvalidated(@NonNull Set<String> param1Set) { LimitOffsetDataSource.this.invalidate(); }
      };
    paramRoomDatabase.getInvalidationTracker().addWeakObserver(this.mObserver);
  }
  
  protected abstract List<T> convertRows(Cursor paramCursor);
  
  public int countItems() {
    roomSQLiteQuery = RoomSQLiteQuery.acquire(this.mCountQuery, this.mSourceQuery.getArgCount());
    roomSQLiteQuery.copyArgumentsFrom(this.mSourceQuery);
    cursor = this.mDb.query(roomSQLiteQuery);
    try {
      if (cursor.moveToFirst())
        return cursor.getInt(0); 
      return 0;
    } finally {
      cursor.close();
      roomSQLiteQuery.release();
    } 
  }
  
  public boolean isInvalid() {
    this.mDb.getInvalidationTracker().refreshVersionsSync();
    return super.isInvalid();
  }
  
  @Nullable
  public List<T> loadRange(int paramInt1, int paramInt2) {
    roomSQLiteQuery = RoomSQLiteQuery.acquire(this.mLimitOffsetQuery, this.mSourceQuery.getArgCount() + 2);
    roomSQLiteQuery.copyArgumentsFrom(this.mSourceQuery);
    roomSQLiteQuery.bindLong(roomSQLiteQuery.getArgCount() - 1, paramInt2);
    roomSQLiteQuery.bindLong(roomSQLiteQuery.getArgCount(), paramInt1);
    if (this.mInTransaction) {
      this.mDb.beginTransaction();
      try {
        object = this.mDb.query(roomSQLiteQuery);
      } finally {
        stringBuilder = null;
      } 
      if (object != null)
        object.close(); 
      this.mDb.endTransaction();
      roomSQLiteQuery.release();
      throw stringBuilder;
    } 
    cursor = this.mDb.query(roomSQLiteQuery);
    try {
      return convertRows(cursor);
    } finally {
      cursor.close();
      roomSQLiteQuery.release();
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/paging/LimitOffsetDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */