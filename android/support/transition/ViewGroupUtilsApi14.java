package android.support.transition;

import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(14)
class ViewGroupUtilsApi14 implements ViewGroupUtilsImpl {
  private static final int LAYOUT_TRANSITION_CHANGING = 4;
  
  private static final String TAG = "ViewGroupUtilsApi14";
  
  private static Method sCancelMethod;
  
  private static boolean sCancelMethodFetched;
  
  private static LayoutTransition sEmptyLayoutTransition;
  
  private static Field sLayoutSuppressedField;
  
  private static boolean sLayoutSuppressedFieldFetched;
  
  private static void cancelLayoutTransition(LayoutTransition paramLayoutTransition) {
    if (!sCancelMethodFetched) {
      try {
        sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
        sCancelMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
      } 
      sCancelMethodFetched = true;
    } 
    if (sCancelMethod != null)
      try {
        sCancelMethod.invoke(paramLayoutTransition, new Object[0]);
        return;
      } catch (IllegalAccessException paramLayoutTransition) {
        Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
        return;
      } catch (InvocationTargetException paramLayoutTransition) {
        Log.i("ViewGroupUtilsApi14", "Failed to invoke cancel method by reflection");
        return;
      }  
  }
  
  public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup) { return ViewGroupOverlayApi14.createFrom(paramViewGroup); }
  
  public void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean) { // Byte code:
    //   0: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   3: astore #5
    //   5: iconst_0
    //   6: istore #4
    //   8: iconst_0
    //   9: istore_3
    //   10: aload #5
    //   12: ifnonnull -> 66
    //   15: new android/support/transition/ViewGroupUtilsApi14$1
    //   18: dup
    //   19: aload_0
    //   20: invokespecial <init> : (Landroid/support/transition/ViewGroupUtilsApi14;)V
    //   23: putstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   26: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   29: iconst_2
    //   30: aconst_null
    //   31: invokevirtual setAnimator : (ILandroid/animation/Animator;)V
    //   34: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   37: iconst_0
    //   38: aconst_null
    //   39: invokevirtual setAnimator : (ILandroid/animation/Animator;)V
    //   42: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   45: iconst_1
    //   46: aconst_null
    //   47: invokevirtual setAnimator : (ILandroid/animation/Animator;)V
    //   50: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   53: iconst_3
    //   54: aconst_null
    //   55: invokevirtual setAnimator : (ILandroid/animation/Animator;)V
    //   58: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   61: iconst_4
    //   62: aconst_null
    //   63: invokevirtual setAnimator : (ILandroid/animation/Animator;)V
    //   66: iload_2
    //   67: ifeq -> 119
    //   70: aload_1
    //   71: invokevirtual getLayoutTransition : ()Landroid/animation/LayoutTransition;
    //   74: astore #5
    //   76: aload #5
    //   78: ifnull -> 111
    //   81: aload #5
    //   83: invokevirtual isRunning : ()Z
    //   86: ifeq -> 94
    //   89: aload #5
    //   91: invokestatic cancelLayoutTransition : (Landroid/animation/LayoutTransition;)V
    //   94: aload #5
    //   96: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   99: if_acmpeq -> 111
    //   102: aload_1
    //   103: getstatic android/support/transition/R$id.transition_layout_save : I
    //   106: aload #5
    //   108: invokevirtual setTag : (ILjava/lang/Object;)V
    //   111: aload_1
    //   112: getstatic android/support/transition/ViewGroupUtilsApi14.sEmptyLayoutTransition : Landroid/animation/LayoutTransition;
    //   115: invokevirtual setLayoutTransition : (Landroid/animation/LayoutTransition;)V
    //   118: return
    //   119: aload_1
    //   120: aconst_null
    //   121: invokevirtual setLayoutTransition : (Landroid/animation/LayoutTransition;)V
    //   124: getstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedFieldFetched : Z
    //   127: ifne -> 162
    //   130: ldc android/view/ViewGroup
    //   132: ldc 'mLayoutSuppressed'
    //   134: invokevirtual getDeclaredField : (Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   137: putstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedField : Ljava/lang/reflect/Field;
    //   140: getstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedField : Ljava/lang/reflect/Field;
    //   143: iconst_1
    //   144: invokevirtual setAccessible : (Z)V
    //   147: goto -> 158
    //   150: ldc 'ViewGroupUtilsApi14'
    //   152: ldc 'Failed to access mLayoutSuppressed field by reflection'
    //   154: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
    //   157: pop
    //   158: iconst_1
    //   159: putstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedFieldFetched : Z
    //   162: iload #4
    //   164: istore_2
    //   165: getstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedField : Ljava/lang/reflect/Field;
    //   168: ifnull -> 208
    //   171: getstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedField : Ljava/lang/reflect/Field;
    //   174: aload_1
    //   175: invokevirtual getBoolean : (Ljava/lang/Object;)Z
    //   178: istore_2
    //   179: iload_2
    //   180: ifeq -> 197
    //   183: getstatic android/support/transition/ViewGroupUtilsApi14.sLayoutSuppressedField : Ljava/lang/reflect/Field;
    //   186: aload_1
    //   187: iconst_0
    //   188: invokevirtual setBoolean : (Ljava/lang/Object;Z)V
    //   191: goto -> 197
    //   194: goto -> 200
    //   197: goto -> 208
    //   200: ldc 'ViewGroupUtilsApi14'
    //   202: ldc 'Failed to get mLayoutSuppressed field by reflection'
    //   204: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
    //   207: pop
    //   208: iload_2
    //   209: ifeq -> 216
    //   212: aload_1
    //   213: invokevirtual requestLayout : ()V
    //   216: aload_1
    //   217: getstatic android/support/transition/R$id.transition_layout_save : I
    //   220: invokevirtual getTag : (I)Ljava/lang/Object;
    //   223: checkcast android/animation/LayoutTransition
    //   226: astore #5
    //   228: aload #5
    //   230: ifnull -> 247
    //   233: aload_1
    //   234: getstatic android/support/transition/R$id.transition_layout_save : I
    //   237: aconst_null
    //   238: invokevirtual setTag : (ILjava/lang/Object;)V
    //   241: aload_1
    //   242: aload #5
    //   244: invokevirtual setLayoutTransition : (Landroid/animation/LayoutTransition;)V
    //   247: return
    //   248: astore #5
    //   250: goto -> 150
    //   253: astore #5
    //   255: iload_3
    //   256: istore_2
    //   257: goto -> 200
    //   260: astore #5
    //   262: goto -> 194
    // Exception table:
    //   from	to	target	type
    //   130	147	248	java/lang/NoSuchFieldException
    //   171	179	253	java/lang/IllegalAccessException
    //   183	191	260	java/lang/IllegalAccessException }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewGroupUtilsApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */