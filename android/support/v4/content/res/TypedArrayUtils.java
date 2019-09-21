package android.support.v4.content.res;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TypedArrayUtils {
  private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
  
  public static int getAttr(@NonNull Context paramContext, int paramInt1, int paramInt2) {
    TypedValue typedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(paramInt1, typedValue, true);
    return (typedValue.resourceId != 0) ? paramInt1 : paramInt2;
  }
  
  public static boolean getBoolean(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, boolean paramBoolean) { return paramTypedArray.getBoolean(paramInt1, paramTypedArray.getBoolean(paramInt2, paramBoolean)); }
  
  @Nullable
  public static Drawable getDrawable(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2) {
    Drawable drawable2 = paramTypedArray.getDrawable(paramInt1);
    Drawable drawable1 = drawable2;
    if (drawable2 == null)
      drawable1 = paramTypedArray.getDrawable(paramInt2); 
    return drawable1;
  }
  
  public static int getInt(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, int paramInt3) { return paramTypedArray.getInt(paramInt1, paramTypedArray.getInt(paramInt2, paramInt3)); }
  
  public static boolean getNamedBoolean(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt, boolean paramBoolean) { return !hasAttribute(paramXmlPullParser, paramString) ? paramBoolean : paramTypedArray.getBoolean(paramInt, paramBoolean); }
  
  @ColorInt
  public static int getNamedColor(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt1, @ColorInt int paramInt2) { return !hasAttribute(paramXmlPullParser, paramString) ? paramInt2 : paramTypedArray.getColor(paramInt1, paramInt2); }
  
  public static float getNamedFloat(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt, float paramFloat) { return !hasAttribute(paramXmlPullParser, paramString) ? paramFloat : paramTypedArray.getFloat(paramInt, paramFloat); }
  
  public static int getNamedInt(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt1, int paramInt2) { return !hasAttribute(paramXmlPullParser, paramString) ? paramInt2 : paramTypedArray.getInt(paramInt1, paramInt2); }
  
  @AnyRes
  public static int getNamedResourceId(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt1, @AnyRes int paramInt2) { return !hasAttribute(paramXmlPullParser, paramString) ? paramInt2 : paramTypedArray.getResourceId(paramInt1, paramInt2); }
  
  @Nullable
  public static String getNamedString(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, @StyleableRes int paramInt) { return !hasAttribute(paramXmlPullParser, paramString) ? null : paramTypedArray.getString(paramInt); }
  
  @AnyRes
  public static int getResourceId(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, @AnyRes int paramInt3) { return paramTypedArray.getResourceId(paramInt1, paramTypedArray.getResourceId(paramInt2, paramInt3)); }
  
  @Nullable
  public static String getString(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2) {
    String str2 = paramTypedArray.getString(paramInt1);
    String str1 = str2;
    if (str2 == null)
      str1 = paramTypedArray.getString(paramInt2); 
    return str1;
  }
  
  @Nullable
  public static CharSequence getText(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2) {
    CharSequence charSequence2 = paramTypedArray.getText(paramInt1);
    CharSequence charSequence1 = charSequence2;
    if (charSequence2 == null)
      charSequence1 = paramTypedArray.getText(paramInt2); 
    return charSequence1;
  }
  
  @Nullable
  public static CharSequence[] getTextArray(@NonNull TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2) {
    CharSequence[] arrayOfCharSequence2 = paramTypedArray.getTextArray(paramInt1);
    CharSequence[] arrayOfCharSequence1 = arrayOfCharSequence2;
    if (arrayOfCharSequence2 == null)
      arrayOfCharSequence1 = paramTypedArray.getTextArray(paramInt2); 
    return arrayOfCharSequence1;
  }
  
  public static boolean hasAttribute(@NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString) { return (paramXmlPullParser.getAttributeValue("http://schemas.android.com/apk/res/android", paramString) != null); }
  
  @NonNull
  public static TypedArray obtainAttributes(@NonNull Resources paramResources, @Nullable Resources.Theme paramTheme, @NonNull AttributeSet paramAttributeSet, @NonNull int[] paramArrayOfInt) { return (paramTheme == null) ? paramResources.obtainAttributes(paramAttributeSet, paramArrayOfInt) : paramTheme.obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, 0, 0); }
  
  @Nullable
  public static TypedValue peekNamedValue(@NonNull TypedArray paramTypedArray, @NonNull XmlPullParser paramXmlPullParser, @NonNull String paramString, int paramInt) { return !hasAttribute(paramXmlPullParser, paramString) ? null : paramTypedArray.peekValue(paramInt); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/res/TypedArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */