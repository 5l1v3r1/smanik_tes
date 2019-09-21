package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.provider.FontsContractCompat;
import android.support.v4.util.LruCache;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TypefaceCompat {
  private static final String TAG = "TypefaceCompat";
  
  private static final LruCache<String, Typeface> sTypefaceCache;
  
  private static final TypefaceCompatImpl sTypefaceCompatImpl;
  
  static  {
    if (Build.VERSION.SDK_INT >= 26) {
      sTypefaceCompatImpl = new TypefaceCompatApi26Impl();
    } else if (Build.VERSION.SDK_INT >= 24 && TypefaceCompatApi24Impl.isUsable()) {
      sTypefaceCompatImpl = new TypefaceCompatApi24Impl();
    } else if (Build.VERSION.SDK_INT >= 21) {
      sTypefaceCompatImpl = new TypefaceCompatApi21Impl();
    } else {
      sTypefaceCompatImpl = new TypefaceCompatBaseImpl();
    } 
    sTypefaceCache = new LruCache(16);
  }
  
  @Nullable
  public static Typeface createFromFontInfo(@NonNull Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt) { return sTypefaceCompatImpl.createFromFontInfo(paramContext, paramCancellationSignal, paramArrayOfFontInfo, paramInt); }
  
  @Nullable
  public static Typeface createFromResourcesFamilyXml(@NonNull Context paramContext, @NonNull FontResourcesParserCompat.FamilyResourceEntry paramFamilyResourceEntry, @NonNull Resources paramResources, int paramInt1, int paramInt2, @Nullable ResourcesCompat.FontCallback paramFontCallback, @Nullable Handler paramHandler, boolean paramBoolean) {
    FontResourcesParserCompat.ProviderResourceEntry providerResourceEntry;
    Typeface typeface;
    if (paramFamilyResourceEntry instanceof FontResourcesParserCompat.ProviderResourceEntry) {
      byte b;
      providerResourceEntry = (FontResourcesParserCompat.ProviderResourceEntry)paramFamilyResourceEntry;
      boolean bool = false;
      if (paramBoolean ? (providerResourceEntry.getFetchStrategy() == 0) : (paramFontCallback == null))
        bool = true; 
      if (paramBoolean) {
        b = providerResourceEntry.getTimeout();
      } else {
        b = -1;
      } 
      typeface = FontsContractCompat.getFontSync(paramContext, providerResourceEntry.getRequest(), paramFontCallback, paramHandler, bool, b, paramInt2);
    } else {
      Typeface typeface1 = sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(typeface, (FontResourcesParserCompat.FontFamilyFilesResourceEntry)providerResourceEntry, paramResources, paramInt2);
      typeface = typeface1;
      if (paramFontCallback != null)
        if (typeface1 != null) {
          paramFontCallback.callbackSuccessAsync(typeface1, paramHandler);
          typeface = typeface1;
        } else {
          paramFontCallback.callbackFailAsync(-3, paramHandler);
          typeface = typeface1;
        }  
    } 
    if (typeface != null)
      sTypefaceCache.put(createResourceUid(paramResources, paramInt1, paramInt2), typeface); 
    return typeface;
  }
  
  @Nullable
  public static Typeface createFromResourcesFontFile(@NonNull Context paramContext, @NonNull Resources paramResources, int paramInt1, String paramString, int paramInt2) {
    Typeface typeface = sTypefaceCompatImpl.createFromResourcesFontFile(paramContext, paramResources, paramInt1, paramString, paramInt2);
    if (typeface != null) {
      String str = createResourceUid(paramResources, paramInt1, paramInt2);
      sTypefaceCache.put(str, typeface);
    } 
    return typeface;
  }
  
  private static String createResourceUid(Resources paramResources, int paramInt1, int paramInt2) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramResources.getResourcePackageName(paramInt1));
    stringBuilder.append("-");
    stringBuilder.append(paramInt1);
    stringBuilder.append("-");
    stringBuilder.append(paramInt2);
    return stringBuilder.toString();
  }
  
  @Nullable
  public static Typeface findFromCache(@NonNull Resources paramResources, int paramInt1, int paramInt2) { return (Typeface)sTypefaceCache.get(createResourceUid(paramResources, paramInt1, paramInt2)); }
  
  static interface TypefaceCompatImpl {
    Typeface createFromFontFamilyFilesResourceEntry(Context param1Context, FontResourcesParserCompat.FontFamilyFilesResourceEntry param1FontFamilyFilesResourceEntry, Resources param1Resources, int param1Int);
    
    Typeface createFromFontInfo(Context param1Context, @Nullable CancellationSignal param1CancellationSignal, @NonNull FontsContractCompat.FontInfo[] param1ArrayOfFontInfo, int param1Int);
    
    Typeface createFromResourcesFontFile(Context param1Context, Resources param1Resources, int param1Int1, String param1String, int param1Int2);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/TypefaceCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */