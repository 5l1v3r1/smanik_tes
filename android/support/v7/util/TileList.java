package android.support.v7.util;

import android.util.SparseArray;
import java.lang.reflect.Array;

class TileList<T> extends Object {
  Tile<T> mLastAccessedTile;
  
  final int mTileSize;
  
  private final SparseArray<Tile<T>> mTiles = new SparseArray(10);
  
  public TileList(int paramInt) { this.mTileSize = paramInt; }
  
  public Tile<T> addOrReplace(Tile<T> paramTile) {
    int i = this.mTiles.indexOfKey(paramTile.mStartPosition);
    if (i < 0) {
      this.mTiles.put(paramTile.mStartPosition, paramTile);
      return null;
    } 
    Tile tile = (Tile)this.mTiles.valueAt(i);
    this.mTiles.setValueAt(i, paramTile);
    if (this.mLastAccessedTile == tile)
      this.mLastAccessedTile = paramTile; 
    return tile;
  }
  
  public void clear() { this.mTiles.clear(); }
  
  public Tile<T> getAtIndex(int paramInt) { return (Tile)this.mTiles.valueAt(paramInt); }
  
  public T getItemAt(int paramInt) {
    if (this.mLastAccessedTile == null || !this.mLastAccessedTile.containsPosition(paramInt)) {
      int i = this.mTileSize;
      i = this.mTiles.indexOfKey(paramInt - paramInt % i);
      if (i < 0)
        return null; 
      this.mLastAccessedTile = (Tile)this.mTiles.valueAt(i);
    } 
    return (T)this.mLastAccessedTile.getByPosition(paramInt);
  }
  
  public Tile<T> removeAtPos(int paramInt) {
    Tile tile = (Tile)this.mTiles.get(paramInt);
    if (this.mLastAccessedTile == tile)
      this.mLastAccessedTile = null; 
    this.mTiles.delete(paramInt);
    return tile;
  }
  
  public int size() { return this.mTiles.size(); }
  
  public static class Tile<T> extends Object {
    public int mItemCount;
    
    public final T[] mItems;
    
    Tile<T> mNext;
    
    public int mStartPosition;
    
    public Tile(Class<T> param1Class, int param1Int) { this.mItems = (Object[])Array.newInstance(param1Class, param1Int); }
    
    boolean containsPosition(int param1Int) { return (this.mStartPosition <= param1Int && param1Int < this.mStartPosition + this.mItemCount); }
    
    T getByPosition(int param1Int) { return (T)this.mItems[param1Int - this.mStartPosition]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/util/TileList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */