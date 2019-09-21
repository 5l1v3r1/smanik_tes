package android.support.v4.util;

class ContainerHelpers {
  static final int[] EMPTY_INTS = new int[0];
  
  static final long[] EMPTY_LONGS = new long[0];
  
  static final Object[] EMPTY_OBJECTS = new Object[0];
  
  static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    paramInt1--;
    int i = 0;
    while (i <= paramInt1) {
      int j = i + paramInt1 >>> 1;
      int k = paramArrayOfInt[j];
      if (k < paramInt2) {
        i = j + 1;
        continue;
      } 
      if (k > paramInt2) {
        paramInt1 = j - 1;
        continue;
      } 
      return j;
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  static int binarySearch(long[] paramArrayOfLong, int paramInt, long paramLong) {
    paramInt--;
    int i = 0;
    while (i <= paramInt) {
      int j = i + paramInt >>> 1;
      long l = paramArrayOfLong[j];
      if (l < paramLong) {
        i = j + 1;
        continue;
      } 
      if (l > paramLong) {
        paramInt = j - 1;
        continue;
      } 
      return j;
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  public static boolean equal(Object paramObject1, Object paramObject2) { return (paramObject1 == paramObject2 || (paramObject1 != null && paramObject1.equals(paramObject2))); }
  
  public static int idealByteArraySize(int paramInt) {
    for (byte b = 4; b < 32; b++) {
      byte b1 = (1 << b) - 12;
      if (paramInt <= b1)
        return b1; 
    } 
    return paramInt;
  }
  
  public static int idealIntArraySize(int paramInt) { return idealByteArraySize(paramInt * 4) / 4; }
  
  public static int idealLongArraySize(int paramInt) { return idealByteArraySize(paramInt * 8) / 8; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/ContainerHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */