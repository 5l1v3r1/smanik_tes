package com.android.volley;

import java.util.Collections;
import java.util.Map;

public interface Cache {
  void clear();
  
  Entry get(String paramString);
  
  void initialize();
  
  void invalidate(String paramString, boolean paramBoolean);
  
  void put(String paramString, Entry paramEntry);
  
  void remove(String paramString);
  
  public static class Entry {
    public byte[] data;
    
    public String etag;
    
    public long lastModified;
    
    public Map<String, String> responseHeaders = Collections.emptyMap();
    
    public long serverDate;
    
    public long softTtl;
    
    public long ttl;
    
    public boolean isExpired() { return (this.ttl < System.currentTimeMillis()); }
    
    public boolean refreshNeeded() { return (this.softTtl < System.currentTimeMillis()); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */