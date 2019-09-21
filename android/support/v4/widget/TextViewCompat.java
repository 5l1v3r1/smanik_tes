package android.support.v4.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class TextViewCompat {
  public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
  
  public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
  
  static final TextViewCompatBaseImpl IMPL;
  
  static  {
    if (BuildCompat.isAtLeastOMR1()) {
      IMPL = new TextViewCompatApi27Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 26) {
      IMPL = new TextViewCompatApi26Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 23) {
      IMPL = new TextViewCompatApi23Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 18) {
      IMPL = new TextViewCompatApi18Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 17) {
      IMPL = new TextViewCompatApi17Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16) {
      IMPL = new TextViewCompatApi16Impl();
      return;
    } 
    IMPL = new TextViewCompatBaseImpl();
  }
  
  public static int getAutoSizeMaxTextSize(@NonNull TextView paramTextView) { return IMPL.getAutoSizeMaxTextSize(paramTextView); }
  
  public static int getAutoSizeMinTextSize(@NonNull TextView paramTextView) { return IMPL.getAutoSizeMinTextSize(paramTextView); }
  
  public static int getAutoSizeStepGranularity(@NonNull TextView paramTextView) { return IMPL.getAutoSizeStepGranularity(paramTextView); }
  
  @NonNull
  public static int[] getAutoSizeTextAvailableSizes(@NonNull TextView paramTextView) { return IMPL.getAutoSizeTextAvailableSizes(paramTextView); }
  
  public static int getAutoSizeTextType(@NonNull TextView paramTextView) { return IMPL.getAutoSizeTextType(paramTextView); }
  
  @NonNull
  public static Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView) { return IMPL.getCompoundDrawablesRelative(paramTextView); }
  
  public static int getMaxLines(@NonNull TextView paramTextView) { return IMPL.getMaxLines(paramTextView); }
  
  public static int getMinLines(@NonNull TextView paramTextView) { return IMPL.getMinLines(paramTextView); }
  
  public static void setAutoSizeTextTypeUniformWithConfiguration(@NonNull TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IllegalArgumentException { IMPL.setAutoSizeTextTypeUniformWithConfiguration(paramTextView, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull TextView paramTextView, @NonNull int[] paramArrayOfInt, int paramInt) throws IllegalArgumentException { IMPL.setAutoSizeTextTypeUniformWithPresetSizes(paramTextView, paramArrayOfInt, paramInt); }
  
  public static void setAutoSizeTextTypeWithDefaults(@NonNull TextView paramTextView, int paramInt) { IMPL.setAutoSizeTextTypeWithDefaults(paramTextView, paramInt); }
  
  public static void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4) { IMPL.setCompoundDrawablesRelative(paramTextView, paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4); }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @DrawableRes int paramInt1, @DrawableRes int paramInt2, @DrawableRes int paramInt3, @DrawableRes int paramInt4) throws IllegalArgumentException { IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(paramTextView, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4) { IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(paramTextView, paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4); }
  
  public static void setCustomSelectionActionModeCallback(@NonNull TextView paramTextView, @NonNull ActionMode.Callback paramCallback) { IMPL.setCustomSelectionActionModeCallback(paramTextView, paramCallback); }
  
  public static void setTextAppearance(@NonNull TextView paramTextView, @StyleRes int paramInt) { IMPL.setTextAppearance(paramTextView, paramInt); }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface AutoSizeTextType {}
  
  @RequiresApi(16)
  static class TextViewCompatApi16Impl extends TextViewCompatBaseImpl {
    public int getMaxLines(TextView param1TextView) { return param1TextView.getMaxLines(); }
    
    public int getMinLines(TextView param1TextView) { return param1TextView.getMinLines(); }
  }
  
  @RequiresApi(17)
  static class TextViewCompatApi17Impl extends TextViewCompatApi16Impl {
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView param1TextView) {
      int i = param1TextView.getLayoutDirection();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      Drawable[] arrayOfDrawable = param1TextView.getCompoundDrawables();
      if (bool) {
        Drawable drawable1 = arrayOfDrawable[2];
        Drawable drawable2 = arrayOfDrawable[0];
        arrayOfDrawable[0] = drawable1;
        arrayOfDrawable[2] = drawable2;
      } 
      return arrayOfDrawable;
    }
    
    public void setCompoundDrawablesRelative(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) {
      Drawable drawable;
      int i = param1TextView.getLayoutDirection();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      if (bool) {
        drawable = param1Drawable3;
      } else {
        drawable = param1Drawable1;
      } 
      if (!bool)
        param1Drawable1 = param1Drawable3; 
      param1TextView.setCompoundDrawables(drawable, param1Drawable2, param1Drawable1, param1Drawable4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @DrawableRes int param1Int1, @DrawableRes int param1Int2, @DrawableRes int param1Int3, @DrawableRes int param1Int4) throws IllegalArgumentException {
      int i = param1TextView.getLayoutDirection();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      if (bool) {
        i = param1Int3;
      } else {
        i = param1Int1;
      } 
      if (!bool)
        param1Int1 = param1Int3; 
      param1TextView.setCompoundDrawablesWithIntrinsicBounds(i, param1Int2, param1Int1, param1Int4);
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) {
      Drawable drawable;
      int i = param1TextView.getLayoutDirection();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      if (bool) {
        drawable = param1Drawable3;
      } else {
        drawable = param1Drawable1;
      } 
      if (!bool)
        param1Drawable1 = param1Drawable3; 
      param1TextView.setCompoundDrawablesWithIntrinsicBounds(drawable, param1Drawable2, param1Drawable1, param1Drawable4);
    }
  }
  
  @RequiresApi(18)
  static class TextViewCompatApi18Impl extends TextViewCompatApi17Impl {
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView param1TextView) { return param1TextView.getCompoundDrawablesRelative(); }
    
    public void setCompoundDrawablesRelative(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) { param1TextView.setCompoundDrawablesRelative(param1Drawable1, param1Drawable2, param1Drawable3, param1Drawable4); }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @DrawableRes int param1Int1, @DrawableRes int param1Int2, @DrawableRes int param1Int3, @DrawableRes int param1Int4) throws IllegalArgumentException { param1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) { param1TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(param1Drawable1, param1Drawable2, param1Drawable3, param1Drawable4); }
  }
  
  @RequiresApi(23)
  static class TextViewCompatApi23Impl extends TextViewCompatApi18Impl {
    public void setTextAppearance(@NonNull TextView param1TextView, @StyleRes int param1Int) { param1TextView.setTextAppearance(param1Int); }
  }
  
  @RequiresApi(26)
  static class TextViewCompatApi26Impl extends TextViewCompatApi23Impl {
    public void setCustomSelectionActionModeCallback(final TextView textView, final ActionMode.Callback callback) {
      if (Build.VERSION.SDK_INT != 26 && Build.VERSION.SDK_INT != 27) {
        super.setCustomSelectionActionModeCallback(param1TextView, param1Callback);
        return;
      } 
      param1TextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
            
            private boolean mCanUseMenuBuilderReferences;
            
            private boolean mInitializedMenuBuilderReferences = false;
            
            private Class mMenuBuilderClass;
            
            private Method mMenuBuilderRemoveItemAtMethod;
            
            private Intent createProcessTextIntent() { return (new Intent()).setAction("android.intent.action.PROCESS_TEXT").setType("text/plain"); }
            
            private Intent createProcessTextIntentForResolveInfo(ResolveInfo param2ResolveInfo, TextView param2TextView) { return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", isEditable(param2TextView) ^ true).setClassName(param2ResolveInfo.activityInfo.packageName, param2ResolveInfo.activityInfo.name); }
            
            private List<ResolveInfo> getSupportedActivities(Context param2Context, PackageManager param2PackageManager) {
              ArrayList arrayList = new ArrayList();
              if (!(param2Context instanceof android.app.Activity))
                return arrayList; 
              for (ResolveInfo resolveInfo : param2PackageManager.queryIntentActivities(createProcessTextIntent(), 0)) {
                if (isSupportedActivity(resolveInfo, param2Context))
                  arrayList.add(resolveInfo); 
              } 
              return arrayList;
            }
            
            private boolean isEditable(TextView param2TextView) { return (param2TextView instanceof android.text.Editable && param2TextView.onCheckIsTextEditor() && param2TextView.isEnabled()); }
            
            private boolean isSupportedActivity(ResolveInfo param2ResolveInfo, Context param2Context) {
              boolean bool2 = param2Context.getPackageName().equals(param2ResolveInfo.activityInfo.packageName);
              boolean bool1 = true;
              if (bool2)
                return true; 
              if (!param2ResolveInfo.activityInfo.exported)
                return false; 
              if (param2ResolveInfo.activityInfo.permission != null) {
                if (param2Context.checkSelfPermission(param2ResolveInfo.activityInfo.permission) == 0)
                  return true; 
                bool1 = false;
              } 
              return bool1;
            }
            
            private void recomputeProcessTextMenuItems(Menu param2Menu) { // Byte code:
              //   0: aload_0
              //   1: getfield val$textView : Landroid/widget/TextView;
              //   4: invokevirtual getContext : ()Landroid/content/Context;
              //   7: astore #5
              //   9: aload #5
              //   11: invokevirtual getPackageManager : ()Landroid/content/pm/PackageManager;
              //   14: astore #4
              //   16: aload_0
              //   17: getfield mInitializedMenuBuilderReferences : Z
              //   20: ifne -> 83
              //   23: aload_0
              //   24: iconst_1
              //   25: putfield mInitializedMenuBuilderReferences : Z
              //   28: aload_0
              //   29: ldc 'com.android.internal.view.menu.MenuBuilder'
              //   31: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
              //   34: putfield mMenuBuilderClass : Ljava/lang/Class;
              //   37: aload_0
              //   38: aload_0
              //   39: getfield mMenuBuilderClass : Ljava/lang/Class;
              //   42: ldc 'removeItemAt'
              //   44: iconst_1
              //   45: anewarray java/lang/Class
              //   48: dup
              //   49: iconst_0
              //   50: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
              //   53: aastore
              //   54: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
              //   57: putfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
              //   60: aload_0
              //   61: iconst_1
              //   62: putfield mCanUseMenuBuilderReferences : Z
              //   65: goto -> 83
              //   68: aload_0
              //   69: aconst_null
              //   70: putfield mMenuBuilderClass : Ljava/lang/Class;
              //   73: aload_0
              //   74: aconst_null
              //   75: putfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
              //   78: aload_0
              //   79: iconst_0
              //   80: putfield mCanUseMenuBuilderReferences : Z
              //   83: aload_0
              //   84: getfield mCanUseMenuBuilderReferences : Z
              //   87: ifeq -> 109
              //   90: aload_0
              //   91: getfield mMenuBuilderClass : Ljava/lang/Class;
              //   94: aload_1
              //   95: invokevirtual isInstance : (Ljava/lang/Object;)Z
              //   98: ifeq -> 109
              //   101: aload_0
              //   102: getfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
              //   105: astore_3
              //   106: goto -> 129
              //   109: aload_1
              //   110: invokevirtual getClass : ()Ljava/lang/Class;
              //   113: ldc 'removeItemAt'
              //   115: iconst_1
              //   116: anewarray java/lang/Class
              //   119: dup
              //   120: iconst_0
              //   121: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
              //   124: aastore
              //   125: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
              //   128: astore_3
              //   129: aload_1
              //   130: invokeinterface size : ()I
              //   135: iconst_1
              //   136: isub
              //   137: istore_2
              //   138: iload_2
              //   139: iflt -> 203
              //   142: aload_1
              //   143: iload_2
              //   144: invokeinterface getItem : (I)Landroid/view/MenuItem;
              //   149: astore #6
              //   151: aload #6
              //   153: invokeinterface getIntent : ()Landroid/content/Intent;
              //   158: ifnull -> 196
              //   161: ldc 'android.intent.action.PROCESS_TEXT'
              //   163: aload #6
              //   165: invokeinterface getIntent : ()Landroid/content/Intent;
              //   170: invokevirtual getAction : ()Ljava/lang/String;
              //   173: invokevirtual equals : (Ljava/lang/Object;)Z
              //   176: ifeq -> 196
              //   179: aload_3
              //   180: aload_1
              //   181: iconst_1
              //   182: anewarray java/lang/Object
              //   185: dup
              //   186: iconst_0
              //   187: iload_2
              //   188: invokestatic valueOf : (I)Ljava/lang/Integer;
              //   191: aastore
              //   192: invokevirtual invoke : (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
              //   195: pop
              //   196: iload_2
              //   197: iconst_1
              //   198: isub
              //   199: istore_2
              //   200: goto -> 138
              //   203: aload_0
              //   204: aload #5
              //   206: aload #4
              //   208: invokespecial getSupportedActivities : (Landroid/content/Context;Landroid/content/pm/PackageManager;)Ljava/util/List;
              //   211: astore_3
              //   212: iconst_0
              //   213: istore_2
              //   214: iload_2
              //   215: aload_3
              //   216: invokeinterface size : ()I
              //   221: if_icmpge -> 283
              //   224: aload_3
              //   225: iload_2
              //   226: invokeinterface get : (I)Ljava/lang/Object;
              //   231: checkcast android/content/pm/ResolveInfo
              //   234: astore #5
              //   236: aload_1
              //   237: iconst_0
              //   238: iconst_0
              //   239: iload_2
              //   240: bipush #100
              //   242: iadd
              //   243: aload #5
              //   245: aload #4
              //   247: invokevirtual loadLabel : (Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
              //   250: invokeinterface add : (IIILjava/lang/CharSequence;)Landroid/view/MenuItem;
              //   255: aload_0
              //   256: aload #5
              //   258: aload_0
              //   259: getfield val$textView : Landroid/widget/TextView;
              //   262: invokespecial createProcessTextIntentForResolveInfo : (Landroid/content/pm/ResolveInfo;Landroid/widget/TextView;)Landroid/content/Intent;
              //   265: invokeinterface setIntent : (Landroid/content/Intent;)Landroid/view/MenuItem;
              //   270: iconst_1
              //   271: invokeinterface setShowAsAction : (I)V
              //   276: iload_2
              //   277: iconst_1
              //   278: iadd
              //   279: istore_2
              //   280: goto -> 214
              //   283: return
              //   284: astore_3
              //   285: goto -> 68
              //   288: astore_1
              //   289: return
              // Exception table:
              //   from	to	target	type
              //   28	65	284	java/lang/ClassNotFoundException
              //   28	65	284	java/lang/NoSuchMethodException
              //   83	106	288	java/lang/NoSuchMethodException
              //   83	106	288	java/lang/IllegalAccessException
              //   83	106	288	java/lang/reflect/InvocationTargetException
              //   109	129	288	java/lang/NoSuchMethodException
              //   109	129	288	java/lang/IllegalAccessException
              //   109	129	288	java/lang/reflect/InvocationTargetException
              //   129	138	288	java/lang/NoSuchMethodException
              //   129	138	288	java/lang/IllegalAccessException
              //   129	138	288	java/lang/reflect/InvocationTargetException
              //   142	196	288	java/lang/NoSuchMethodException
              //   142	196	288	java/lang/IllegalAccessException
              //   142	196	288	java/lang/reflect/InvocationTargetException }
            
            public boolean onActionItemClicked(ActionMode param2ActionMode, MenuItem param2MenuItem) { return callback.onActionItemClicked(param2ActionMode, param2MenuItem); }
            
            public boolean onCreateActionMode(ActionMode param2ActionMode, Menu param2Menu) { return callback.onCreateActionMode(param2ActionMode, param2Menu); }
            
            public void onDestroyActionMode(ActionMode param2ActionMode) { callback.onDestroyActionMode(param2ActionMode); }
            
            public boolean onPrepareActionMode(ActionMode param2ActionMode, Menu param2Menu) {
              recomputeProcessTextMenuItems(param2Menu);
              return callback.onPrepareActionMode(param2ActionMode, param2Menu);
            }
          });
    }
  }
  
  class null implements ActionMode.Callback {
    private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
    
    private boolean mCanUseMenuBuilderReferences;
    
    private boolean mInitializedMenuBuilderReferences = false;
    
    private Class mMenuBuilderClass;
    
    private Method mMenuBuilderRemoveItemAtMethod;
    
    private Intent createProcessTextIntent() { return (new Intent()).setAction("android.intent.action.PROCESS_TEXT").setType("text/plain"); }
    
    private Intent createProcessTextIntentForResolveInfo(ResolveInfo param1ResolveInfo, TextView param1TextView) { return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", isEditable(param1TextView) ^ true).setClassName(param1ResolveInfo.activityInfo.packageName, param1ResolveInfo.activityInfo.name); }
    
    private List<ResolveInfo> getSupportedActivities(Context param1Context, PackageManager param1PackageManager) {
      ArrayList arrayList = new ArrayList();
      if (!(param1Context instanceof android.app.Activity))
        return arrayList; 
      for (ResolveInfo resolveInfo : param1PackageManager.queryIntentActivities(createProcessTextIntent(), 0)) {
        if (isSupportedActivity(resolveInfo, param1Context))
          arrayList.add(resolveInfo); 
      } 
      return arrayList;
    }
    
    private boolean isEditable(TextView param1TextView) { return (param1TextView instanceof android.text.Editable && param1TextView.onCheckIsTextEditor() && param1TextView.isEnabled()); }
    
    private boolean isSupportedActivity(ResolveInfo param1ResolveInfo, Context param1Context) {
      boolean bool2 = param1Context.getPackageName().equals(param1ResolveInfo.activityInfo.packageName);
      boolean bool1 = true;
      if (bool2)
        return true; 
      if (!param1ResolveInfo.activityInfo.exported)
        return false; 
      if (param1ResolveInfo.activityInfo.permission != null) {
        if (param1Context.checkSelfPermission(param1ResolveInfo.activityInfo.permission) == 0)
          return true; 
        bool1 = false;
      } 
      return bool1;
    }
    
    private void recomputeProcessTextMenuItems(Menu param1Menu) { // Byte code:
      //   0: aload_0
      //   1: getfield val$textView : Landroid/widget/TextView;
      //   4: invokevirtual getContext : ()Landroid/content/Context;
      //   7: astore #5
      //   9: aload #5
      //   11: invokevirtual getPackageManager : ()Landroid/content/pm/PackageManager;
      //   14: astore #4
      //   16: aload_0
      //   17: getfield mInitializedMenuBuilderReferences : Z
      //   20: ifne -> 83
      //   23: aload_0
      //   24: iconst_1
      //   25: putfield mInitializedMenuBuilderReferences : Z
      //   28: aload_0
      //   29: ldc 'com.android.internal.view.menu.MenuBuilder'
      //   31: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
      //   34: putfield mMenuBuilderClass : Ljava/lang/Class;
      //   37: aload_0
      //   38: aload_0
      //   39: getfield mMenuBuilderClass : Ljava/lang/Class;
      //   42: ldc 'removeItemAt'
      //   44: iconst_1
      //   45: anewarray java/lang/Class
      //   48: dup
      //   49: iconst_0
      //   50: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
      //   53: aastore
      //   54: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
      //   57: putfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
      //   60: aload_0
      //   61: iconst_1
      //   62: putfield mCanUseMenuBuilderReferences : Z
      //   65: goto -> 83
      //   68: aload_0
      //   69: aconst_null
      //   70: putfield mMenuBuilderClass : Ljava/lang/Class;
      //   73: aload_0
      //   74: aconst_null
      //   75: putfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
      //   78: aload_0
      //   79: iconst_0
      //   80: putfield mCanUseMenuBuilderReferences : Z
      //   83: aload_0
      //   84: getfield mCanUseMenuBuilderReferences : Z
      //   87: ifeq -> 109
      //   90: aload_0
      //   91: getfield mMenuBuilderClass : Ljava/lang/Class;
      //   94: aload_1
      //   95: invokevirtual isInstance : (Ljava/lang/Object;)Z
      //   98: ifeq -> 109
      //   101: aload_0
      //   102: getfield mMenuBuilderRemoveItemAtMethod : Ljava/lang/reflect/Method;
      //   105: astore_3
      //   106: goto -> 129
      //   109: aload_1
      //   110: invokevirtual getClass : ()Ljava/lang/Class;
      //   113: ldc 'removeItemAt'
      //   115: iconst_1
      //   116: anewarray java/lang/Class
      //   119: dup
      //   120: iconst_0
      //   121: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
      //   124: aastore
      //   125: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
      //   128: astore_3
      //   129: aload_1
      //   130: invokeinterface size : ()I
      //   135: iconst_1
      //   136: isub
      //   137: istore_2
      //   138: iload_2
      //   139: iflt -> 203
      //   142: aload_1
      //   143: iload_2
      //   144: invokeinterface getItem : (I)Landroid/view/MenuItem;
      //   149: astore #6
      //   151: aload #6
      //   153: invokeinterface getIntent : ()Landroid/content/Intent;
      //   158: ifnull -> 196
      //   161: ldc 'android.intent.action.PROCESS_TEXT'
      //   163: aload #6
      //   165: invokeinterface getIntent : ()Landroid/content/Intent;
      //   170: invokevirtual getAction : ()Ljava/lang/String;
      //   173: invokevirtual equals : (Ljava/lang/Object;)Z
      //   176: ifeq -> 196
      //   179: aload_3
      //   180: aload_1
      //   181: iconst_1
      //   182: anewarray java/lang/Object
      //   185: dup
      //   186: iconst_0
      //   187: iload_2
      //   188: invokestatic valueOf : (I)Ljava/lang/Integer;
      //   191: aastore
      //   192: invokevirtual invoke : (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
      //   195: pop
      //   196: iload_2
      //   197: iconst_1
      //   198: isub
      //   199: istore_2
      //   200: goto -> 138
      //   203: aload_0
      //   204: aload #5
      //   206: aload #4
      //   208: invokespecial getSupportedActivities : (Landroid/content/Context;Landroid/content/pm/PackageManager;)Ljava/util/List;
      //   211: astore_3
      //   212: iconst_0
      //   213: istore_2
      //   214: iload_2
      //   215: aload_3
      //   216: invokeinterface size : ()I
      //   221: if_icmpge -> 283
      //   224: aload_3
      //   225: iload_2
      //   226: invokeinterface get : (I)Ljava/lang/Object;
      //   231: checkcast android/content/pm/ResolveInfo
      //   234: astore #5
      //   236: aload_1
      //   237: iconst_0
      //   238: iconst_0
      //   239: iload_2
      //   240: bipush #100
      //   242: iadd
      //   243: aload #5
      //   245: aload #4
      //   247: invokevirtual loadLabel : (Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
      //   250: invokeinterface add : (IIILjava/lang/CharSequence;)Landroid/view/MenuItem;
      //   255: aload_0
      //   256: aload #5
      //   258: aload_0
      //   259: getfield val$textView : Landroid/widget/TextView;
      //   262: invokespecial createProcessTextIntentForResolveInfo : (Landroid/content/pm/ResolveInfo;Landroid/widget/TextView;)Landroid/content/Intent;
      //   265: invokeinterface setIntent : (Landroid/content/Intent;)Landroid/view/MenuItem;
      //   270: iconst_1
      //   271: invokeinterface setShowAsAction : (I)V
      //   276: iload_2
      //   277: iconst_1
      //   278: iadd
      //   279: istore_2
      //   280: goto -> 214
      //   283: return
      //   284: astore_3
      //   285: goto -> 68
      //   288: astore_1
      //   289: return
      // Exception table:
      //   from	to	target	type
      //   28	65	284	java/lang/ClassNotFoundException
      //   28	65	284	java/lang/NoSuchMethodException
      //   83	106	288	java/lang/NoSuchMethodException
      //   83	106	288	java/lang/IllegalAccessException
      //   83	106	288	java/lang/reflect/InvocationTargetException
      //   109	129	288	java/lang/NoSuchMethodException
      //   109	129	288	java/lang/IllegalAccessException
      //   109	129	288	java/lang/reflect/InvocationTargetException
      //   129	138	288	java/lang/NoSuchMethodException
      //   129	138	288	java/lang/IllegalAccessException
      //   129	138	288	java/lang/reflect/InvocationTargetException
      //   142	196	288	java/lang/NoSuchMethodException
      //   142	196	288	java/lang/IllegalAccessException
      //   142	196	288	java/lang/reflect/InvocationTargetException }
    
    public boolean onActionItemClicked(ActionMode param1ActionMode, MenuItem param1MenuItem) { return callback.onActionItemClicked(param1ActionMode, param1MenuItem); }
    
    public boolean onCreateActionMode(ActionMode param1ActionMode, Menu param1Menu) { return callback.onCreateActionMode(param1ActionMode, param1Menu); }
    
    public void onDestroyActionMode(ActionMode param1ActionMode) { callback.onDestroyActionMode(param1ActionMode); }
    
    public boolean onPrepareActionMode(ActionMode param1ActionMode, Menu param1Menu) {
      recomputeProcessTextMenuItems(param1Menu);
      return callback.onPrepareActionMode(param1ActionMode, param1Menu);
    }
  }
  
  @RequiresApi(27)
  static class TextViewCompatApi27Impl extends TextViewCompatApi26Impl {
    public int getAutoSizeMaxTextSize(TextView param1TextView) { return param1TextView.getAutoSizeMaxTextSize(); }
    
    public int getAutoSizeMinTextSize(TextView param1TextView) { return param1TextView.getAutoSizeMinTextSize(); }
    
    public int getAutoSizeStepGranularity(TextView param1TextView) { return param1TextView.getAutoSizeStepGranularity(); }
    
    public int[] getAutoSizeTextAvailableSizes(TextView param1TextView) { return param1TextView.getAutoSizeTextAvailableSizes(); }
    
    public int getAutoSizeTextType(TextView param1TextView) { return param1TextView.getAutoSizeTextType(); }
    
    public void setAutoSizeTextTypeUniformWithConfiguration(TextView param1TextView, int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws IllegalArgumentException { param1TextView.setAutoSizeTextTypeUniformWithConfiguration(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void setAutoSizeTextTypeUniformWithPresetSizes(TextView param1TextView, @NonNull int[] param1ArrayOfInt, int param1Int) throws IllegalArgumentException { param1TextView.setAutoSizeTextTypeUniformWithPresetSizes(param1ArrayOfInt, param1Int); }
    
    public void setAutoSizeTextTypeWithDefaults(TextView param1TextView, int param1Int) { param1TextView.setAutoSizeTextTypeWithDefaults(param1Int); }
  }
  
  static class TextViewCompatBaseImpl {
    private static final int LINES = 1;
    
    private static final String LOG_TAG = "TextViewCompatBase";
    
    private static Field sMaxModeField;
    
    private static boolean sMaxModeFieldFetched;
    
    private static Field sMaximumField;
    
    private static boolean sMaximumFieldFetched;
    
    private static Field sMinModeField;
    
    private static boolean sMinModeFieldFetched;
    
    private static Field sMinimumField;
    
    private static boolean sMinimumFieldFetched;
    
    private static Field retrieveField(String param1String) {
      try {
        noSuchFieldException = TextView.class.getDeclaredField(param1String);
        try {
          noSuchFieldException.setAccessible(true);
          return noSuchFieldException;
        } catch (NoSuchFieldException noSuchFieldException1) {}
      } catch (NoSuchFieldException noSuchFieldException) {
        noSuchFieldException = null;
      } 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Could not retrieve ");
      stringBuilder.append(param1String);
      stringBuilder.append(" field.");
      Log.e("TextViewCompatBase", stringBuilder.toString());
      return noSuchFieldException;
    }
    
    private static int retrieveIntFromField(Field param1Field, TextView param1TextView) {
      try {
        return param1Field.getInt(param1TextView);
      } catch (IllegalAccessException param1TextView) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not retrieve value of ");
        stringBuilder.append(param1Field.getName());
        stringBuilder.append(" field.");
        Log.d("TextViewCompatBase", stringBuilder.toString());
        return -1;
      } 
    }
    
    public int getAutoSizeMaxTextSize(TextView param1TextView) { return (param1TextView instanceof AutoSizeableTextView) ? ((AutoSizeableTextView)param1TextView).getAutoSizeMaxTextSize() : -1; }
    
    public int getAutoSizeMinTextSize(TextView param1TextView) { return (param1TextView instanceof AutoSizeableTextView) ? ((AutoSizeableTextView)param1TextView).getAutoSizeMinTextSize() : -1; }
    
    public int getAutoSizeStepGranularity(TextView param1TextView) { return (param1TextView instanceof AutoSizeableTextView) ? ((AutoSizeableTextView)param1TextView).getAutoSizeStepGranularity() : -1; }
    
    public int[] getAutoSizeTextAvailableSizes(TextView param1TextView) { return (param1TextView instanceof AutoSizeableTextView) ? ((AutoSizeableTextView)param1TextView).getAutoSizeTextAvailableSizes() : new int[0]; }
    
    public int getAutoSizeTextType(TextView param1TextView) { return (param1TextView instanceof AutoSizeableTextView) ? ((AutoSizeableTextView)param1TextView).getAutoSizeTextType() : 0; }
    
    public Drawable[] getCompoundDrawablesRelative(@NonNull TextView param1TextView) { return param1TextView.getCompoundDrawables(); }
    
    public int getMaxLines(TextView param1TextView) {
      if (!sMaxModeFieldFetched) {
        sMaxModeField = retrieveField("mMaxMode");
        sMaxModeFieldFetched = true;
      } 
      if (sMaxModeField != null && retrieveIntFromField(sMaxModeField, param1TextView) == 1) {
        if (!sMaximumFieldFetched) {
          sMaximumField = retrieveField("mMaximum");
          sMaximumFieldFetched = true;
        } 
        if (sMaximumField != null)
          return retrieveIntFromField(sMaximumField, param1TextView); 
      } 
      return -1;
    }
    
    public int getMinLines(TextView param1TextView) {
      if (!sMinModeFieldFetched) {
        sMinModeField = retrieveField("mMinMode");
        sMinModeFieldFetched = true;
      } 
      if (sMinModeField != null && retrieveIntFromField(sMinModeField, param1TextView) == 1) {
        if (!sMinimumFieldFetched) {
          sMinimumField = retrieveField("mMinimum");
          sMinimumFieldFetched = true;
        } 
        if (sMinimumField != null)
          return retrieveIntFromField(sMinimumField, param1TextView); 
      } 
      return -1;
    }
    
    public void setAutoSizeTextTypeUniformWithConfiguration(TextView param1TextView, int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws IllegalArgumentException {
      if (param1TextView instanceof AutoSizeableTextView)
        ((AutoSizeableTextView)param1TextView).setAutoSizeTextTypeUniformWithConfiguration(param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void setAutoSizeTextTypeUniformWithPresetSizes(TextView param1TextView, @NonNull int[] param1ArrayOfInt, int param1Int) throws IllegalArgumentException {
      if (param1TextView instanceof AutoSizeableTextView)
        ((AutoSizeableTextView)param1TextView).setAutoSizeTextTypeUniformWithPresetSizes(param1ArrayOfInt, param1Int); 
    }
    
    public void setAutoSizeTextTypeWithDefaults(TextView param1TextView, int param1Int) {
      if (param1TextView instanceof AutoSizeableTextView)
        ((AutoSizeableTextView)param1TextView).setAutoSizeTextTypeWithDefaults(param1Int); 
    }
    
    public void setCompoundDrawablesRelative(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) { param1TextView.setCompoundDrawables(param1Drawable1, param1Drawable2, param1Drawable3, param1Drawable4); }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @DrawableRes int param1Int1, @DrawableRes int param1Int2, @DrawableRes int param1Int3, @DrawableRes int param1Int4) throws IllegalArgumentException { param1TextView.setCompoundDrawablesWithIntrinsicBounds(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView param1TextView, @Nullable Drawable param1Drawable1, @Nullable Drawable param1Drawable2, @Nullable Drawable param1Drawable3, @Nullable Drawable param1Drawable4) { param1TextView.setCompoundDrawablesWithIntrinsicBounds(param1Drawable1, param1Drawable2, param1Drawable3, param1Drawable4); }
    
    public void setCustomSelectionActionModeCallback(TextView param1TextView, ActionMode.Callback param1Callback) { param1TextView.setCustomSelectionActionModeCallback(param1Callback); }
    
    public void setTextAppearance(TextView param1TextView, @StyleRes int param1Int) { param1TextView.setTextAppearance(param1TextView.getContext(), param1Int); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/TextViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */