package com.android.volley.toolbox;

import com.android.volley.Cache;

public class NoCache implements Cache {
  public void clear() {}
  
  public Cache.Entry get(String paramString) { return null; }
  
  public void initialize() {}
  
  public void invalidate(String paramString, boolean paramBoolean) {}
  
  public void put(String paramString, Cache.Entry paramEntry) {}
  
  public void remove(String paramString) {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/NoCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */