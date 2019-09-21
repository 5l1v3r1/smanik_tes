package android.support.v7.widget;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.WrappedDrawable;
import android.support.v7.graphics.drawable.DrawableWrapper;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class DrawableUtils {
  public static final Rect INSETS_NONE = new Rect();
  
  private static final String TAG = "DrawableUtils";
  
  private static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
  
  private static Class<?> sInsetsClazz;
  
  static  {
    if (Build.VERSION.SDK_INT >= 18)
      try {
        sInsetsClazz = Class.forName("android.graphics.Insets");
        return;
      } catch (ClassNotFoundException classNotFoundException) {
        return;
      }  
  }
  
  public static boolean canSafelyMutateDrawable(@NonNull Drawable paramDrawable) {
    Drawable[] arrayOfDrawable;
    if (Build.VERSION.SDK_INT < 15 && paramDrawable instanceof android.graphics.drawable.InsetDrawable)
      return false; 
    if (Build.VERSION.SDK_INT < 15 && paramDrawable instanceof android.graphics.drawable.GradientDrawable)
      return false; 
    if (Build.VERSION.SDK_INT < 17 && paramDrawable instanceof android.graphics.drawable.LayerDrawable)
      return false; 
    if (paramDrawable instanceof DrawableContainer) {
      arrayOfDrawable = paramDrawable.getConstantState();
      if (arrayOfDrawable instanceof DrawableContainer.DrawableContainerState) {
        arrayOfDrawable = ((DrawableContainer.DrawableContainerState)arrayOfDrawable).getChildren();
        int i = arrayOfDrawable.length;
        for (byte b = 0; b < i; b++) {
          if (!canSafelyMutateDrawable(arrayOfDrawable[b]))
            return false; 
        } 
      } 
    } else {
      if (arrayOfDrawable instanceof WrappedDrawable)
        return canSafelyMutateDrawable(((WrappedDrawable)arrayOfDrawable).getWrappedDrawable()); 
      if (arrayOfDrawable instanceof DrawableWrapper)
        return canSafelyMutateDrawable(((DrawableWrapper)arrayOfDrawable).getWrappedDrawable()); 
      if (arrayOfDrawable instanceof ScaleDrawable)
        return canSafelyMutateDrawable(((ScaleDrawable)arrayOfDrawable).getDrawable()); 
    } 
    return true;
  }
  
  static void fixDrawable(@NonNull Drawable paramDrawable) {
    if (Build.VERSION.SDK_INT == 21 && "android.graphics.drawable.VectorDrawable".equals(paramDrawable.getClass().getName()))
      fixVectorDrawableTinting(paramDrawable); 
  }
  
  private static void fixVectorDrawableTinting(Drawable paramDrawable) {
    int[] arrayOfInt = paramDrawable.getState();
    if (arrayOfInt == null || arrayOfInt.length == 0) {
      paramDrawable.setState(ThemeUtils.CHECKED_STATE_SET);
    } else {
      paramDrawable.setState(ThemeUtils.EMPTY_STATE_SET);
    } 
    paramDrawable.setState(arrayOfInt);
  }
  
  public static Rect getOpticalBounds(Drawable paramDrawable) { // Byte code:
    //   0: getstatic android/support/v7/widget/DrawableUtils.sInsetsClazz : Ljava/lang/Class;
    //   3: ifnull -> 242
    //   6: aload_0
    //   7: invokestatic unwrap : (Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
    //   10: astore_0
    //   11: aload_0
    //   12: invokevirtual getClass : ()Ljava/lang/Class;
    //   15: ldc 'getOpticalInsets'
    //   17: iconst_0
    //   18: anewarray java/lang/Class
    //   21: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   24: aload_0
    //   25: iconst_0
    //   26: anewarray java/lang/Object
    //   29: invokevirtual invoke : (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   32: astore_0
    //   33: aload_0
    //   34: ifnull -> 242
    //   37: new android/graphics/Rect
    //   40: dup
    //   41: invokespecial <init> : ()V
    //   44: astore #4
    //   46: getstatic android/support/v7/widget/DrawableUtils.sInsetsClazz : Ljava/lang/Class;
    //   49: invokevirtual getFields : ()[Ljava/lang/reflect/Field;
    //   52: astore #5
    //   54: aload #5
    //   56: arraylength
    //   57: istore_3
    //   58: iconst_0
    //   59: istore_2
    //   60: iload_2
    //   61: iload_3
    //   62: if_icmpge -> 231
    //   65: aload #5
    //   67: iload_2
    //   68: aaload
    //   69: astore #6
    //   71: aload #6
    //   73: invokevirtual getName : ()Ljava/lang/String;
    //   76: astore #7
    //   78: aload #7
    //   80: invokevirtual hashCode : ()I
    //   83: istore_1
    //   84: iload_1
    //   85: ldc -1383228885
    //   87: if_icmpeq -> 156
    //   90: iload_1
    //   91: ldc 115029
    //   93: if_icmpeq -> 141
    //   96: iload_1
    //   97: ldc 3317767
    //   99: if_icmpeq -> 126
    //   102: iload_1
    //   103: ldc 108511772
    //   105: if_icmpeq -> 111
    //   108: goto -> 250
    //   111: aload #7
    //   113: ldc 'right'
    //   115: invokevirtual equals : (Ljava/lang/Object;)Z
    //   118: ifeq -> 250
    //   121: iconst_2
    //   122: istore_1
    //   123: goto -> 252
    //   126: aload #7
    //   128: ldc 'left'
    //   130: invokevirtual equals : (Ljava/lang/Object;)Z
    //   133: ifeq -> 250
    //   136: iconst_0
    //   137: istore_1
    //   138: goto -> 252
    //   141: aload #7
    //   143: ldc 'top'
    //   145: invokevirtual equals : (Ljava/lang/Object;)Z
    //   148: ifeq -> 250
    //   151: iconst_1
    //   152: istore_1
    //   153: goto -> 252
    //   156: aload #7
    //   158: ldc 'bottom'
    //   160: invokevirtual equals : (Ljava/lang/Object;)Z
    //   163: ifeq -> 250
    //   166: iconst_3
    //   167: istore_1
    //   168: goto -> 252
    //   171: aload #4
    //   173: aload #6
    //   175: aload_0
    //   176: invokevirtual getInt : (Ljava/lang/Object;)I
    //   179: putfield bottom : I
    //   182: goto -> 224
    //   185: aload #4
    //   187: aload #6
    //   189: aload_0
    //   190: invokevirtual getInt : (Ljava/lang/Object;)I
    //   193: putfield right : I
    //   196: goto -> 224
    //   199: aload #4
    //   201: aload #6
    //   203: aload_0
    //   204: invokevirtual getInt : (Ljava/lang/Object;)I
    //   207: putfield top : I
    //   210: goto -> 224
    //   213: aload #4
    //   215: aload #6
    //   217: aload_0
    //   218: invokevirtual getInt : (Ljava/lang/Object;)I
    //   221: putfield left : I
    //   224: iload_2
    //   225: iconst_1
    //   226: iadd
    //   227: istore_2
    //   228: goto -> 60
    //   231: aload #4
    //   233: areturn
    //   234: ldc 'DrawableUtils'
    //   236: ldc 'Couldn't obtain the optical insets. Ignoring.'
    //   238: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   241: pop
    //   242: getstatic android/support/v7/widget/DrawableUtils.INSETS_NONE : Landroid/graphics/Rect;
    //   245: areturn
    //   246: astore_0
    //   247: goto -> 234
    //   250: iconst_m1
    //   251: istore_1
    //   252: iload_1
    //   253: tableswitch default -> 284, 0 -> 213, 1 -> 199, 2 -> 185, 3 -> 171
    //   284: goto -> 224
    // Exception table:
    //   from	to	target	type
    //   6	33	246	java/lang/Exception
    //   37	58	246	java/lang/Exception
    //   71	84	246	java/lang/Exception
    //   111	121	246	java/lang/Exception
    //   126	136	246	java/lang/Exception
    //   141	151	246	java/lang/Exception
    //   156	166	246	java/lang/Exception
    //   171	182	246	java/lang/Exception
    //   185	196	246	java/lang/Exception
    //   199	210	246	java/lang/Exception
    //   213	224	246	java/lang/Exception }
  
  public static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode) {
    if (paramInt != 3) {
      if (paramInt != 5) {
        if (paramInt != 9) {
          switch (paramInt) {
            default:
              return paramMode;
            case 16:
              return PorterDuff.Mode.ADD;
            case 15:
              return PorterDuff.Mode.SCREEN;
            case 14:
              break;
          } 
          return PorterDuff.Mode.MULTIPLY;
        } 
        return PorterDuff.Mode.SRC_ATOP;
      } 
      return PorterDuff.Mode.SRC_IN;
    } 
    return PorterDuff.Mode.SRC_OVER;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/DrawableUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */