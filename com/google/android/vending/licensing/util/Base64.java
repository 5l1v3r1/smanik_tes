package com.google.android.vending.licensing.util;

public class Base64 {
  private static final byte[] ALPHABET = { 
      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
      121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
      56, 57, 43, 47 };
  
  private static final byte[] DECODABET;
  
  public static final boolean DECODE = false;
  
  public static final boolean ENCODE = true;
  
  private static final byte EQUALS_SIGN = 61;
  
  private static final byte EQUALS_SIGN_ENC = -1;
  
  private static final byte NEW_LINE = 10;
  
  private static final byte[] WEBSAFE_ALPHABET = { 
      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
      121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
      56, 57, 45, 95 };
  
  private static final byte[] WEBSAFE_DECODABET;
  
  private static final byte WHITE_SPACE_ENC = -5;
  
  static  {
    DECODABET = new byte[] { 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -9, -9, -9, -9, -9 };
    WEBSAFE_DECODABET = new byte[] { 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -9, -9, -9, -9, -9 };
  }
  
  public static byte[] decode(String paramString) throws Base64DecoderException {
    byte[] arrayOfByte = paramString.getBytes();
    return decode(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public static byte[] decode(byte[] paramArrayOfByte) throws Base64DecoderException { return decode(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public static byte[] decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws Base64DecoderException { return decode(paramArrayOfByte, paramInt1, paramInt2, DECODABET); }
  
  public static byte[] decode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2) throws Base64DecoderException {
    StringBuilder stringBuilder;
    byte[] arrayOfByte1 = new byte[paramInt2 * 3 / 4 + 2];
    byte[] arrayOfByte2 = new byte[4];
    int k = 0;
    int j = 0;
    int i = 0;
    while (k < paramInt2) {
      StringBuilder stringBuilder1;
      int m = k + paramInt1;
      byte b1 = (byte)(paramArrayOfByte1[m] & 0x7F);
      byte b2 = paramArrayOfByte2[b1];
      if (b2 >= -5) {
        if (b2 >= -1) {
          if (b1 == 61) {
            m = paramInt2 - k;
            paramInt1 = (byte)(paramArrayOfByte1[paramInt2 - 1 + paramInt1] & 0x7F);
            if (!j || j == 1) {
              stringBuilder1 = new StringBuilder();
              stringBuilder1.append("invalid padding byte '=' at byte offset ");
              stringBuilder1.append(k);
              throw new Base64DecoderException(stringBuilder1.toString());
            } 
            if ((j == 3 && m > 2) || (j == 4 && m > 1)) {
              stringBuilder1 = new StringBuilder();
              stringBuilder1.append("padding byte '=' falsely signals end of encoded value at offset ");
              stringBuilder1.append(k);
              throw new Base64DecoderException(stringBuilder1.toString());
            } 
            if (paramInt1 != 61 && paramInt1 != 10)
              throw new Base64DecoderException("encoded value has invalid trailing byte"); 
            break;
          } 
          m = j + true;
          arrayOfByte2[j] = b1;
          if (m == 4) {
            i += decode4to3(arrayOfByte2, 0, arrayOfByte1, i, paramArrayOfByte2);
            j = 0;
          } else {
            j = m;
          } 
        } 
        k++;
        continue;
      } 
      stringBuilder = new StringBuilder();
      stringBuilder.append("Bad Base64 input character at ");
      stringBuilder.append(k);
      stringBuilder.append(": ");
      stringBuilder.append(stringBuilder1[m]);
      stringBuilder.append("(decimal)");
      throw new Base64DecoderException(stringBuilder.toString());
    } 
    if (j != 0) {
      if (j == 1) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("single trailing character at offset ");
        stringBuilder1.append(paramInt2 - 1);
        throw new Base64DecoderException(stringBuilder1.toString());
      } 
      arrayOfByte2[j] = 61;
      i += decode4to3(arrayOfByte2, 0, arrayOfByte1, i, stringBuilder);
    } 
    paramArrayOfByte1 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, paramArrayOfByte1, 0, i);
    return paramArrayOfByte1;
  }
  
  private static int decode4to3(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3) {
    int i = paramInt1 + 2;
    if (paramArrayOfByte1[i] == 61) {
      i = paramArrayOfByte3[paramArrayOfByte1[paramInt1]];
      paramArrayOfByte2[paramInt2] = (byte)((paramArrayOfByte3[paramArrayOfByte1[paramInt1 + 1]] << 24 >>> 12 | i << 24 >>> 6) >>> 16);
      return 1;
    } 
    int j = paramInt1 + 3;
    if (paramArrayOfByte1[j] == 61) {
      j = paramArrayOfByte3[paramArrayOfByte1[paramInt1]];
      paramInt1 = paramArrayOfByte3[paramArrayOfByte1[paramInt1 + 1]];
      paramInt1 = paramArrayOfByte3[paramArrayOfByte1[i]] << 24 >>> 18 | paramInt1 << 24 >>> 12 | j << 24 >>> 6;
      paramArrayOfByte2[paramInt2] = (byte)(paramInt1 >>> 16);
      paramArrayOfByte2[paramInt2 + 1] = (byte)(paramInt1 >>> 8);
      return 2;
    } 
    byte b = paramArrayOfByte3[paramArrayOfByte1[paramInt1]];
    paramInt1 = paramArrayOfByte3[paramArrayOfByte1[paramInt1 + 1]];
    i = paramArrayOfByte3[paramArrayOfByte1[i]];
    paramInt1 = paramArrayOfByte3[paramArrayOfByte1[j]] << 24 >>> 24 | paramInt1 << 24 >>> 12 | b << 24 >>> 6 | i << 24 >>> 18;
    paramArrayOfByte2[paramInt2] = (byte)(paramInt1 >> 16);
    paramArrayOfByte2[paramInt2 + 1] = (byte)(paramInt1 >> 8);
    paramArrayOfByte2[paramInt2 + 2] = (byte)paramInt1;
    return 3;
  }
  
  public static byte[] decodeWebSafe(String paramString) throws Base64DecoderException {
    byte[] arrayOfByte = paramString.getBytes();
    return decodeWebSafe(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public static byte[] decodeWebSafe(byte[] paramArrayOfByte) throws Base64DecoderException { return decodeWebSafe(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public static byte[] decodeWebSafe(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws Base64DecoderException { return decode(paramArrayOfByte, paramInt1, paramInt2, WEBSAFE_DECODABET); }
  
  public static String encode(byte[] paramArrayOfByte) { return encode(paramArrayOfByte, 0, paramArrayOfByte.length, ALPHABET, true); }
  
  public static String encode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, boolean paramBoolean) {
    paramArrayOfByte1 = encode(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, 2147483647);
    for (paramInt1 = paramArrayOfByte1.length; !paramBoolean && paramInt1 > 0 && paramArrayOfByte1[paramInt1 - 1] == 61; paramInt1--);
    return new String(paramArrayOfByte1, 0, paramInt1);
  }
  
  public static byte[] encode(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3) {
    int i = (paramInt2 + 2) / 3 * 4;
    byte[] arrayOfByte = new byte[i + i / paramInt3];
    int j = 0;
    i = 0;
    byte b = 0;
    while (j < paramInt2 - 2) {
      byte b1 = paramArrayOfByte1[j + paramInt1] << 24 >>> 8 | paramArrayOfByte1[j + 1 + paramInt1] << 24 >>> 16 | paramArrayOfByte1[j + 2 + paramInt1] << 24 >>> 24;
      arrayOfByte[i] = paramArrayOfByte2[b1 >>> 18];
      int k = i + 1;
      arrayOfByte[k] = paramArrayOfByte2[b1 >>> 12 & 0x3F];
      arrayOfByte[i + 2] = paramArrayOfByte2[b1 >>> 6 & 0x3F];
      arrayOfByte[i + 3] = paramArrayOfByte2[b1 & 0x3F];
      b += 4;
      if (b == paramInt3) {
        arrayOfByte[i + 4] = 10;
        b = 0;
        i = k;
      } 
      j += 3;
      i += 4;
    } 
    if (j < paramInt2) {
      encode3to4(paramArrayOfByte1, j + paramInt1, paramInt2 - j, arrayOfByte, i, paramArrayOfByte2);
      if (b + 4 == paramInt3)
        arrayOfByte[i + 4] = 10; 
    } 
    return arrayOfByte;
  }
  
  private static byte[] encode3to4(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, byte[] paramArrayOfByte3) {
    byte b2;
    byte b1;
    byte b = 0;
    if (paramInt2 > 0) {
      b1 = paramArrayOfByte1[paramInt1] << 24 >>> 8;
    } else {
      b1 = 0;
    } 
    if (paramInt2 > 1) {
      b2 = paramArrayOfByte1[paramInt1 + 1] << 24 >>> 16;
    } else {
      b2 = 0;
    } 
    if (paramInt2 > 2)
      b = paramArrayOfByte1[paramInt1 + 2] << 24 >>> 24; 
    paramInt1 = b1 | b2 | b;
    switch (paramInt2) {
      default:
        return paramArrayOfByte2;
      case 3:
        paramArrayOfByte2[paramInt3] = paramArrayOfByte3[paramInt1 >>> 18];
        paramArrayOfByte2[paramInt3 + 1] = paramArrayOfByte3[paramInt1 >>> 12 & 0x3F];
        paramArrayOfByte2[paramInt3 + 2] = paramArrayOfByte3[paramInt1 >>> 6 & 0x3F];
        paramArrayOfByte2[paramInt3 + 3] = paramArrayOfByte3[paramInt1 & 0x3F];
        return paramArrayOfByte2;
      case 2:
        paramArrayOfByte2[paramInt3] = paramArrayOfByte3[paramInt1 >>> 18];
        paramArrayOfByte2[paramInt3 + 1] = paramArrayOfByte3[paramInt1 >>> 12 & 0x3F];
        paramArrayOfByte2[paramInt3 + 2] = paramArrayOfByte3[paramInt1 >>> 6 & 0x3F];
        paramArrayOfByte2[paramInt3 + 3] = 61;
        return paramArrayOfByte2;
      case 1:
        break;
    } 
    paramArrayOfByte2[paramInt3] = paramArrayOfByte3[paramInt1 >>> 18];
    paramArrayOfByte2[paramInt3 + 1] = paramArrayOfByte3[paramInt1 >>> 12 & 0x3F];
    paramArrayOfByte2[paramInt3 + 2] = 61;
    paramArrayOfByte2[paramInt3 + 3] = 61;
    return paramArrayOfByte2;
  }
  
  public static String encodeWebSafe(byte[] paramArrayOfByte, boolean paramBoolean) { return encode(paramArrayOfByte, 0, paramArrayOfByte.length, WEBSAFE_ALPHABET, paramBoolean); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/util/Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */