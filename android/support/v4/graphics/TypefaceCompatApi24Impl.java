package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.provider.FontsContractCompat;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@RequiresApi(24)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatApi24Impl extends TypefaceCompatBaseImpl {
  private static final String ADD_FONT_WEIGHT_STYLE_METHOD = "addFontWeightStyle";
  
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  
  private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
  
  private static final String TAG = "TypefaceCompatApi24Impl";
  
  private static final Method sAddFontWeightStyle;
  
  private static final Method sCreateFromFamiliesWithDefault;
  
  private static final Class sFontFamily;
  
  private static final Constructor sFontFamilyCtor;
  
  static  {
    ClassNotFoundException classNotFoundException4;
    ClassNotFoundException classNotFoundException3;
    ClassNotFoundException classNotFoundException2;
    ClassNotFoundException classNotFoundException5 = null;
    try {
      classNotFoundException2 = Class.forName("android.graphics.FontFamily");
      classNotFoundException1 = classNotFoundException2.getConstructor(new Class[0]);
      classNotFoundException4 = classNotFoundException2.getMethod("addFontWeightStyle", new Class[] { ByteBuffer.class, int.class, java.util.List.class, int.class, boolean.class });
      classNotFoundException3 = Typeface.class.getMethod("createFromFamiliesWithDefault", new Class[] { Array.newInstance(classNotFoundException2, 1).getClass() });
    } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException1) {
      Log.e("TypefaceCompatApi24Impl", classNotFoundException1.getClass().getName(), classNotFoundException1);
      classNotFoundException2 = null;
      classNotFoundException1 = classNotFoundException2;
      classNotFoundException4 = classNotFoundException1;
      classNotFoundException3 = classNotFoundException1;
      classNotFoundException1 = classNotFoundException5;
    } 
    sFontFamilyCtor = classNotFoundException1;
    sFontFamily = classNotFoundException2;
    sAddFontWeightStyle = classNotFoundException4;
    sCreateFromFamiliesWithDefault = classNotFoundException3;
  }
  
  private static boolean addFontWeightStyle(Object paramObject, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, boolean paramBoolean) {
    try {
      return ((Boolean)sAddFontWeightStyle.invoke(paramObject, new Object[] { paramByteBuffer, Integer.valueOf(paramInt1), null, Integer.valueOf(paramInt2), Boolean.valueOf(paramBoolean) })).booleanValue();
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException paramObject) {
      throw new RuntimeException(paramObject);
    } 
  }
  
  private static Typeface createFromFamiliesWithDefault(Object paramObject) {
    try {
      Object object = Array.newInstance(sFontFamily, 1);
      Array.set(object, 0, paramObject);
      return (Typeface)sCreateFromFamiliesWithDefault.invoke(null, new Object[] { object });
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException paramObject) {
      throw new RuntimeException(paramObject);
    } 
  }
  
  public static boolean isUsable() {
    if (sAddFontWeightStyle == null)
      Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation."); 
    return (sAddFontWeightStyle != null);
  }
  
  private static Object newFamily() {
    try {
      return sFontFamilyCtor.newInstance(new Object[0]);
    } catch (IllegalAccessException|InstantiationException|java.lang.reflect.InvocationTargetException illegalAccessException) {
      throw new RuntimeException(illegalAccessException);
    } 
  }
  
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt) {
    Object object = newFamily();
    FontResourcesParserCompat.FontFileResourceEntry[] arrayOfFontFileResourceEntry = paramFontFamilyFilesResourceEntry.getEntries();
    int i = arrayOfFontFileResourceEntry.length;
    for (paramInt = 0; paramInt < i; paramInt++) {
      FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry = arrayOfFontFileResourceEntry[paramInt];
      ByteBuffer byteBuffer = TypefaceCompatUtil.copyToDirectBuffer(paramContext, paramResources, fontFileResourceEntry.getResourceId());
      if (byteBuffer == null)
        return null; 
      if (!addFontWeightStyle(object, byteBuffer, 0, fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic()))
        return null; 
    } 
    return createFromFamiliesWithDefault(object);
  }
  
  public Typeface createFromFontInfo(Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt) {
    Object object = newFamily();
    SimpleArrayMap simpleArrayMap = new SimpleArrayMap();
    int i = paramArrayOfFontInfo.length;
    byte b;
    for (b = 0; b < i; b++) {
      FontsContractCompat.FontInfo fontInfo = paramArrayOfFontInfo[b];
      Uri uri = fontInfo.getUri();
      ByteBuffer byteBuffer2 = (ByteBuffer)simpleArrayMap.get(uri);
      ByteBuffer byteBuffer1 = byteBuffer2;
      if (byteBuffer2 == null) {
        byteBuffer1 = TypefaceCompatUtil.mmap(paramContext, paramCancellationSignal, uri);
        simpleArrayMap.put(uri, byteBuffer1);
      } 
      if (!addFontWeightStyle(object, byteBuffer1, fontInfo.getTtcIndex(), fontInfo.getWeight(), fontInfo.isItalic()))
        return null; 
    } 
    return Typeface.create(createFromFamiliesWithDefault(object), paramInt);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/TypefaceCompatApi24Impl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */