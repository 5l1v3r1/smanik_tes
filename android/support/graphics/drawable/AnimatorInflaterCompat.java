package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.AnimatorRes;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.support.v4.graphics.PathParser.PathDataNode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class AnimatorInflaterCompat {
  private static final boolean DBG_ANIMATOR_INFLATER = false;
  
  private static final int MAX_NUM_POINTS = 100;
  
  private static final String TAG = "AnimatorInflater";
  
  private static final int TOGETHER = 0;
  
  private static final int VALUE_TYPE_COLOR = 3;
  
  private static final int VALUE_TYPE_FLOAT = 0;
  
  private static final int VALUE_TYPE_INT = 1;
  
  private static final int VALUE_TYPE_PATH = 2;
  
  private static final int VALUE_TYPE_UNDEFINED = 4;
  
  private static Animator createAnimatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, float paramFloat) throws XmlPullParserException, IOException { return createAnimatorFromXml(paramContext, paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser), null, 0, paramFloat); }
  
  private static Animator createAnimatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, AnimatorSet paramAnimatorSet, int paramInt, float paramFloat) throws XmlPullParserException, IOException {
    int i = paramXmlPullParser.getDepth();
    TypedArray typedArray = null;
    ArrayList arrayList = null;
    while (true) {
      int k = paramXmlPullParser.next();
      int j = 0;
      if ((k != 3 || paramXmlPullParser.getDepth() > i) && k != 1) {
        if (k != 2)
          continue; 
        TypedArray typedArray1 = paramXmlPullParser.getName();
        if (typedArray1.equals("objectAnimator")) {
          typedArray1 = loadObjectAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, paramFloat, paramXmlPullParser);
        } else {
          ValueAnimator valueAnimator;
          if (typedArray1.equals("animator")) {
            valueAnimator = loadAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, null, paramFloat, paramXmlPullParser);
          } else {
            AnimatorSet animatorSet;
            if (valueAnimator.equals("set")) {
              animatorSet = new AnimatorSet();
              typedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_ANIMATOR_SET);
              j = TypedArrayUtils.getNamedInt(typedArray, paramXmlPullParser, "ordering", 0, 0);
              createAnimatorFromXml(paramContext, paramResources, paramTheme, paramXmlPullParser, paramAttributeSet, (AnimatorSet)animatorSet, j, paramFloat);
              typedArray.recycle();
              j = 0;
            } else if (animatorSet.equals("propertyValuesHolder")) {
              PropertyValuesHolder[] arrayOfPropertyValuesHolder = loadValues(paramContext, paramResources, paramTheme, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser));
              if (arrayOfPropertyValuesHolder != null && typedArray != null && typedArray instanceof ValueAnimator)
                ((ValueAnimator)typedArray).setValues(arrayOfPropertyValuesHolder); 
              j = 1;
              typedArray1 = typedArray;
            } else {
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("Unknown animator name: ");
              stringBuilder.append(paramXmlPullParser.getName());
              throw new RuntimeException(stringBuilder.toString());
            } 
          } 
        } 
        typedArray = typedArray1;
        if (paramAnimatorSet != null) {
          typedArray = typedArray1;
          if (j == 0) {
            ArrayList arrayList1 = arrayList;
            if (arrayList == null)
              arrayList1 = new ArrayList(); 
            arrayList1.add(typedArray1);
            typedArray = typedArray1;
            arrayList = arrayList1;
          } 
        } 
        continue;
      } 
      break;
    } 
    if (paramAnimatorSet != null && arrayList != null) {
      Animator[] arrayOfAnimator = new Animator[arrayList.size()];
      Iterator iterator = arrayList.iterator();
      byte b;
      for (b = 0; iterator.hasNext(); b++)
        arrayOfAnimator[b] = (Animator)iterator.next(); 
      if (paramInt == 0) {
        paramAnimatorSet.playTogether(arrayOfAnimator);
        return typedArray;
      } 
      paramAnimatorSet.playSequentially(arrayOfAnimator);
    } 
    return typedArray;
  }
  
  private static Keyframe createNewKeyframe(Keyframe paramKeyframe, float paramFloat) { return (paramKeyframe.getType() == float.class) ? Keyframe.ofFloat(paramFloat) : ((paramKeyframe.getType() == int.class) ? Keyframe.ofInt(paramFloat) : Keyframe.ofObject(paramFloat)); }
  
  private static void distributeKeyframes(Keyframe[] paramArrayOfKeyframe, float paramFloat, int paramInt1, int paramInt2) {
    paramFloat /= (paramInt2 - paramInt1 + 2);
    while (paramInt1 <= paramInt2) {
      paramArrayOfKeyframe[paramInt1].setFraction(paramArrayOfKeyframe[paramInt1 - 1].getFraction() + paramFloat);
      paramInt1++;
    } 
  }
  
  private static void dumpKeyframes(Object[] paramArrayOfObject, String paramString) {
    if (paramArrayOfObject != null) {
      if (paramArrayOfObject.length == 0)
        return; 
      Log.d("AnimatorInflater", paramString);
      int i = paramArrayOfObject.length;
      for (byte b = 0; b < i; b++) {
        String str;
        Keyframe keyframe = (Keyframe)paramArrayOfObject[b];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Keyframe ");
        stringBuilder.append(b);
        stringBuilder.append(": fraction ");
        if (keyframe.getFraction() < 0.0F) {
          paramString = "null";
        } else {
          str = Float.valueOf(keyframe.getFraction());
        } 
        stringBuilder.append(str);
        stringBuilder.append(", ");
        stringBuilder.append(", value : ");
        if (keyframe.hasValue()) {
          Object object = keyframe.getValue();
        } else {
          str = "null";
        } 
        stringBuilder.append(str);
        Log.d("AnimatorInflater", stringBuilder.toString());
      } 
      return;
    } 
  }
  
  private static PropertyValuesHolder getPVH(TypedArray paramTypedArray, int paramInt1, int paramInt2, int paramInt3, String paramString) { // Byte code:
    //   0: aload_0
    //   1: iload_2
    //   2: invokevirtual peekValue : (I)Landroid/util/TypedValue;
    //   5: astore #12
    //   7: aload #12
    //   9: ifnull -> 18
    //   12: iconst_1
    //   13: istore #8
    //   15: goto -> 21
    //   18: iconst_0
    //   19: istore #8
    //   21: iload #8
    //   23: ifeq -> 36
    //   26: aload #12
    //   28: getfield type : I
    //   31: istore #10
    //   33: goto -> 39
    //   36: iconst_0
    //   37: istore #10
    //   39: aload_0
    //   40: iload_3
    //   41: invokevirtual peekValue : (I)Landroid/util/TypedValue;
    //   44: astore #12
    //   46: aload #12
    //   48: ifnull -> 57
    //   51: iconst_1
    //   52: istore #9
    //   54: goto -> 60
    //   57: iconst_0
    //   58: istore #9
    //   60: iload #9
    //   62: ifeq -> 75
    //   65: aload #12
    //   67: getfield type : I
    //   70: istore #11
    //   72: goto -> 78
    //   75: iconst_0
    //   76: istore #11
    //   78: iload_1
    //   79: istore #7
    //   81: iload_1
    //   82: iconst_4
    //   83: if_icmpne -> 121
    //   86: iload #8
    //   88: ifeq -> 99
    //   91: iload #10
    //   93: invokestatic isColorType : (I)Z
    //   96: ifne -> 112
    //   99: iload #9
    //   101: ifeq -> 118
    //   104: iload #11
    //   106: invokestatic isColorType : (I)Z
    //   109: ifeq -> 118
    //   112: iconst_3
    //   113: istore #7
    //   115: goto -> 121
    //   118: iconst_0
    //   119: istore #7
    //   121: iload #7
    //   123: ifne -> 131
    //   126: iconst_1
    //   127: istore_1
    //   128: goto -> 133
    //   131: iconst_0
    //   132: istore_1
    //   133: aconst_null
    //   134: astore #12
    //   136: aconst_null
    //   137: astore #14
    //   139: iload #7
    //   141: iconst_2
    //   142: if_icmpne -> 338
    //   145: aload_0
    //   146: iload_2
    //   147: invokevirtual getString : (I)Ljava/lang/String;
    //   150: astore #13
    //   152: aload_0
    //   153: iload_3
    //   154: invokevirtual getString : (I)Ljava/lang/String;
    //   157: astore #14
    //   159: aload #13
    //   161: invokestatic createNodesFromPathData : (Ljava/lang/String;)[Landroid/support/v4/graphics/PathParser$PathDataNode;
    //   164: astore #15
    //   166: aload #14
    //   168: invokestatic createNodesFromPathData : (Ljava/lang/String;)[Landroid/support/v4/graphics/PathParser$PathDataNode;
    //   171: astore #16
    //   173: aload #15
    //   175: ifnonnull -> 186
    //   178: aload #12
    //   180: astore_0
    //   181: aload #16
    //   183: ifnull -> 726
    //   186: aload #15
    //   188: ifnull -> 307
    //   191: new android/support/graphics/drawable/AnimatorInflaterCompat$PathDataEvaluator
    //   194: dup
    //   195: aconst_null
    //   196: invokespecial <init> : (Landroid/support/graphics/drawable/AnimatorInflaterCompat$1;)V
    //   199: astore_0
    //   200: aload #16
    //   202: ifnull -> 289
    //   205: aload #15
    //   207: aload #16
    //   209: invokestatic canMorph : ([Landroid/support/v4/graphics/PathParser$PathDataNode;[Landroid/support/v4/graphics/PathParser$PathDataNode;)Z
    //   212: ifne -> 265
    //   215: new java/lang/StringBuilder
    //   218: dup
    //   219: invokespecial <init> : ()V
    //   222: astore_0
    //   223: aload_0
    //   224: ldc_w ' Can't morph from '
    //   227: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   230: pop
    //   231: aload_0
    //   232: aload #13
    //   234: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: pop
    //   238: aload_0
    //   239: ldc_w ' to '
    //   242: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: pop
    //   246: aload_0
    //   247: aload #14
    //   249: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: new android/view/InflateException
    //   256: dup
    //   257: aload_0
    //   258: invokevirtual toString : ()Ljava/lang/String;
    //   261: invokespecial <init> : (Ljava/lang/String;)V
    //   264: athrow
    //   265: aload #4
    //   267: aload_0
    //   268: iconst_2
    //   269: anewarray java/lang/Object
    //   272: dup
    //   273: iconst_0
    //   274: aload #15
    //   276: aastore
    //   277: dup
    //   278: iconst_1
    //   279: aload #16
    //   281: aastore
    //   282: invokestatic ofObject : (Ljava/lang/String;Landroid/animation/TypeEvaluator;[Ljava/lang/Object;)Landroid/animation/PropertyValuesHolder;
    //   285: astore_0
    //   286: goto -> 305
    //   289: aload #4
    //   291: aload_0
    //   292: iconst_1
    //   293: anewarray java/lang/Object
    //   296: dup
    //   297: iconst_0
    //   298: aload #15
    //   300: aastore
    //   301: invokestatic ofObject : (Ljava/lang/String;Landroid/animation/TypeEvaluator;[Ljava/lang/Object;)Landroid/animation/PropertyValuesHolder;
    //   304: astore_0
    //   305: aload_0
    //   306: areturn
    //   307: aload #12
    //   309: astore_0
    //   310: aload #16
    //   312: ifnull -> 726
    //   315: aload #4
    //   317: new android/support/graphics/drawable/AnimatorInflaterCompat$PathDataEvaluator
    //   320: dup
    //   321: aconst_null
    //   322: invokespecial <init> : (Landroid/support/graphics/drawable/AnimatorInflaterCompat$1;)V
    //   325: iconst_1
    //   326: anewarray java/lang/Object
    //   329: dup
    //   330: iconst_0
    //   331: aload #16
    //   333: aastore
    //   334: invokestatic ofObject : (Ljava/lang/String;Landroid/animation/TypeEvaluator;[Ljava/lang/Object;)Landroid/animation/PropertyValuesHolder;
    //   337: areturn
    //   338: iload #7
    //   340: iconst_3
    //   341: if_icmpne -> 352
    //   344: invokestatic getInstance : ()Landroid/support/graphics/drawable/ArgbEvaluator;
    //   347: astore #13
    //   349: goto -> 355
    //   352: aconst_null
    //   353: astore #13
    //   355: iload_1
    //   356: ifeq -> 503
    //   359: iload #8
    //   361: ifeq -> 458
    //   364: iload #10
    //   366: iconst_5
    //   367: if_icmpne -> 381
    //   370: aload_0
    //   371: iload_2
    //   372: fconst_0
    //   373: invokevirtual getDimension : (IF)F
    //   376: fstore #5
    //   378: goto -> 389
    //   381: aload_0
    //   382: iload_2
    //   383: fconst_0
    //   384: invokevirtual getFloat : (IF)F
    //   387: fstore #5
    //   389: iload #9
    //   391: ifeq -> 441
    //   394: iload #11
    //   396: iconst_5
    //   397: if_icmpne -> 411
    //   400: aload_0
    //   401: iload_3
    //   402: fconst_0
    //   403: invokevirtual getDimension : (IF)F
    //   406: fstore #6
    //   408: goto -> 419
    //   411: aload_0
    //   412: iload_3
    //   413: fconst_0
    //   414: invokevirtual getFloat : (IF)F
    //   417: fstore #6
    //   419: aload #4
    //   421: iconst_2
    //   422: newarray float
    //   424: dup
    //   425: iconst_0
    //   426: fload #5
    //   428: fastore
    //   429: dup
    //   430: iconst_1
    //   431: fload #6
    //   433: fastore
    //   434: invokestatic ofFloat : (Ljava/lang/String;[F)Landroid/animation/PropertyValuesHolder;
    //   437: astore_0
    //   438: goto -> 497
    //   441: aload #4
    //   443: iconst_1
    //   444: newarray float
    //   446: dup
    //   447: iconst_0
    //   448: fload #5
    //   450: fastore
    //   451: invokestatic ofFloat : (Ljava/lang/String;[F)Landroid/animation/PropertyValuesHolder;
    //   454: astore_0
    //   455: goto -> 497
    //   458: iload #11
    //   460: iconst_5
    //   461: if_icmpne -> 475
    //   464: aload_0
    //   465: iload_3
    //   466: fconst_0
    //   467: invokevirtual getDimension : (IF)F
    //   470: fstore #5
    //   472: goto -> 483
    //   475: aload_0
    //   476: iload_3
    //   477: fconst_0
    //   478: invokevirtual getFloat : (IF)F
    //   481: fstore #5
    //   483: aload #4
    //   485: iconst_1
    //   486: newarray float
    //   488: dup
    //   489: iconst_0
    //   490: fload #5
    //   492: fastore
    //   493: invokestatic ofFloat : (Ljava/lang/String;[F)Landroid/animation/PropertyValuesHolder;
    //   496: astore_0
    //   497: aload_0
    //   498: astore #12
    //   500: goto -> 700
    //   503: iload #8
    //   505: ifeq -> 635
    //   508: iload #10
    //   510: iconst_5
    //   511: if_icmpne -> 525
    //   514: aload_0
    //   515: iload_2
    //   516: fconst_0
    //   517: invokevirtual getDimension : (IF)F
    //   520: f2i
    //   521: istore_1
    //   522: goto -> 550
    //   525: iload #10
    //   527: invokestatic isColorType : (I)Z
    //   530: ifeq -> 543
    //   533: aload_0
    //   534: iload_2
    //   535: iconst_0
    //   536: invokevirtual getColor : (II)I
    //   539: istore_1
    //   540: goto -> 550
    //   543: aload_0
    //   544: iload_2
    //   545: iconst_0
    //   546: invokevirtual getInt : (II)I
    //   549: istore_1
    //   550: iload #9
    //   552: ifeq -> 618
    //   555: iload #11
    //   557: iconst_5
    //   558: if_icmpne -> 572
    //   561: aload_0
    //   562: iload_3
    //   563: fconst_0
    //   564: invokevirtual getDimension : (IF)F
    //   567: f2i
    //   568: istore_2
    //   569: goto -> 597
    //   572: iload #11
    //   574: invokestatic isColorType : (I)Z
    //   577: ifeq -> 590
    //   580: aload_0
    //   581: iload_3
    //   582: iconst_0
    //   583: invokevirtual getColor : (II)I
    //   586: istore_2
    //   587: goto -> 597
    //   590: aload_0
    //   591: iload_3
    //   592: iconst_0
    //   593: invokevirtual getInt : (II)I
    //   596: istore_2
    //   597: aload #4
    //   599: iconst_2
    //   600: newarray int
    //   602: dup
    //   603: iconst_0
    //   604: iload_1
    //   605: iastore
    //   606: dup
    //   607: iconst_1
    //   608: iload_2
    //   609: iastore
    //   610: invokestatic ofInt : (Ljava/lang/String;[I)Landroid/animation/PropertyValuesHolder;
    //   613: astore #12
    //   615: goto -> 700
    //   618: aload #4
    //   620: iconst_1
    //   621: newarray int
    //   623: dup
    //   624: iconst_0
    //   625: iload_1
    //   626: iastore
    //   627: invokestatic ofInt : (Ljava/lang/String;[I)Landroid/animation/PropertyValuesHolder;
    //   630: astore #12
    //   632: goto -> 700
    //   635: aload #14
    //   637: astore #12
    //   639: iload #9
    //   641: ifeq -> 700
    //   644: iload #11
    //   646: iconst_5
    //   647: if_icmpne -> 661
    //   650: aload_0
    //   651: iload_3
    //   652: fconst_0
    //   653: invokevirtual getDimension : (IF)F
    //   656: f2i
    //   657: istore_1
    //   658: goto -> 686
    //   661: iload #11
    //   663: invokestatic isColorType : (I)Z
    //   666: ifeq -> 679
    //   669: aload_0
    //   670: iload_3
    //   671: iconst_0
    //   672: invokevirtual getColor : (II)I
    //   675: istore_1
    //   676: goto -> 686
    //   679: aload_0
    //   680: iload_3
    //   681: iconst_0
    //   682: invokevirtual getInt : (II)I
    //   685: istore_1
    //   686: aload #4
    //   688: iconst_1
    //   689: newarray int
    //   691: dup
    //   692: iconst_0
    //   693: iload_1
    //   694: iastore
    //   695: invokestatic ofInt : (Ljava/lang/String;[I)Landroid/animation/PropertyValuesHolder;
    //   698: astore #12
    //   700: aload #12
    //   702: astore_0
    //   703: aload #12
    //   705: ifnull -> 726
    //   708: aload #12
    //   710: astore_0
    //   711: aload #13
    //   713: ifnull -> 726
    //   716: aload #12
    //   718: aload #13
    //   720: invokevirtual setEvaluator : (Landroid/animation/TypeEvaluator;)V
    //   723: aload #12
    //   725: astore_0
    //   726: aload_0
    //   727: areturn }
  
  private static int inferValueTypeFromValues(TypedArray paramTypedArray, int paramInt1, int paramInt2) {
    byte b;
    TypedValue typedValue2 = paramTypedArray.peekValue(paramInt1);
    int i = 1;
    int j = 0;
    if (typedValue2 != null) {
      paramInt1 = 1;
    } else {
      paramInt1 = 0;
    } 
    if (paramInt1 != 0) {
      b = typedValue2.type;
    } else {
      b = 0;
    } 
    TypedValue typedValue1 = paramTypedArray.peekValue(paramInt2);
    if (typedValue1 != null) {
      paramInt2 = i;
    } else {
      paramInt2 = 0;
    } 
    if (paramInt2 != 0) {
      i = typedValue1.type;
    } else {
      i = 0;
    } 
    if (paramInt1 == 0 || !isColorType(b)) {
      paramInt1 = j;
      if (paramInt2 != 0) {
        paramInt1 = j;
        if (isColorType(i))
          return 3; 
      } 
      return paramInt1;
    } 
    return 3;
  }
  
  private static int inferValueTypeOfKeyframe(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser) {
    boolean bool;
    TypedArray typedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_KEYFRAME);
    byte b2 = 0;
    TypedValue typedValue = TypedArrayUtils.peekNamedValue(typedArray, paramXmlPullParser, "value", 0);
    if (typedValue != null) {
      bool = true;
    } else {
      bool = false;
    } 
    byte b1 = b2;
    if (bool) {
      b1 = b2;
      if (isColorType(typedValue.type))
        b1 = 3; 
    } 
    typedArray.recycle();
    return b1;
  }
  
  private static boolean isColorType(int paramInt) { return (paramInt >= 28 && paramInt <= 31); }
  
  public static Animator loadAnimator(Context paramContext, @AnimatorRes int paramInt) throws Resources.NotFoundException { return (Build.VERSION.SDK_INT >= 24) ? AnimatorInflater.loadAnimator(paramContext, paramInt) : loadAnimator(paramContext, paramContext.getResources(), paramContext.getTheme(), paramInt); }
  
  public static Animator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, @AnimatorRes int paramInt) throws Resources.NotFoundException { return loadAnimator(paramContext, paramResources, paramTheme, paramInt, 1.0F); }
  
  public static Animator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, @AnimatorRes int paramInt, float paramFloat) throws Resources.NotFoundException {
    Context context2 = null;
    context3 = null;
    Context context1 = null;
    try {
      xmlResourceParser = paramResources.getAnimation(paramInt);
    } catch (XmlPullParserException paramResources) {
    
    } catch (IOException paramResources) {
      paramContext = context2;
      context1 = paramContext;
      StringBuilder stringBuilder1 = new StringBuilder();
      context1 = paramContext;
      stringBuilder1.append("Can't load animation resource ID #0x");
      context1 = paramContext;
      stringBuilder1.append(Integer.toHexString(paramInt));
      context1 = paramContext;
      Resources.NotFoundException notFoundException1 = new Resources.NotFoundException(stringBuilder1.toString());
      context1 = paramContext;
      notFoundException1.initCause(paramResources);
      context1 = paramContext;
      throw notFoundException1;
    } finally {
      if (context1 != null)
        context1.close(); 
    } 
    context1 = paramContext;
    StringBuilder stringBuilder = new StringBuilder();
    context1 = paramContext;
    stringBuilder.append("Can't load animation resource ID #0x");
    context1 = paramContext;
    stringBuilder.append(Integer.toHexString(paramInt));
    context1 = paramContext;
    Resources.NotFoundException notFoundException = new Resources.NotFoundException(stringBuilder.toString());
    context1 = paramContext;
    notFoundException.initCause(paramResources);
    context1 = paramContext;
    throw notFoundException;
  }
  
  private static ValueAnimator loadAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, ValueAnimator paramValueAnimator, float paramFloat, XmlPullParser paramXmlPullParser) throws Resources.NotFoundException {
    TypedArray typedArray2 = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_ANIMATOR);
    TypedArray typedArray1 = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
    ValueAnimator valueAnimator = paramValueAnimator;
    if (paramValueAnimator == null)
      valueAnimator = new ValueAnimator(); 
    parseAnimatorFromTypeArray(valueAnimator, typedArray2, typedArray1, paramFloat, paramXmlPullParser);
    int i = TypedArrayUtils.getNamedResourceId(typedArray2, paramXmlPullParser, "interpolator", 0, 0);
    if (i > 0)
      valueAnimator.setInterpolator(AnimationUtilsCompat.loadInterpolator(paramContext, i)); 
    typedArray2.recycle();
    if (typedArray1 != null)
      typedArray1.recycle(); 
    return valueAnimator;
  }
  
  private static Keyframe loadKeyframe(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, int paramInt, XmlPullParser paramXmlPullParser) throws XmlPullParserException, IOException {
    boolean bool;
    TypedArray typedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_KEYFRAME);
    float f = TypedArrayUtils.getNamedFloat(typedArray, paramXmlPullParser, "fraction", 3, -1.0F);
    Keyframe keyframe = TypedArrayUtils.peekNamedValue(typedArray, paramXmlPullParser, "value", 0);
    if (keyframe != null) {
      bool = true;
    } else {
      bool = false;
    } 
    int i = paramInt;
    if (paramInt == 4)
      if (bool && isColorType(keyframe.type)) {
        i = 3;
      } else {
        i = 0;
      }  
    if (bool) {
      if (i != 3) {
        Keyframe keyframe1;
        switch (i) {
          default:
            keyframe = null;
            break;
          case 0:
            keyframe1 = Keyframe.ofFloat(f, TypedArrayUtils.getNamedFloat(typedArray, paramXmlPullParser, "value", 0, 0.0F));
            break;
          case 1:
            keyframe1 = Keyframe.ofInt(f, TypedArrayUtils.getNamedInt(typedArray, paramXmlPullParser, "value", 0, 0));
            break;
        } 
      } else {
      
      } 
    } else if (i == 0) {
      Keyframe keyframe1 = Keyframe.ofFloat(f);
    } else {
      keyframe = Keyframe.ofInt(f);
    } 
    paramInt = TypedArrayUtils.getNamedResourceId(typedArray, paramXmlPullParser, "interpolator", 1, 0);
    if (paramInt > 0)
      keyframe.setInterpolator(AnimationUtilsCompat.loadInterpolator(paramContext, paramInt)); 
    typedArray.recycle();
    return keyframe;
  }
  
  private static ObjectAnimator loadObjectAnimator(Context paramContext, Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, float paramFloat, XmlPullParser paramXmlPullParser) throws Resources.NotFoundException {
    ObjectAnimator objectAnimator = new ObjectAnimator();
    loadAnimator(paramContext, paramResources, paramTheme, paramAttributeSet, objectAnimator, paramFloat, paramXmlPullParser);
    return objectAnimator;
  }
  
  private static PropertyValuesHolder loadPvh(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, String paramString, int paramInt) throws XmlPullParserException, IOException {
    PropertyValuesHolder propertyValuesHolder;
    Context context = null;
    ArrayList arrayList = null;
    int i = paramInt;
    while (true) {
      paramInt = paramXmlPullParser.next();
      if (paramInt != 3 && paramInt != 1) {
        if (paramXmlPullParser.getName().equals("keyframe")) {
          paramInt = i;
          if (i == 4)
            paramInt = inferValueTypeOfKeyframe(paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser), paramXmlPullParser); 
          Keyframe keyframe = loadKeyframe(paramContext, paramResources, paramTheme, Xml.asAttributeSet(paramXmlPullParser), paramInt, paramXmlPullParser);
          ArrayList arrayList1 = arrayList;
          if (keyframe != null) {
            arrayList1 = arrayList;
            if (arrayList == null)
              arrayList1 = new ArrayList(); 
            arrayList1.add(keyframe);
          } 
          paramXmlPullParser.next();
          i = paramInt;
          arrayList = arrayList1;
        } 
        continue;
      } 
      break;
    } 
    paramContext = context;
    if (arrayList != null) {
      int j = arrayList.size();
      paramContext = context;
      if (j > 0) {
        int k = 0;
        Keyframe keyframe1 = (Keyframe)arrayList.get(0);
        Keyframe keyframe2 = (Keyframe)arrayList.get(j - 1);
        float f = keyframe2.getFraction();
        paramInt = j;
        if (f < 1.0F)
          if (f < 0.0F) {
            keyframe2.setFraction(1.0F);
            paramInt = j;
          } else {
            arrayList.add(arrayList.size(), createNewKeyframe(keyframe2, 1.0F));
            paramInt = j + 1;
          }  
        f = keyframe1.getFraction();
        j = paramInt;
        if (f != 0.0F)
          if (f < 0.0F) {
            keyframe1.setFraction(0.0F);
            j = paramInt;
          } else {
            arrayList.add(0, createNewKeyframe(keyframe1, 0.0F));
            j = paramInt + 1;
          }  
        Keyframe[] arrayOfKeyframe = new Keyframe[j];
        arrayList.toArray(arrayOfKeyframe);
        for (paramInt = k; paramInt < j; paramInt++) {
          keyframe2 = arrayOfKeyframe[paramInt];
          if (keyframe2.getFraction() < 0.0F)
            if (paramInt == 0) {
              keyframe2.setFraction(0.0F);
            } else {
              int m = j - 1;
              if (paramInt == m) {
                keyframe2.setFraction(1.0F);
              } else {
                k = paramInt + 1;
                int n = paramInt;
                while (k < m && arrayOfKeyframe[k].getFraction() < 0.0F) {
                  n = k;
                  k++;
                } 
                distributeKeyframes(arrayOfKeyframe, arrayOfKeyframe[n + 1].getFraction() - arrayOfKeyframe[paramInt - 1].getFraction(), paramInt, n);
              } 
            }  
        } 
        PropertyValuesHolder propertyValuesHolder1 = PropertyValuesHolder.ofKeyframe(paramString, arrayOfKeyframe);
        propertyValuesHolder = propertyValuesHolder1;
        if (i == 3) {
          propertyValuesHolder1.setEvaluator(ArgbEvaluator.getInstance());
          propertyValuesHolder = propertyValuesHolder1;
        } 
      } 
    } 
    return propertyValuesHolder;
  }
  
  private static PropertyValuesHolder[] loadValues(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet) throws XmlPullParserException, IOException {
    int i;
    PropertyValuesHolder[] arrayOfPropertyValuesHolder;
    Context context = null;
    ArrayList arrayList = null;
    while (true) {
      int j = paramXmlPullParser.getEventType();
      i = 0;
      if (j != 3 && j != 1) {
        if (j != 2) {
          paramXmlPullParser.next();
          continue;
        } 
        if (paramXmlPullParser.getName().equals("propertyValuesHolder")) {
          TypedArray typedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
          String str = TypedArrayUtils.getNamedString(typedArray, paramXmlPullParser, "propertyName", 3);
          i = TypedArrayUtils.getNamedInt(typedArray, paramXmlPullParser, "valueType", 2, 4);
          ArrayList arrayList1 = loadPvh(paramContext, paramResources, paramTheme, paramXmlPullParser, str, i);
          PropertyValuesHolder propertyValuesHolder = arrayList1;
          if (arrayList1 == null)
            propertyValuesHolder = getPVH(typedArray, i, 0, 1, str); 
          arrayList1 = arrayList;
          if (propertyValuesHolder != null) {
            arrayList1 = arrayList;
            if (arrayList == null)
              arrayList1 = new ArrayList(); 
            arrayList1.add(propertyValuesHolder);
          } 
          typedArray.recycle();
          arrayList = arrayList1;
        } 
        paramXmlPullParser.next();
        continue;
      } 
      break;
    } 
    paramContext = context;
    if (arrayList != null) {
      int j = arrayList.size();
      PropertyValuesHolder[] arrayOfPropertyValuesHolder1 = new PropertyValuesHolder[j];
      while (true) {
        arrayOfPropertyValuesHolder = arrayOfPropertyValuesHolder1;
        if (i < j) {
          arrayOfPropertyValuesHolder1[i] = (PropertyValuesHolder)arrayList.get(i);
          i++;
          continue;
        } 
        break;
      } 
    } 
    return arrayOfPropertyValuesHolder;
  }
  
  private static void parseAnimatorFromTypeArray(ValueAnimator paramValueAnimator, TypedArray paramTypedArray1, TypedArray paramTypedArray2, float paramFloat, XmlPullParser paramXmlPullParser) {
    long l1 = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "duration", 1, 300);
    long l2 = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "startOffset", 2, 0);
    int i = TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "valueType", 7, 4);
    int j = i;
    if (TypedArrayUtils.hasAttribute(paramXmlPullParser, "valueFrom")) {
      j = i;
      if (TypedArrayUtils.hasAttribute(paramXmlPullParser, "valueTo")) {
        int k = i;
        if (i == 4)
          k = inferValueTypeFromValues(paramTypedArray1, 5, 6); 
        PropertyValuesHolder propertyValuesHolder = getPVH(paramTypedArray1, k, 5, 6, "");
        j = k;
        if (propertyValuesHolder != null) {
          paramValueAnimator.setValues(new PropertyValuesHolder[] { propertyValuesHolder });
          j = k;
        } 
      } 
    } 
    paramValueAnimator.setDuration(l1);
    paramValueAnimator.setStartDelay(l2);
    paramValueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "repeatCount", 3, 0));
    paramValueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(paramTypedArray1, paramXmlPullParser, "repeatMode", 4, 1));
    if (paramTypedArray2 != null)
      setupObjectAnimator(paramValueAnimator, paramTypedArray2, j, paramFloat, paramXmlPullParser); 
  }
  
  private static void setupObjectAnimator(ValueAnimator paramValueAnimator, TypedArray paramTypedArray, int paramInt, float paramFloat, XmlPullParser paramXmlPullParser) { // Byte code:
    //   0: aload_0
    //   1: checkcast android/animation/ObjectAnimator
    //   4: astore_0
    //   5: aload_1
    //   6: aload #4
    //   8: ldc_w 'pathData'
    //   11: iconst_1
    //   12: invokestatic getNamedString : (Landroid/content/res/TypedArray;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)Ljava/lang/String;
    //   15: astore #5
    //   17: aload #5
    //   19: ifnull -> 117
    //   22: aload_1
    //   23: aload #4
    //   25: ldc_w 'propertyXName'
    //   28: iconst_2
    //   29: invokestatic getNamedString : (Landroid/content/res/TypedArray;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)Ljava/lang/String;
    //   32: astore #6
    //   34: aload_1
    //   35: aload #4
    //   37: ldc_w 'propertyYName'
    //   40: iconst_3
    //   41: invokestatic getNamedString : (Landroid/content/res/TypedArray;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)Ljava/lang/String;
    //   44: astore #4
    //   46: iload_2
    //   47: iconst_2
    //   48: if_icmpeq -> 51
    //   51: aload #6
    //   53: ifnonnull -> 98
    //   56: aload #4
    //   58: ifnonnull -> 98
    //   61: new java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial <init> : ()V
    //   68: astore_0
    //   69: aload_0
    //   70: aload_1
    //   71: invokevirtual getPositionDescription : ()Ljava/lang/String;
    //   74: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload_0
    //   79: ldc_w ' propertyXName or propertyYName is needed for PathData'
    //   82: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: pop
    //   86: new android/view/InflateException
    //   89: dup
    //   90: aload_0
    //   91: invokevirtual toString : ()Ljava/lang/String;
    //   94: invokespecial <init> : (Ljava/lang/String;)V
    //   97: athrow
    //   98: aload #5
    //   100: invokestatic createPathFromPathData : (Ljava/lang/String;)Landroid/graphics/Path;
    //   103: aload_0
    //   104: fload_3
    //   105: ldc_w 0.5
    //   108: fmul
    //   109: aload #6
    //   111: aload #4
    //   113: invokestatic setupPathMotion : (Landroid/graphics/Path;Landroid/animation/ObjectAnimator;FLjava/lang/String;Ljava/lang/String;)V
    //   116: return
    //   117: aload_0
    //   118: aload_1
    //   119: aload #4
    //   121: ldc_w 'propertyName'
    //   124: iconst_0
    //   125: invokestatic getNamedString : (Landroid/content/res/TypedArray;Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)Ljava/lang/String;
    //   128: invokevirtual setPropertyName : (Ljava/lang/String;)V
    //   131: return }
  
  private static void setupPathMotion(Path paramPath, ObjectAnimator paramObjectAnimator, float paramFloat, String paramString1, String paramString2) {
    float f2;
    PathMeasure pathMeasure2 = new PathMeasure(paramPath, false);
    ArrayList arrayList = new ArrayList();
    arrayList.add(Float.valueOf(0.0F));
    float f1 = 0.0F;
    do {
      f2 = f1 + pathMeasure2.getLength();
      arrayList.add(Float.valueOf(f2));
      f1 = f2;
    } while (pathMeasure2.nextContour());
    PathMeasure pathMeasure1 = new PathMeasure(paramPath, false);
    int i = Math.min(100, (int)(f2 / paramFloat) + 1);
    float[] arrayOfFloat2 = new float[i];
    float[] arrayOfFloat1 = new float[i];
    float[] arrayOfFloat3 = new float[2];
    f2 /= (i - 1);
    byte b1 = 0;
    paramFloat = 0.0F;
    byte b2 = 0;
    while (true) {
      pathMeasure2 = null;
      if (b1 < i) {
        pathMeasure1.getPosTan(paramFloat, arrayOfFloat3, null);
        arrayOfFloat2[b1] = arrayOfFloat3[0];
        arrayOfFloat1[b1] = arrayOfFloat3[1];
        f1 = paramFloat + f2;
        byte b4 = b2 + true;
        paramFloat = f1;
        byte b3 = b2;
        if (b4 < arrayList.size()) {
          paramFloat = f1;
          b3 = b2;
          if (f1 > ((Float)arrayList.get(b4)).floatValue()) {
            paramFloat = f1 - ((Float)arrayList.get(b4)).floatValue();
            pathMeasure1.nextContour();
            b3 = b4;
          } 
        } 
        b1++;
        b2 = b3;
        continue;
      } 
      break;
    } 
    if (paramString1 != null) {
      PropertyValuesHolder propertyValuesHolder1 = PropertyValuesHolder.ofFloat(paramString1, arrayOfFloat2);
    } else {
      pathMeasure1 = null;
    } 
    PropertyValuesHolder propertyValuesHolder = pathMeasure2;
    if (paramString2 != null)
      propertyValuesHolder = PropertyValuesHolder.ofFloat(paramString2, arrayOfFloat1); 
    if (pathMeasure1 == null) {
      paramObjectAnimator.setValues(new PropertyValuesHolder[] { propertyValuesHolder });
      return;
    } 
    if (propertyValuesHolder == null) {
      paramObjectAnimator.setValues(new PropertyValuesHolder[] { pathMeasure1 });
      return;
    } 
    paramObjectAnimator.setValues(new PropertyValuesHolder[] { pathMeasure1, propertyValuesHolder });
  }
  
  private static class PathDataEvaluator extends Object implements TypeEvaluator<PathParser.PathDataNode[]> {
    private PathParser.PathDataNode[] mNodeArray;
    
    private PathDataEvaluator() {}
    
    PathDataEvaluator(PathParser.PathDataNode[] param1ArrayOfPathDataNode) { this.mNodeArray = param1ArrayOfPathDataNode; }
    
    public PathParser.PathDataNode[] evaluate(float param1Float, PathParser.PathDataNode[] param1ArrayOfPathDataNode1, PathParser.PathDataNode[] param1ArrayOfPathDataNode2) {
      if (!PathParser.canMorph(param1ArrayOfPathDataNode1, param1ArrayOfPathDataNode2))
        throw new IllegalArgumentException("Can't interpolate between two incompatible pathData"); 
      if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, param1ArrayOfPathDataNode1))
        this.mNodeArray = PathParser.deepCopyNodes(param1ArrayOfPathDataNode1); 
      byte b;
      for (b = 0; b < param1ArrayOfPathDataNode1.length; b++)
        this.mNodeArray[b].interpolatePathDataNode(param1ArrayOfPathDataNode1[b], param1ArrayOfPathDataNode2[b], param1Float); 
      return this.mNodeArray;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/graphics/drawable/AnimatorInflaterCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */