package android.arch.persistence.room;

import android.arch.persistence.db.SupportSQLiteProgram;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class RoomSQLiteQuery implements SupportSQLiteQuery, SupportSQLiteProgram {
  private static final int BLOB = 5;
  
  @VisibleForTesting
  static final int DESIRED_POOL_SIZE = 10;
  
  private static final int DOUBLE = 3;
  
  private static final int LONG = 2;
  
  private static final int NULL = 1;
  
  @VisibleForTesting
  static final int POOL_LIMIT = 15;
  
  private static final int STRING = 4;
  
  @VisibleForTesting
  static final TreeMap<Integer, RoomSQLiteQuery> sQueryPool = new TreeMap();
  
  @VisibleForTesting
  int mArgCount;
  
  private final int[] mBindingTypes;
  
  @VisibleForTesting
  final byte[][] mBlobBindings;
  
  @VisibleForTesting
  final int mCapacity;
  
  @VisibleForTesting
  final double[] mDoubleBindings;
  
  @VisibleForTesting
  final long[] mLongBindings;
  
  @VisibleForTesting
  final String[] mStringBindings;
  
  private RoomSQLiteQuery(int paramInt) {
    this.mCapacity = paramInt;
    this.mBindingTypes = new int[++paramInt];
    this.mLongBindings = new long[paramInt];
    this.mDoubleBindings = new double[paramInt];
    this.mStringBindings = new String[paramInt];
    this.mBlobBindings = new byte[paramInt][];
  }
  
  public static RoomSQLiteQuery acquire(String paramString, int paramInt) {
    synchronized (sQueryPool) {
      Map.Entry entry = sQueryPool.ceilingEntry(Integer.valueOf(paramInt));
      if (entry != null) {
        sQueryPool.remove(entry.getKey());
        RoomSQLiteQuery roomSQLiteQuery = (RoomSQLiteQuery)entry.getValue();
        roomSQLiteQuery.init(paramString, paramInt);
        return roomSQLiteQuery;
      } 
      null = new RoomSQLiteQuery(paramInt);
      null.init(paramString, paramInt);
      return null;
    } 
  }
  
  private static void prunePoolLocked() {
    if (sQueryPool.size() > 15) {
      int i = sQueryPool.size() - 10;
      Iterator iterator = sQueryPool.descendingKeySet().iterator();
      while (i > 0) {
        iterator.next();
        iterator.remove();
        i--;
      } 
    } 
  }
  
  public void bindBlob(int paramInt, byte[] paramArrayOfByte) {
    this.mBindingTypes[paramInt] = 5;
    this.mBlobBindings[paramInt] = paramArrayOfByte;
  }
  
  public void bindDouble(int paramInt, double paramDouble) {
    this.mBindingTypes[paramInt] = 3;
    this.mDoubleBindings[paramInt] = paramDouble;
  }
  
  public void bindLong(int paramInt, long paramLong) {
    this.mBindingTypes[paramInt] = 2;
    this.mLongBindings[paramInt] = paramLong;
  }
  
  public void bindNull(int paramInt) { this.mBindingTypes[paramInt] = 1; }
  
  public void bindString(int paramInt, String paramString) {
    this.mBindingTypes[paramInt] = 4;
    this.mStringBindings[paramInt] = paramString;
  }
  
  public void bindTo(SupportSQLiteProgram paramSupportSQLiteProgram) {
    for (byte b = 1; b <= this.mArgCount; b++) {
      switch (this.mBindingTypes[b]) {
        case 5:
          paramSupportSQLiteProgram.bindBlob(b, this.mBlobBindings[b]);
          break;
        case 4:
          paramSupportSQLiteProgram.bindString(b, this.mStringBindings[b]);
          break;
        case 3:
          paramSupportSQLiteProgram.bindDouble(b, this.mDoubleBindings[b]);
          break;
        case 2:
          paramSupportSQLiteProgram.bindLong(b, this.mLongBindings[b]);
          break;
        case 1:
          paramSupportSQLiteProgram.bindNull(b);
          break;
      } 
    } 
  }
  
  public void clearBindings() {
    Arrays.fill(this.mBindingTypes, 1);
    Arrays.fill(this.mStringBindings, null);
    Arrays.fill(this.mBlobBindings, null);
    this.mQuery = null;
  }
  
  public void close() {}
  
  public void copyArgumentsFrom(RoomSQLiteQuery paramRoomSQLiteQuery) {
    int i = paramRoomSQLiteQuery.getArgCount() + 1;
    System.arraycopy(paramRoomSQLiteQuery.mBindingTypes, 0, this.mBindingTypes, 0, i);
    System.arraycopy(paramRoomSQLiteQuery.mLongBindings, 0, this.mLongBindings, 0, i);
    System.arraycopy(paramRoomSQLiteQuery.mStringBindings, 0, this.mStringBindings, 0, i);
    System.arraycopy(paramRoomSQLiteQuery.mBlobBindings, 0, this.mBlobBindings, 0, i);
    System.arraycopy(paramRoomSQLiteQuery.mDoubleBindings, 0, this.mDoubleBindings, 0, i);
  }
  
  public int getArgCount() { return this.mArgCount; }
  
  public String getSql() { return this.mQuery; }
  
  void init(String paramString, int paramInt) {
    this.mQuery = paramString;
    this.mArgCount = paramInt;
  }
  
  public void release() {
    synchronized (sQueryPool) {
      sQueryPool.put(Integer.valueOf(this.mCapacity), this);
      prunePoolLocked();
      return;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/RoomSQLiteQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */