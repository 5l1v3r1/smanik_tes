package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.LruCache;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class AppCompatDrawableManager {
  private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY;
  
  private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED;
  
  private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL;
  
  private static final ColorFilterLruCache COLOR_FILTER_CACHE;
  
  private static final boolean DEBUG = false;
  
  private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
  
  private static AppCompatDrawableManager INSTANCE;
  
  private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
  
  private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
  
  private static final String TAG = "AppCompatDrawableManag";
  
  private static final int[] TINT_CHECKABLE_BUTTON_LIST;
  
  private static final int[] TINT_COLOR_CONTROL_NORMAL;
  
  private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
  
  private ArrayMap<String, InflateDelegate> mDelegates;
  
  private final Object mDrawableCacheLock = new Object();
  
  private final WeakHashMap<Context, LongSparseArray<WeakReference<Drawable.ConstantState>>> mDrawableCaches = new WeakHashMap(0);
  
  private boolean mHasCheckedVectorDrawableSetup;
  
  private SparseArrayCompat<String> mKnownDrawableIdTags;
  
  private WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists;
  
  private TypedValue mTypedValue;
  
  static  {
    COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    COLORFILTER_TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha };
    TINT_COLOR_CONTROL_NORMAL = new int[] { R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_seekbar_tick_mark_material, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha };
    COLORFILTER_COLOR_CONTROL_ACTIVATED = new int[] { R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material, R.drawable.abc_text_select_handle_left_mtrl_dark, R.drawable.abc_text_select_handle_middle_mtrl_dark, R.drawable.abc_text_select_handle_right_mtrl_dark, R.drawable.abc_text_select_handle_left_mtrl_light, R.drawable.abc_text_select_handle_middle_mtrl_light, R.drawable.abc_text_select_handle_right_mtrl_light };
    COLORFILTER_COLOR_BACKGROUND_MULTIPLY = new int[] { R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult };
    TINT_COLOR_CONTROL_STATE_LIST = new int[] { R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material };
    TINT_CHECKABLE_BUTTON_LIST = new int[] { R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material };
  }
  
  private void addDelegate(@NonNull String paramString, @NonNull InflateDelegate paramInflateDelegate) {
    if (this.mDelegates == null)
      this.mDelegates = new ArrayMap(); 
    this.mDelegates.put(paramString, paramInflateDelegate);
  }
  
  private boolean addDrawableToCache(@NonNull Context paramContext, long paramLong, @NonNull Drawable paramDrawable) {
    Drawable.ConstantState constantState = paramDrawable.getConstantState();
    if (constantState != null)
      synchronized (this.mDrawableCacheLock) {
        LongSparseArray longSparseArray2 = (LongSparseArray)this.mDrawableCaches.get(paramContext);
        LongSparseArray longSparseArray1 = longSparseArray2;
        if (longSparseArray2 == null) {
          longSparseArray1 = new LongSparseArray();
          this.mDrawableCaches.put(paramContext, longSparseArray1);
        } 
        longSparseArray1.put(paramLong, new WeakReference(constantState));
        return true;
      }  
    return false;
  }
  
  private void addTintListToCache(@NonNull Context paramContext, @DrawableRes int paramInt, @NonNull ColorStateList paramColorStateList) {
    if (this.mTintLists == null)
      this.mTintLists = new WeakHashMap(); 
    SparseArrayCompat sparseArrayCompat2 = (SparseArrayCompat)this.mTintLists.get(paramContext);
    SparseArrayCompat sparseArrayCompat1 = sparseArrayCompat2;
    if (sparseArrayCompat2 == null) {
      sparseArrayCompat1 = new SparseArrayCompat();
      this.mTintLists.put(paramContext, sparseArrayCompat1);
    } 
    sparseArrayCompat1.append(paramInt, paramColorStateList);
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt) {
    int i = paramArrayOfInt.length;
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfInt[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  private void checkVectorDrawableSetup(@NonNull Context paramContext) {
    if (this.mHasCheckedVectorDrawableSetup)
      return; 
    this.mHasCheckedVectorDrawableSetup = true;
    Drawable drawable = getDrawable(paramContext, R.drawable.abc_vector_test);
    if (drawable == null || !isVectorDrawable(drawable)) {
      this.mHasCheckedVectorDrawableSetup = false;
      throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
    } 
  }
  
  private ColorStateList createBorderlessButtonColorStateList(@NonNull Context paramContext) { return createButtonColorStateList(paramContext, 0); }
  
  private ColorStateList createButtonColorStateList(@NonNull Context paramContext, @ColorInt int paramInt) {
    int k = ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlHighlight);
    int i = ThemeUtils.getDisabledThemeAttrColor(paramContext, R.attr.colorButtonNormal);
    int[] arrayOfInt1 = ThemeUtils.DISABLED_STATE_SET;
    int[] arrayOfInt2 = ThemeUtils.PRESSED_STATE_SET;
    int j = ColorUtils.compositeColors(k, paramInt);
    int[] arrayOfInt3 = ThemeUtils.FOCUSED_STATE_SET;
    k = ColorUtils.compositeColors(k, paramInt);
    return new ColorStateList(new int[][] { arrayOfInt1, arrayOfInt2, arrayOfInt3, ThemeUtils.EMPTY_STATE_SET }, new int[] { i, j, k, paramInt });
  }
  
  private static long createCacheKey(TypedValue paramTypedValue) { return paramTypedValue.assetCookie << 32 | paramTypedValue.data; }
  
  private ColorStateList createColoredButtonColorStateList(@NonNull Context paramContext) { return createButtonColorStateList(paramContext, ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorAccent)); }
  
  private ColorStateList createDefaultButtonColorStateList(@NonNull Context paramContext) { return createButtonColorStateList(paramContext, ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorButtonNormal)); }
  
  private Drawable createDrawableIfNeeded(@NonNull Context paramContext, @DrawableRes int paramInt) {
    if (this.mTypedValue == null)
      this.mTypedValue = new TypedValue(); 
    TypedValue typedValue = this.mTypedValue;
    paramContext.getResources().getValue(paramInt, typedValue, true);
    long l = createCacheKey(typedValue);
    LayerDrawable layerDrawable = getCachedDrawable(paramContext, l);
    if (layerDrawable != null)
      return layerDrawable; 
    if (paramInt == R.drawable.abc_cab_background_top_material)
      layerDrawable = new LayerDrawable(new Drawable[] { getDrawable(paramContext, R.drawable.abc_cab_background_internal_bg), getDrawable(paramContext, R.drawable.abc_cab_background_top_mtrl_alpha) }); 
    if (layerDrawable != null) {
      layerDrawable.setChangingConfigurations(typedValue.changingConfigurations);
      addDrawableToCache(paramContext, l, layerDrawable);
    } 
    return layerDrawable;
  }
  
  private ColorStateList createSwitchThumbColorStateList(Context paramContext) {
    int[][] arrayOfInt = new int[3][];
    int[] arrayOfInt1 = new int[3];
    ColorStateList colorStateList = ThemeUtils.getThemeAttrColorStateList(paramContext, R.attr.colorSwitchThumbNormal);
    if (colorStateList != null && colorStateList.isStateful()) {
      arrayOfInt[0] = ThemeUtils.DISABLED_STATE_SET;
      arrayOfInt1[0] = colorStateList.getColorForState(arrayOfInt[0], 0);
      arrayOfInt[1] = ThemeUtils.CHECKED_STATE_SET;
      arrayOfInt1[1] = ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlActivated);
      arrayOfInt[2] = ThemeUtils.EMPTY_STATE_SET;
      arrayOfInt1[2] = colorStateList.getDefaultColor();
    } else {
      arrayOfInt[0] = ThemeUtils.DISABLED_STATE_SET;
      arrayOfInt1[0] = ThemeUtils.getDisabledThemeAttrColor(paramContext, R.attr.colorSwitchThumbNormal);
      arrayOfInt[1] = ThemeUtils.CHECKED_STATE_SET;
      arrayOfInt1[1] = ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorControlActivated);
      arrayOfInt[2] = ThemeUtils.EMPTY_STATE_SET;
      arrayOfInt1[2] = ThemeUtils.getThemeAttrColor(paramContext, R.attr.colorSwitchThumbNormal);
    } 
    return new ColorStateList(arrayOfInt, arrayOfInt1);
  }
  
  private static PorterDuffColorFilter createTintFilter(ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int[] paramArrayOfInt) { return (paramColorStateList == null || paramMode == null) ? null : getPorterDuffColorFilter(paramColorStateList.getColorForState(paramArrayOfInt, 0), paramMode); }
  
  public static AppCompatDrawableManager get() {
    if (INSTANCE == null)
      (INSTANCE = new AppCompatDrawableManager()).installDefaultInflateDelegates(INSTANCE); 
    return INSTANCE;
  }
  
  private Drawable getCachedDrawable(@NonNull Context paramContext, long paramLong) {
    synchronized (this.mDrawableCacheLock) {
      LongSparseArray longSparseArray = (LongSparseArray)this.mDrawableCaches.get(paramContext);
      if (longSparseArray == null)
        return null; 
      WeakReference weakReference = (WeakReference)longSparseArray.get(paramLong);
      if (weakReference != null) {
        Drawable.ConstantState constantState = (Drawable.ConstantState)weakReference.get();
        if (constantState != null)
          return constantState.newDrawable(paramContext.getResources()); 
        longSparseArray.delete(paramLong);
      } 
      return null;
    } 
  }
  
  public static PorterDuffColorFilter getPorterDuffColorFilter(int paramInt, PorterDuff.Mode paramMode) {
    PorterDuffColorFilter porterDuffColorFilter2 = COLOR_FILTER_CACHE.get(paramInt, paramMode);
    PorterDuffColorFilter porterDuffColorFilter1 = porterDuffColorFilter2;
    if (porterDuffColorFilter2 == null) {
      porterDuffColorFilter1 = new PorterDuffColorFilter(paramInt, paramMode);
      COLOR_FILTER_CACHE.put(paramInt, paramMode, porterDuffColorFilter1);
    } 
    return porterDuffColorFilter1;
  }
  
  private ColorStateList getTintListFromCache(@NonNull Context paramContext, @DrawableRes int paramInt) {
    WeakHashMap weakHashMap = this.mTintLists;
    Context context = null;
    if (weakHashMap != null) {
      ColorStateList colorStateList;
      SparseArrayCompat sparseArrayCompat = (SparseArrayCompat)this.mTintLists.get(paramContext);
      paramContext = context;
      if (sparseArrayCompat != null)
        colorStateList = (ColorStateList)sparseArrayCompat.get(paramInt); 
      return colorStateList;
    } 
    return null;
  }
  
  static PorterDuff.Mode getTintMode(int paramInt) { return (paramInt == R.drawable.abc_switch_thumb_material) ? PorterDuff.Mode.MULTIPLY : null; }
  
  private static void installDefaultInflateDelegates(@NonNull AppCompatDrawableManager paramAppCompatDrawableManager) {
    if (Build.VERSION.SDK_INT < 24) {
      paramAppCompatDrawableManager.addDelegate("vector", new VdcInflateDelegate());
      paramAppCompatDrawableManager.addDelegate("animated-vector", new AvdcInflateDelegate());
    } 
  }
  
  private static boolean isVectorDrawable(@NonNull Drawable paramDrawable) { return (paramDrawable instanceof VectorDrawableCompat || "android.graphics.drawable.VectorDrawable".equals(paramDrawable.getClass().getName())); }
  
  private Drawable loadDrawableFromDelegates(@NonNull Context paramContext, @DrawableRes int paramInt) {
    if (this.mDelegates != null && !this.mDelegates.isEmpty()) {
      if (this.mKnownDrawableIdTags != null) {
        String str = (String)this.mKnownDrawableIdTags.get(paramInt);
        if ("appcompat_skip_skip".equals(str) || (str != null && this.mDelegates.get(str) == null))
          return null; 
      } else {
        this.mKnownDrawableIdTags = new SparseArrayCompat();
      } 
      if (this.mTypedValue == null)
        this.mTypedValue = new TypedValue(); 
      TypedValue typedValue = this.mTypedValue;
      Resources resources = paramContext.getResources();
      resources.getValue(paramInt, typedValue, true);
      long l = createCacheKey(typedValue);
      Drawable drawable2 = getCachedDrawable(paramContext, l);
      if (drawable2 != null)
        return drawable2; 
      Drawable drawable1 = drawable2;
      if (typedValue.string != null) {
        drawable1 = drawable2;
        if (typedValue.string.toString().endsWith(".xml")) {
          drawable1 = drawable2;
          try {
            int i;
            XmlResourceParser xmlResourceParser = resources.getXml(paramInt);
            drawable1 = drawable2;
            AttributeSet attributeSet = Xml.asAttributeSet(xmlResourceParser);
            while (true) {
              drawable1 = drawable2;
              i = xmlResourceParser.next();
              if (i != 2 && i != 1)
                continue; 
              break;
            } 
            if (i != 2) {
              drawable1 = drawable2;
              throw new XmlPullParserException("No start tag found");
            } 
            drawable1 = drawable2;
            String str = xmlResourceParser.getName();
            drawable1 = drawable2;
            this.mKnownDrawableIdTags.append(paramInt, str);
            drawable1 = drawable2;
            InflateDelegate inflateDelegate = (InflateDelegate)this.mDelegates.get(str);
            Drawable drawable = drawable2;
            if (inflateDelegate != null) {
              drawable1 = drawable2;
              drawable = inflateDelegate.createFromXmlInner(paramContext, xmlResourceParser, attributeSet, paramContext.getTheme());
            } 
            drawable1 = drawable;
            if (drawable != null) {
              drawable1 = drawable;
              drawable.setChangingConfigurations(typedValue.changingConfigurations);
              drawable1 = drawable;
              addDrawableToCache(paramContext, l, drawable);
              drawable1 = drawable;
            } 
          } catch (Exception paramContext) {
            Log.e("AppCompatDrawableManag", "Exception while inflating drawable", paramContext);
          } 
        } 
      } 
      if (drawable1 == null)
        this.mKnownDrawableIdTags.append(paramInt, "appcompat_skip_skip"); 
      return drawable1;
    } 
    return null;
  }
  
  private void removeDelegate(@NonNull String paramString, @NonNull InflateDelegate paramInflateDelegate) {
    if (this.mDelegates != null && this.mDelegates.get(paramString) == paramInflateDelegate)
      this.mDelegates.remove(paramString); 
  }
  
  private static void setPorterDuffColorFilter(Drawable paramDrawable, int paramInt, PorterDuff.Mode paramMode) {
    Drawable drawable = paramDrawable;
    if (DrawableUtils.canSafelyMutateDrawable(paramDrawable))
      drawable = paramDrawable.mutate(); 
    PorterDuff.Mode mode = paramMode;
    if (paramMode == null)
      mode = DEFAULT_MODE; 
    drawable.setColorFilter(getPorterDuffColorFilter(paramInt, mode));
  }
  
  private Drawable tintDrawable(@NonNull Context paramContext, @DrawableRes int paramInt, boolean paramBoolean, @NonNull Drawable paramDrawable) {
    PorterDuff.Mode mode1;
    Drawable drawable;
    PorterDuff.Mode mode2 = getTintList(paramContext, paramInt);
    if (mode2 != null) {
      drawable = paramDrawable;
      if (DrawableUtils.canSafelyMutateDrawable(paramDrawable))
        drawable = paramDrawable.mutate(); 
      drawable = DrawableCompat.wrap(drawable);
      DrawableCompat.setTintList(drawable, mode2);
      mode1 = getTintMode(paramInt);
      Drawable drawable1 = drawable;
      if (mode1 != null) {
        DrawableCompat.setTintMode(drawable, mode1);
        return drawable;
      } 
    } else {
      if (paramInt == R.drawable.abc_seekbar_track_material) {
        LayerDrawable layerDrawable = (LayerDrawable)mode1;
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908288), ThemeUtils.getThemeAttrColor(drawable, R.attr.colorControlNormal), DEFAULT_MODE);
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(drawable, R.attr.colorControlNormal), DEFAULT_MODE);
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(drawable, R.attr.colorControlActivated), DEFAULT_MODE);
        return mode1;
      } 
      if (paramInt == R.drawable.abc_ratingbar_material || paramInt == R.drawable.abc_ratingbar_indicator_material || paramInt == R.drawable.abc_ratingbar_small_material) {
        LayerDrawable layerDrawable = (LayerDrawable)mode1;
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908288), ThemeUtils.getDisabledThemeAttrColor(drawable, R.attr.colorControlNormal), DEFAULT_MODE);
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(drawable, R.attr.colorControlActivated), DEFAULT_MODE);
        setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(drawable, R.attr.colorControlActivated), DEFAULT_MODE);
        return mode1;
      } 
      mode2 = mode1;
      if (!tintDrawableUsingColorFilter(drawable, paramInt, mode1)) {
        mode2 = mode1;
        if (paramBoolean)
          return null; 
      } 
    } 
    return mode2;
  }
  
  static void tintDrawable(Drawable paramDrawable, TintInfo paramTintInfo, int[] paramArrayOfInt) {
    if (DrawableUtils.canSafelyMutateDrawable(paramDrawable) && paramDrawable.mutate() != paramDrawable) {
      Log.d("AppCompatDrawableManag", "Mutated drawable is not the same instance as the input.");
      return;
    } 
    if (paramTintInfo.mHasTintList || paramTintInfo.mHasTintMode) {
      ColorStateList colorStateList;
      PorterDuff.Mode mode;
      if (paramTintInfo.mHasTintList) {
        colorStateList = paramTintInfo.mTintList;
      } else {
        colorStateList = null;
      } 
      if (paramTintInfo.mHasTintMode) {
        mode = paramTintInfo.mTintMode;
      } else {
        mode = DEFAULT_MODE;
      } 
      paramDrawable.setColorFilter(createTintFilter(colorStateList, mode, paramArrayOfInt));
    } else {
      paramDrawable.clearColorFilter();
    } 
    if (Build.VERSION.SDK_INT <= 23)
      paramDrawable.invalidateSelf(); 
  }
  
  static boolean tintDrawableUsingColorFilter(@NonNull Context paramContext, @DrawableRes int paramInt, @NonNull Drawable paramDrawable) { // Byte code:
    //   0: getstatic android/support/v7/widget/AppCompatDrawableManager.DEFAULT_MODE : Landroid/graphics/PorterDuff$Mode;
    //   3: astore #6
    //   5: getstatic android/support/v7/widget/AppCompatDrawableManager.COLORFILTER_TINT_COLOR_CONTROL_NORMAL : [I
    //   8: iload_1
    //   9: invokestatic arrayContains : ([II)Z
    //   12: istore #5
    //   14: ldc_w 16842801
    //   17: istore_3
    //   18: iload #5
    //   20: ifeq -> 35
    //   23: getstatic android/support/v7/appcompat/R$attr.colorControlNormal : I
    //   26: istore_1
    //   27: iconst_1
    //   28: istore #4
    //   30: iconst_m1
    //   31: istore_3
    //   32: goto -> 115
    //   35: getstatic android/support/v7/widget/AppCompatDrawableManager.COLORFILTER_COLOR_CONTROL_ACTIVATED : [I
    //   38: iload_1
    //   39: invokestatic arrayContains : ([II)Z
    //   42: ifeq -> 52
    //   45: getstatic android/support/v7/appcompat/R$attr.colorControlActivated : I
    //   48: istore_1
    //   49: goto -> 27
    //   52: getstatic android/support/v7/widget/AppCompatDrawableManager.COLORFILTER_COLOR_BACKGROUND_MULTIPLY : [I
    //   55: iload_1
    //   56: invokestatic arrayContains : ([II)Z
    //   59: ifeq -> 72
    //   62: getstatic android/graphics/PorterDuff$Mode.MULTIPLY : Landroid/graphics/PorterDuff$Mode;
    //   65: astore #6
    //   67: iload_3
    //   68: istore_1
    //   69: goto -> 27
    //   72: iload_1
    //   73: getstatic android/support/v7/appcompat/R$drawable.abc_list_divider_mtrl_alpha : I
    //   76: if_icmpne -> 96
    //   79: ldc_w 16842800
    //   82: istore_1
    //   83: ldc_w 40.8
    //   86: invokestatic round : (F)I
    //   89: istore_3
    //   90: iconst_1
    //   91: istore #4
    //   93: goto -> 115
    //   96: iload_1
    //   97: getstatic android/support/v7/appcompat/R$drawable.abc_dialog_material_background : I
    //   100: if_icmpne -> 108
    //   103: iload_3
    //   104: istore_1
    //   105: goto -> 27
    //   108: iconst_0
    //   109: istore #4
    //   111: iconst_m1
    //   112: istore_3
    //   113: iconst_0
    //   114: istore_1
    //   115: iload #4
    //   117: ifeq -> 164
    //   120: aload_2
    //   121: astore #7
    //   123: aload_2
    //   124: invokestatic canSafelyMutateDrawable : (Landroid/graphics/drawable/Drawable;)Z
    //   127: ifeq -> 136
    //   130: aload_2
    //   131: invokevirtual mutate : ()Landroid/graphics/drawable/Drawable;
    //   134: astore #7
    //   136: aload #7
    //   138: aload_0
    //   139: iload_1
    //   140: invokestatic getThemeAttrColor : (Landroid/content/Context;I)I
    //   143: aload #6
    //   145: invokestatic getPorterDuffColorFilter : (ILandroid/graphics/PorterDuff$Mode;)Landroid/graphics/PorterDuffColorFilter;
    //   148: invokevirtual setColorFilter : (Landroid/graphics/ColorFilter;)V
    //   151: iload_3
    //   152: iconst_m1
    //   153: if_icmpeq -> 162
    //   156: aload #7
    //   158: iload_3
    //   159: invokevirtual setAlpha : (I)V
    //   162: iconst_1
    //   163: ireturn
    //   164: iconst_0
    //   165: ireturn }
  
  public Drawable getDrawable(@NonNull Context paramContext, @DrawableRes int paramInt) { return getDrawable(paramContext, paramInt, false); }
  
  Drawable getDrawable(@NonNull Context paramContext, @DrawableRes int paramInt, boolean paramBoolean) {
    checkVectorDrawableSetup(paramContext);
    Drawable drawable2 = loadDrawableFromDelegates(paramContext, paramInt);
    Drawable drawable1 = drawable2;
    if (drawable2 == null)
      drawable1 = createDrawableIfNeeded(paramContext, paramInt); 
    drawable2 = drawable1;
    if (drawable1 == null)
      drawable2 = ContextCompat.getDrawable(paramContext, paramInt); 
    drawable1 = drawable2;
    if (drawable2 != null)
      drawable1 = tintDrawable(paramContext, paramInt, paramBoolean, drawable2); 
    if (drawable1 != null)
      DrawableUtils.fixDrawable(drawable1); 
    return drawable1;
  }
  
  ColorStateList getTintList(@NonNull Context paramContext, @DrawableRes int paramInt) {
    ColorStateList colorStateList1 = getTintListFromCache(paramContext, paramInt);
    ColorStateList colorStateList2 = colorStateList1;
    if (colorStateList1 == null) {
      if (paramInt == R.drawable.abc_edit_text_material) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_edittext);
      } else if (paramInt == R.drawable.abc_switch_track_mtrl_alpha) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_switch_track);
      } else if (paramInt == R.drawable.abc_switch_thumb_material) {
        colorStateList1 = createSwitchThumbColorStateList(paramContext);
      } else if (paramInt == R.drawable.abc_btn_default_mtrl_shape) {
        colorStateList1 = createDefaultButtonColorStateList(paramContext);
      } else if (paramInt == R.drawable.abc_btn_borderless_material) {
        colorStateList1 = createBorderlessButtonColorStateList(paramContext);
      } else if (paramInt == R.drawable.abc_btn_colored_material) {
        colorStateList1 = createColoredButtonColorStateList(paramContext);
      } else if (paramInt == R.drawable.abc_spinner_mtrl_am_alpha || paramInt == R.drawable.abc_spinner_textfield_background_material) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_spinner);
      } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, paramInt)) {
        colorStateList1 = ThemeUtils.getThemeAttrColorStateList(paramContext, R.attr.colorControlNormal);
      } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, paramInt)) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_default);
      } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, paramInt)) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_btn_checkable);
      } else if (paramInt == R.drawable.abc_seekbar_thumb_material) {
        colorStateList1 = AppCompatResources.getColorStateList(paramContext, R.color.abc_tint_seek_thumb);
      } 
      colorStateList2 = colorStateList1;
      if (colorStateList1 != null) {
        addTintListToCache(paramContext, paramInt, colorStateList1);
        colorStateList2 = colorStateList1;
      } 
    } 
    return colorStateList2;
  }
  
  public void onConfigurationChanged(@NonNull Context paramContext) {
    synchronized (this.mDrawableCacheLock) {
      LongSparseArray longSparseArray = (LongSparseArray)this.mDrawableCaches.get(paramContext);
      if (longSparseArray != null)
        longSparseArray.clear(); 
      return;
    } 
  }
  
  Drawable onDrawableLoadedFromResources(@NonNull Context paramContext, @NonNull VectorEnabledTintResources paramVectorEnabledTintResources, @DrawableRes int paramInt) {
    Drawable drawable2 = loadDrawableFromDelegates(paramContext, paramInt);
    Drawable drawable1 = drawable2;
    if (drawable2 == null)
      drawable1 = paramVectorEnabledTintResources.superGetDrawable(paramInt); 
    return (drawable1 != null) ? tintDrawable(paramContext, paramInt, false, drawable1) : null;
  }
  
  @RequiresApi(11)
  private static class AvdcInflateDelegate implements InflateDelegate {
    public Drawable createFromXmlInner(@NonNull Context param1Context, @NonNull XmlPullParser param1XmlPullParser, @NonNull AttributeSet param1AttributeSet, @Nullable Resources.Theme param1Theme) {
      try {
        return AnimatedVectorDrawableCompat.createFromXmlInner(param1Context, param1Context.getResources(), param1XmlPullParser, param1AttributeSet, param1Theme);
      } catch (Exception param1Context) {
        Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", param1Context);
        return null;
      } 
    }
  }
  
  private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
    public ColorFilterLruCache(int param1Int) { super(param1Int); }
    
    private static int generateCacheKey(int param1Int, PorterDuff.Mode param1Mode) { return (param1Int + 31) * 31 + param1Mode.hashCode(); }
    
    PorterDuffColorFilter get(int param1Int, PorterDuff.Mode param1Mode) { return (PorterDuffColorFilter)get(Integer.valueOf(generateCacheKey(param1Int, param1Mode))); }
    
    PorterDuffColorFilter put(int param1Int, PorterDuff.Mode param1Mode, PorterDuffColorFilter param1PorterDuffColorFilter) { return (PorterDuffColorFilter)put(Integer.valueOf(generateCacheKey(param1Int, param1Mode)), param1PorterDuffColorFilter); }
  }
  
  private static interface InflateDelegate {
    Drawable createFromXmlInner(@NonNull Context param1Context, @NonNull XmlPullParser param1XmlPullParser, @NonNull AttributeSet param1AttributeSet, @Nullable Resources.Theme param1Theme);
  }
  
  private static class VdcInflateDelegate implements InflateDelegate {
    public Drawable createFromXmlInner(@NonNull Context param1Context, @NonNull XmlPullParser param1XmlPullParser, @NonNull AttributeSet param1AttributeSet, @Nullable Resources.Theme param1Theme) {
      try {
        return VectorDrawableCompat.createFromXmlInner(param1Context.getResources(), param1XmlPullParser, param1AttributeSet, param1Theme);
      } catch (Exception param1Context) {
        Log.e("VdcInflateDelegate", "Exception while inflating <vector>", param1Context);
        return null;
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AppCompatDrawableManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */