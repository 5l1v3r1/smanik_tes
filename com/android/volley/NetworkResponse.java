package com.android.volley;

import java.util.Collections;
import java.util.Map;

public class NetworkResponse {
  public final byte[] data;
  
  public final Map<String, String> headers;
  
  public final long networkTimeMs;
  
  public final boolean notModified;
  
  public final int statusCode;
  
  public NetworkResponse(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, boolean paramBoolean) { this(paramInt, paramArrayOfByte, paramMap, paramBoolean, 0L); }
  
  public NetworkResponse(int paramInt, byte[] paramArrayOfByte, Map<String, String> paramMap, boolean paramBoolean, long paramLong) {
    this.statusCode = paramInt;
    this.data = paramArrayOfByte;
    this.headers = paramMap;
    this.notModified = paramBoolean;
    this.networkTimeMs = paramLong;
  }
  
  public NetworkResponse(byte[] paramArrayOfByte) { this(200, paramArrayOfByte, Collections.emptyMap(), false, 0L); }
  
  public NetworkResponse(byte[] paramArrayOfByte, Map<String, String> paramMap) { this(200, paramArrayOfByte, paramMap, false, 0L); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/NetworkResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */