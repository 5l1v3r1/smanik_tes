package com.google.android.vending.licensing;

import com.google.android.vending.licensing.util.Base64;
import com.google.android.vending.licensing.util.Base64DecoderException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESObfuscator implements Obfuscator {
  private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
  
  private static final byte[] IV = { 
      16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 
      0, -29, 70, 65, -12, 74 };
  
  private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
  
  private static final String UTF8 = "UTF-8";
  
  private static final String header = "com.android.vending.licensing.AESObfuscator-1|";
  
  private Cipher mDecryptor;
  
  private Cipher mEncryptor;
  
  public AESObfuscator(byte[] paramArrayOfByte, String paramString1, String paramString2) {
    try {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWITHSHAAND256BITAES-CBC-BC");
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString1);
      stringBuilder.append(paramString2);
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyFactory.generateSecret(new PBEKeySpec(stringBuilder.toString().toCharArray(), paramArrayOfByte, 1024, 256)).getEncoded(), "AES");
      this.mEncryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
      this.mEncryptor.init(1, secretKeySpec, new IvParameterSpec(IV));
      this.mDecryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
      this.mDecryptor.init(2, secretKeySpec, new IvParameterSpec(IV));
      return;
    } catch (GeneralSecurityException paramArrayOfByte) {
      throw new RuntimeException("Invalid environment", paramArrayOfByte);
    } 
  }
  
  public String obfuscate(String paramString1, String paramString2) {
    if (paramString1 == null)
      return null; 
    try {
      Cipher cipher = this.mEncryptor;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("com.android.vending.licensing.AESObfuscator-1|");
      stringBuilder.append(paramString2);
      stringBuilder.append(paramString1);
      return Base64.encode(cipher.doFinal(stringBuilder.toString().getBytes("UTF-8")));
    } catch (UnsupportedEncodingException paramString1) {
      throw new RuntimeException("Invalid environment", paramString1);
    } catch (GeneralSecurityException paramString1) {
      throw new RuntimeException("Invalid environment", paramString1);
    } 
  }
  
  public String unobfuscate(String paramString1, String paramString2) {
    if (paramString1 == null)
      return null; 
    try {
      StringBuilder stringBuilder1;
      String str = new String(this.mDecryptor.doFinal(Base64.decode(paramString1)), "UTF-8");
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append("com.android.vending.licensing.AESObfuscator-1|");
      stringBuilder2.append(paramString2);
      if (str.indexOf(stringBuilder2.toString()) != 0) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Header not found (invalid data or key):");
        stringBuilder1.append(paramString1);
        throw new ValidationException(stringBuilder1.toString());
      } 
      return str.substring("com.android.vending.licensing.AESObfuscator-1|".length() + stringBuilder1.length(), str.length());
    } catch (Base64DecoderException paramString2) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString2.getMessage());
      stringBuilder.append(":");
      stringBuilder.append(paramString1);
      throw new ValidationException(stringBuilder.toString());
    } catch (IllegalBlockSizeException paramString2) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString2.getMessage());
      stringBuilder.append(":");
      stringBuilder.append(paramString1);
      throw new ValidationException(stringBuilder.toString());
    } catch (BadPaddingException paramString2) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString2.getMessage());
      stringBuilder.append(":");
      stringBuilder.append(paramString1);
      throw new ValidationException(stringBuilder.toString());
    } catch (UnsupportedEncodingException paramString1) {
      throw new RuntimeException("Invalid environment", paramString1);
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/AESObfuscator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */