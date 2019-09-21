package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class Transition implements Cloneable {
  static final boolean DBG = false;
  
  private static final int[] DEFAULT_MATCH_ORDER = { 2, 1, 3, 4 };
  
  private static final String LOG_TAG = "Transition";
  
  private static final int MATCH_FIRST = 1;
  
  public static final int MATCH_ID = 3;
  
  private static final String MATCH_ID_STR = "id";
  
  public static final int MATCH_INSTANCE = 1;
  
  private static final String MATCH_INSTANCE_STR = "instance";
  
  public static final int MATCH_ITEM_ID = 4;
  
  private static final String MATCH_ITEM_ID_STR = "itemId";
  
  private static final int MATCH_LAST = 4;
  
  public static final int MATCH_NAME = 2;
  
  private static final String MATCH_NAME_STR = "name";
  
  private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion() {
      public Path getPath(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
        Path path = new Path();
        path.moveTo(param1Float1, param1Float2);
        path.lineTo(param1Float3, param1Float4);
        return path;
      }
    };
  
  private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal();
  
  private ArrayList<Animator> mAnimators = new ArrayList();
  
  boolean mCanRemoveViews = false;
  
  private ArrayList<Animator> mCurrentAnimators = new ArrayList();
  
  long mDuration = -1L;
  
  private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
  
  private ArrayList<TransitionValues> mEndValuesList;
  
  private boolean mEnded = false;
  
  private EpicenterCallback mEpicenterCallback;
  
  private TimeInterpolator mInterpolator = null;
  
  private ArrayList<TransitionListener> mListeners = null;
  
  private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
  
  private String mName = getClass().getName();
  
  private ArrayMap<String, String> mNameOverrides;
  
  private int mNumInstances = 0;
  
  TransitionSet mParent = null;
  
  private PathMotion mPathMotion = STRAIGHT_PATH_MOTION;
  
  private boolean mPaused = false;
  
  TransitionPropagation mPropagation;
  
  private ViewGroup mSceneRoot = null;
  
  private long mStartDelay = -1L;
  
  private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
  
  private ArrayList<TransitionValues> mStartValuesList;
  
  private ArrayList<View> mTargetChildExcludes = null;
  
  private ArrayList<View> mTargetExcludes = null;
  
  private ArrayList<Integer> mTargetIdChildExcludes = null;
  
  private ArrayList<Integer> mTargetIdExcludes = null;
  
  ArrayList<Integer> mTargetIds = new ArrayList();
  
  private ArrayList<String> mTargetNameExcludes = null;
  
  private ArrayList<String> mTargetNames = null;
  
  private ArrayList<Class> mTargetTypeChildExcludes = null;
  
  private ArrayList<Class> mTargetTypeExcludes = null;
  
  private ArrayList<Class> mTargetTypes = null;
  
  ArrayList<View> mTargets = new ArrayList();
  
  public Transition() {}
  
  public Transition(Context paramContext, AttributeSet paramAttributeSet) {
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION);
    XmlResourceParser xmlResourceParser = (XmlResourceParser)paramAttributeSet;
    long l = TypedArrayUtils.getNamedInt(typedArray, xmlResourceParser, "duration", 1, -1);
    if (l >= 0L)
      setDuration(l); 
    l = TypedArrayUtils.getNamedInt(typedArray, xmlResourceParser, "startDelay", 2, -1);
    if (l > 0L)
      setStartDelay(l); 
    int i = TypedArrayUtils.getNamedResourceId(typedArray, xmlResourceParser, "interpolator", 0, 0);
    if (i > 0)
      setInterpolator(AnimationUtils.loadInterpolator(paramContext, i)); 
    String str = TypedArrayUtils.getNamedString(typedArray, xmlResourceParser, "matchOrder", 3);
    if (str != null)
      setMatchOrder(parseMatchOrder(str)); 
    typedArray.recycle();
  }
  
  private void addUnmatched(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2) {
    byte b2;
    boolean bool = false;
    byte b1 = 0;
    while (true) {
      b2 = bool;
      if (b1 < paramArrayMap1.size()) {
        TransitionValues transitionValues = (TransitionValues)paramArrayMap1.valueAt(b1);
        if (isValidTarget(transitionValues.view)) {
          this.mStartValuesList.add(transitionValues);
          this.mEndValuesList.add(null);
        } 
        b1++;
        continue;
      } 
      break;
    } 
    while (b2 < paramArrayMap2.size()) {
      TransitionValues transitionValues = (TransitionValues)paramArrayMap2.valueAt(b2);
      if (isValidTarget(transitionValues.view)) {
        this.mEndValuesList.add(transitionValues);
        this.mStartValuesList.add(null);
      } 
      b2++;
    } 
  }
  
  private static void addViewValues(TransitionValuesMaps paramTransitionValuesMaps, View paramView, TransitionValues paramTransitionValues) {
    paramTransitionValuesMaps.mViewValues.put(paramView, paramTransitionValues);
    int i = paramView.getId();
    if (i >= 0)
      if (paramTransitionValuesMaps.mIdValues.indexOfKey(i) >= 0) {
        paramTransitionValuesMaps.mIdValues.put(i, null);
      } else {
        paramTransitionValuesMaps.mIdValues.put(i, paramView);
      }  
    String str = ViewCompat.getTransitionName(paramView);
    if (str != null)
      if (paramTransitionValuesMaps.mNameValues.containsKey(str)) {
        paramTransitionValuesMaps.mNameValues.put(str, null);
      } else {
        paramTransitionValuesMaps.mNameValues.put(str, paramView);
      }  
    if (paramView.getParent() instanceof ListView) {
      ListView listView = (ListView)paramView.getParent();
      if (listView.getAdapter().hasStableIds()) {
        long l = listView.getItemIdAtPosition(listView.getPositionForView(paramView));
        if (paramTransitionValuesMaps.mItemIdValues.indexOfKey(l) >= 0) {
          paramView = (View)paramTransitionValuesMaps.mItemIdValues.get(l);
          if (paramView != null) {
            ViewCompat.setHasTransientState(paramView, false);
            paramTransitionValuesMaps.mItemIdValues.put(l, null);
            return;
          } 
        } else {
          ViewCompat.setHasTransientState(paramView, true);
          paramTransitionValuesMaps.mItemIdValues.put(l, paramView);
        } 
      } 
    } 
  }
  
  private static boolean alreadyContains(int[] paramArrayOfInt, int paramInt) {
    int i = paramArrayOfInt[paramInt];
    for (byte b = 0; b < paramInt; b++) {
      if (paramArrayOfInt[b] == i)
        return true; 
    } 
    return false;
  }
  
  private void captureHierarchy(View paramView, boolean paramBoolean) {
    if (paramView == null)
      return; 
    int i = paramView.getId();
    if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf(i)))
      return; 
    if (this.mTargetExcludes != null && this.mTargetExcludes.contains(paramView))
      return; 
    ArrayList arrayList = this.mTargetTypeExcludes;
    boolean bool = false;
    if (arrayList != null) {
      int j = this.mTargetTypeExcludes.size();
      for (byte b = 0; b < j; b++) {
        if (((Class)this.mTargetTypeExcludes.get(b)).isInstance(paramView))
          return; 
      } 
    } 
    if (paramView.getParent() instanceof ViewGroup) {
      TransitionValues transitionValues = new TransitionValues();
      transitionValues.view = paramView;
      if (paramBoolean) {
        captureStartValues(transitionValues);
      } else {
        captureEndValues(transitionValues);
      } 
      transitionValues.mTargetedTransitions.add(this);
      capturePropagationValues(transitionValues);
      if (paramBoolean) {
        addViewValues(this.mStartValues, paramView, transitionValues);
      } else {
        addViewValues(this.mEndValues, paramView, transitionValues);
      } 
    } 
    if (paramView instanceof ViewGroup) {
      if (this.mTargetIdChildExcludes != null && this.mTargetIdChildExcludes.contains(Integer.valueOf(i)))
        return; 
      if (this.mTargetChildExcludes != null && this.mTargetChildExcludes.contains(paramView))
        return; 
      if (this.mTargetTypeChildExcludes != null) {
        i = this.mTargetTypeChildExcludes.size();
        for (byte b1 = 0; b1 < i; b1++) {
          if (((Class)this.mTargetTypeChildExcludes.get(b1)).isInstance(paramView))
            return; 
        } 
      } 
      ViewGroup viewGroup = (ViewGroup)paramView;
      for (byte b = bool; b < viewGroup.getChildCount(); b++)
        captureHierarchy(viewGroup.getChildAt(b), paramBoolean); 
    } 
  }
  
  private ArrayList<Integer> excludeId(ArrayList<Integer> paramArrayList, int paramInt, boolean paramBoolean) {
    ArrayList<Integer> arrayList = paramArrayList;
    if (paramInt > 0) {
      if (paramBoolean)
        return ArrayListManager.add(paramArrayList, Integer.valueOf(paramInt)); 
      arrayList = ArrayListManager.remove(paramArrayList, Integer.valueOf(paramInt));
    } 
    return arrayList;
  }
  
  private static <T> ArrayList<T> excludeObject(ArrayList<T> paramArrayList, T paramT, boolean paramBoolean) {
    ArrayList<T> arrayList = paramArrayList;
    if (paramT != null) {
      if (paramBoolean)
        return ArrayListManager.add(paramArrayList, paramT); 
      arrayList = ArrayListManager.remove(paramArrayList, paramT);
    } 
    return arrayList;
  }
  
  private ArrayList<Class> excludeType(ArrayList<Class> paramArrayList, Class paramClass, boolean paramBoolean) {
    ArrayList<Class> arrayList = paramArrayList;
    if (paramClass != null) {
      if (paramBoolean)
        return ArrayListManager.add(paramArrayList, paramClass); 
      arrayList = ArrayListManager.remove(paramArrayList, paramClass);
    } 
    return arrayList;
  }
  
  private ArrayList<View> excludeView(ArrayList<View> paramArrayList, View paramView, boolean paramBoolean) {
    ArrayList<View> arrayList = paramArrayList;
    if (paramView != null) {
      if (paramBoolean)
        return ArrayListManager.add(paramArrayList, paramView); 
      arrayList = ArrayListManager.remove(paramArrayList, paramView);
    } 
    return arrayList;
  }
  
  private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
    ArrayMap arrayMap2 = (ArrayMap)sRunningAnimators.get();
    ArrayMap arrayMap1 = arrayMap2;
    if (arrayMap2 == null) {
      arrayMap1 = new ArrayMap();
      sRunningAnimators.set(arrayMap1);
    } 
    return arrayMap1;
  }
  
  private static boolean isValidMatch(int paramInt) { return (paramInt >= 1 && paramInt <= 4); }
  
  private static boolean isValueChanged(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2, String paramString) {
    Object object1 = paramTransitionValues1.values.get(paramString);
    Object object2 = paramTransitionValues2.values.get(paramString);
    boolean bool = true;
    if (object1 == null && object2 == null)
      return false; 
    if (object1 != null) {
      if (object2 == null)
        return true; 
      bool = true ^ object1.equals(object2);
    } 
    return bool;
  }
  
  private void matchIds(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, SparseArray<View> paramSparseArray1, SparseArray<View> paramSparseArray2) {
    int i = paramSparseArray1.size();
    byte b;
    for (b = 0; b < i; b++) {
      View view = (View)paramSparseArray1.valueAt(b);
      if (view != null && isValidTarget(view)) {
        View view1 = (View)paramSparseArray2.get(paramSparseArray1.keyAt(b));
        if (view1 != null && isValidTarget(view1)) {
          TransitionValues transitionValues1 = (TransitionValues)paramArrayMap1.get(view);
          TransitionValues transitionValues2 = (TransitionValues)paramArrayMap2.get(view1);
          if (transitionValues1 != null && transitionValues2 != null) {
            this.mStartValuesList.add(transitionValues1);
            this.mEndValuesList.add(transitionValues2);
            paramArrayMap1.remove(view);
            paramArrayMap2.remove(view1);
          } 
        } 
      } 
    } 
  }
  
  private void matchInstances(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2) {
    for (int i = paramArrayMap1.size() - 1; i >= 0; i--) {
      View view = (View)paramArrayMap1.keyAt(i);
      if (view != null && isValidTarget(view)) {
        TransitionValues transitionValues = (TransitionValues)paramArrayMap2.remove(view);
        if (transitionValues != null && transitionValues.view != null && isValidTarget(transitionValues.view)) {
          TransitionValues transitionValues1 = (TransitionValues)paramArrayMap1.removeAt(i);
          this.mStartValuesList.add(transitionValues1);
          this.mEndValuesList.add(transitionValues);
        } 
      } 
    } 
  }
  
  private void matchItemIds(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, LongSparseArray<View> paramLongSparseArray1, LongSparseArray<View> paramLongSparseArray2) {
    int i = paramLongSparseArray1.size();
    byte b;
    for (b = 0; b < i; b++) {
      View view = (View)paramLongSparseArray1.valueAt(b);
      if (view != null && isValidTarget(view)) {
        View view1 = (View)paramLongSparseArray2.get(paramLongSparseArray1.keyAt(b));
        if (view1 != null && isValidTarget(view1)) {
          TransitionValues transitionValues1 = (TransitionValues)paramArrayMap1.get(view);
          TransitionValues transitionValues2 = (TransitionValues)paramArrayMap2.get(view1);
          if (transitionValues1 != null && transitionValues2 != null) {
            this.mStartValuesList.add(transitionValues1);
            this.mEndValuesList.add(transitionValues2);
            paramArrayMap1.remove(view);
            paramArrayMap2.remove(view1);
          } 
        } 
      } 
    } 
  }
  
  private void matchNames(ArrayMap<View, TransitionValues> paramArrayMap1, ArrayMap<View, TransitionValues> paramArrayMap2, ArrayMap<String, View> paramArrayMap3, ArrayMap<String, View> paramArrayMap4) {
    int i = paramArrayMap3.size();
    byte b;
    for (b = 0; b < i; b++) {
      View view = (View)paramArrayMap3.valueAt(b);
      if (view != null && isValidTarget(view)) {
        View view1 = (View)paramArrayMap4.get(paramArrayMap3.keyAt(b));
        if (view1 != null && isValidTarget(view1)) {
          TransitionValues transitionValues1 = (TransitionValues)paramArrayMap1.get(view);
          TransitionValues transitionValues2 = (TransitionValues)paramArrayMap2.get(view1);
          if (transitionValues1 != null && transitionValues2 != null) {
            this.mStartValuesList.add(transitionValues1);
            this.mEndValuesList.add(transitionValues2);
            paramArrayMap1.remove(view);
            paramArrayMap2.remove(view1);
          } 
        } 
      } 
    } 
  }
  
  private void matchStartAndEnd(TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2) {
    ArrayMap arrayMap1 = new ArrayMap(paramTransitionValuesMaps1.mViewValues);
    ArrayMap arrayMap2 = new ArrayMap(paramTransitionValuesMaps2.mViewValues);
    for (byte b = 0; b < this.mMatchOrder.length; b++) {
      switch (this.mMatchOrder[b]) {
        case 4:
          matchItemIds(arrayMap1, arrayMap2, paramTransitionValuesMaps1.mItemIdValues, paramTransitionValuesMaps2.mItemIdValues);
          break;
        case 3:
          matchIds(arrayMap1, arrayMap2, paramTransitionValuesMaps1.mIdValues, paramTransitionValuesMaps2.mIdValues);
          break;
        case 2:
          matchNames(arrayMap1, arrayMap2, paramTransitionValuesMaps1.mNameValues, paramTransitionValuesMaps2.mNameValues);
          break;
        case 1:
          matchInstances(arrayMap1, arrayMap2);
          break;
      } 
    } 
    addUnmatched(arrayMap1, arrayMap2);
  }
  
  private static int[] parseMatchOrder(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    StringBuilder stringBuilder = new int[stringTokenizer.countTokens()];
    for (byte b = 0; stringTokenizer.hasMoreTokens(); b++) {
      String str = stringTokenizer.nextToken().trim();
      if ("id".equalsIgnoreCase(str)) {
        stringBuilder[b] = 3;
      } else if ("instance".equalsIgnoreCase(str)) {
        stringBuilder[b] = 1;
      } else if ("name".equalsIgnoreCase(str)) {
        stringBuilder[b] = 2;
      } else if ("itemId".equalsIgnoreCase(str)) {
        stringBuilder[b] = 4;
      } else {
        int[] arrayOfInt;
        if (str.isEmpty()) {
          arrayOfInt = new int[stringBuilder.length - 1];
          System.arraycopy(stringBuilder, 0, arrayOfInt, 0, b);
          b--;
          stringBuilder = arrayOfInt;
        } else {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Unknown match type in matchOrder: '");
          stringBuilder.append(arrayOfInt);
          stringBuilder.append("'");
          throw new InflateException(stringBuilder.toString());
        } 
      } 
    } 
    return stringBuilder;
  }
  
  private void runAnimator(Animator paramAnimator, final ArrayMap<Animator, AnimationInfo> runningAnimators) {
    if (paramAnimator != null) {
      paramAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) {
              runningAnimators.remove(param1Animator);
              Transition.this.mCurrentAnimators.remove(param1Animator);
            }
            
            public void onAnimationStart(Animator param1Animator) { Transition.this.mCurrentAnimators.add(param1Animator); }
          });
      animate(paramAnimator);
    } 
  }
  
  @NonNull
  public Transition addListener(@NonNull TransitionListener paramTransitionListener) {
    if (this.mListeners == null)
      this.mListeners = new ArrayList(); 
    this.mListeners.add(paramTransitionListener);
    return this;
  }
  
  @NonNull
  public Transition addTarget(@IdRes int paramInt) {
    if (paramInt != 0)
      this.mTargetIds.add(Integer.valueOf(paramInt)); 
    return this;
  }
  
  @NonNull
  public Transition addTarget(@NonNull View paramView) {
    this.mTargets.add(paramView);
    return this;
  }
  
  @NonNull
  public Transition addTarget(@NonNull Class paramClass) {
    if (this.mTargetTypes == null)
      this.mTargetTypes = new ArrayList(); 
    this.mTargetTypes.add(paramClass);
    return this;
  }
  
  @NonNull
  public Transition addTarget(@NonNull String paramString) {
    if (this.mTargetNames == null)
      this.mTargetNames = new ArrayList(); 
    this.mTargetNames.add(paramString);
    return this;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void animate(Animator paramAnimator) {
    if (paramAnimator == null) {
      end();
      return;
    } 
    if (getDuration() >= 0L)
      paramAnimator.setDuration(getDuration()); 
    if (getStartDelay() >= 0L)
      paramAnimator.setStartDelay(getStartDelay()); 
    if (getInterpolator() != null)
      paramAnimator.setInterpolator(getInterpolator()); 
    paramAnimator.addListener(new AnimatorListenerAdapter() {
          public void onAnimationEnd(Animator param1Animator) {
            Transition.this.end();
            param1Animator.removeListener(this);
          }
        });
    paramAnimator.start();
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void cancel() {
    int i;
    for (i = this.mCurrentAnimators.size() - 1; i >= 0; i--)
      ((Animator)this.mCurrentAnimators.get(i)).cancel(); 
    if (this.mListeners != null && this.mListeners.size() > 0) {
      ArrayList arrayList = (ArrayList)this.mListeners.clone();
      int j = arrayList.size();
      for (i = 0; i < j; i++)
        ((TransitionListener)arrayList.get(i)).onTransitionCancel(this); 
    } 
  }
  
  public abstract void captureEndValues(@NonNull TransitionValues paramTransitionValues);
  
  void capturePropagationValues(TransitionValues paramTransitionValues) {
    if (this.mPropagation != null && !paramTransitionValues.values.isEmpty()) {
      String[] arrayOfString = this.mPropagation.getPropagationProperties();
      if (arrayOfString == null)
        return; 
      byte b2 = 0;
      byte b1 = 0;
      while (true) {
        if (b1 < arrayOfString.length) {
          if (!paramTransitionValues.values.containsKey(arrayOfString[b1])) {
            b1 = b2;
            break;
          } 
          b1++;
          continue;
        } 
        b1 = 1;
        break;
      } 
      if (b1 == 0)
        this.mPropagation.captureValues(paramTransitionValues); 
    } 
  }
  
  public abstract void captureStartValues(@NonNull TransitionValues paramTransitionValues);
  
  void captureValues(ViewGroup paramViewGroup, boolean paramBoolean) {
    View view;
    clearValues(paramBoolean);
    int i = this.mTargetIds.size();
    boolean bool = false;
    if ((i > 0 || this.mTargets.size() > 0) && (this.mTargetNames == null || this.mTargetNames.isEmpty()) && (this.mTargetTypes == null || this.mTargetTypes.isEmpty())) {
      for (i = 0; i < this.mTargetIds.size(); i++) {
        View view1 = paramViewGroup.findViewById(((Integer)this.mTargetIds.get(i)).intValue());
        if (view1 != null) {
          TransitionValues transitionValues = new TransitionValues();
          transitionValues.view = view1;
          if (paramBoolean) {
            captureStartValues(transitionValues);
          } else {
            captureEndValues(transitionValues);
          } 
          transitionValues.mTargetedTransitions.add(this);
          capturePropagationValues(transitionValues);
          if (paramBoolean) {
            addViewValues(this.mStartValues, view1, transitionValues);
          } else {
            addViewValues(this.mEndValues, view1, transitionValues);
          } 
        } 
      } 
      for (i = 0; i < this.mTargets.size(); i++) {
        view = (View)this.mTargets.get(i);
        TransitionValues transitionValues = new TransitionValues();
        transitionValues.view = view;
        if (paramBoolean) {
          captureStartValues(transitionValues);
        } else {
          captureEndValues(transitionValues);
        } 
        transitionValues.mTargetedTransitions.add(this);
        capturePropagationValues(transitionValues);
        if (paramBoolean) {
          addViewValues(this.mStartValues, view, transitionValues);
        } else {
          addViewValues(this.mEndValues, view, transitionValues);
        } 
      } 
    } else {
      captureHierarchy(view, paramBoolean);
    } 
    if (!paramBoolean && this.mNameOverrides != null) {
      byte b;
      int j = this.mNameOverrides.size();
      ArrayList arrayList = new ArrayList(j);
      i = 0;
      while (true) {
        b = bool;
        if (i < j) {
          String str = (String)this.mNameOverrides.keyAt(i);
          arrayList.add(this.mStartValues.mNameValues.remove(str));
          i++;
          continue;
        } 
        break;
      } 
      while (b < j) {
        View view1 = (View)arrayList.get(b);
        if (view1 != null) {
          String str = (String)this.mNameOverrides.valueAt(b);
          this.mStartValues.mNameValues.put(str, view1);
        } 
        b++;
      } 
    } 
  }
  
  void clearValues(boolean paramBoolean) {
    if (paramBoolean) {
      this.mStartValues.mViewValues.clear();
      this.mStartValues.mIdValues.clear();
      this.mStartValues.mItemIdValues.clear();
      return;
    } 
    this.mEndValues.mViewValues.clear();
    this.mEndValues.mIdValues.clear();
    this.mEndValues.mItemIdValues.clear();
  }
  
  public Transition clone() {
    try {
      Transition transition = (Transition)super.clone();
      transition.mAnimators = new ArrayList();
      transition.mStartValues = new TransitionValuesMaps();
      transition.mEndValues = new TransitionValuesMaps();
      transition.mStartValuesList = null;
      transition.mEndValuesList = null;
      return transition;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  @Nullable
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, @Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2) { return null; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2, ArrayList<TransitionValues> paramArrayList1, ArrayList<TransitionValues> paramArrayList2) { // Byte code:
    //   0: invokestatic getRunningAnimators : ()Landroid/support/v4/util/ArrayMap;
    //   3: astore #20
    //   5: new android/util/SparseIntArray
    //   8: dup
    //   9: invokespecial <init> : ()V
    //   12: astore #19
    //   14: aload #4
    //   16: invokevirtual size : ()I
    //   19: istore #8
    //   21: ldc2_w 9223372036854775807
    //   24: lstore #10
    //   26: iconst_0
    //   27: istore #6
    //   29: iload #6
    //   31: iload #8
    //   33: if_icmpge -> 555
    //   36: aload #4
    //   38: iload #6
    //   40: invokevirtual get : (I)Ljava/lang/Object;
    //   43: checkcast android/support/transition/TransitionValues
    //   46: astore #14
    //   48: aload #5
    //   50: iload #6
    //   52: invokevirtual get : (I)Ljava/lang/Object;
    //   55: checkcast android/support/transition/TransitionValues
    //   58: astore_2
    //   59: aload #14
    //   61: astore #15
    //   63: aload #14
    //   65: ifnull -> 87
    //   68: aload #14
    //   70: astore #15
    //   72: aload #14
    //   74: getfield mTargetedTransitions : Ljava/util/ArrayList;
    //   77: aload_0
    //   78: invokevirtual contains : (Ljava/lang/Object;)Z
    //   81: ifne -> 87
    //   84: aconst_null
    //   85: astore #15
    //   87: aload_2
    //   88: astore #16
    //   90: aload_2
    //   91: ifnull -> 111
    //   94: aload_2
    //   95: astore #16
    //   97: aload_2
    //   98: getfield mTargetedTransitions : Ljava/util/ArrayList;
    //   101: aload_0
    //   102: invokevirtual contains : (Ljava/lang/Object;)Z
    //   105: ifne -> 111
    //   108: aconst_null
    //   109: astore #16
    //   111: aload #15
    //   113: ifnonnull -> 132
    //   116: aload #16
    //   118: ifnonnull -> 132
    //   121: lload #10
    //   123: lstore #12
    //   125: iload #6
    //   127: istore #7
    //   129: goto -> 542
    //   132: aload #15
    //   134: ifnull -> 162
    //   137: aload #16
    //   139: ifnull -> 162
    //   142: aload_0
    //   143: aload #15
    //   145: aload #16
    //   147: invokevirtual isTransitionRequired : (Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)Z
    //   150: ifeq -> 156
    //   153: goto -> 162
    //   156: iconst_0
    //   157: istore #7
    //   159: goto -> 165
    //   162: iconst_1
    //   163: istore #7
    //   165: iload #7
    //   167: ifeq -> 121
    //   170: aload_0
    //   171: aload_1
    //   172: aload #15
    //   174: aload #16
    //   176: invokevirtual createAnimator : (Landroid/view/ViewGroup;Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)Landroid/animation/Animator;
    //   179: astore_2
    //   180: aload_2
    //   181: ifnull -> 121
    //   184: aload #16
    //   186: ifnull -> 430
    //   189: aload #16
    //   191: getfield view : Landroid/view/View;
    //   194: astore #17
    //   196: aload_0
    //   197: invokevirtual getTransitionProperties : ()[Ljava/lang/String;
    //   200: astore #21
    //   202: aload #17
    //   204: ifnull -> 424
    //   207: aload #21
    //   209: ifnull -> 424
    //   212: aload #21
    //   214: arraylength
    //   215: ifle -> 424
    //   218: new android/support/transition/TransitionValues
    //   221: dup
    //   222: invokespecial <init> : ()V
    //   225: astore #18
    //   227: aload #18
    //   229: aload #17
    //   231: putfield view : Landroid/view/View;
    //   234: aload_3
    //   235: getfield mViewValues : Landroid/support/v4/util/ArrayMap;
    //   238: aload #17
    //   240: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   243: checkcast android/support/transition/TransitionValues
    //   246: astore #14
    //   248: iload #6
    //   250: istore #7
    //   252: aload #14
    //   254: ifnull -> 312
    //   257: iconst_0
    //   258: istore #9
    //   260: iload #6
    //   262: istore #7
    //   264: iload #9
    //   266: aload #21
    //   268: arraylength
    //   269: if_icmpge -> 312
    //   272: aload #18
    //   274: getfield values : Ljava/util/Map;
    //   277: aload #21
    //   279: iload #9
    //   281: aaload
    //   282: aload #14
    //   284: getfield values : Ljava/util/Map;
    //   287: aload #21
    //   289: iload #9
    //   291: aaload
    //   292: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   297: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   302: pop
    //   303: iload #9
    //   305: iconst_1
    //   306: iadd
    //   307: istore #9
    //   309: goto -> 260
    //   312: iload #7
    //   314: istore #6
    //   316: aload #20
    //   318: invokevirtual size : ()I
    //   321: istore #9
    //   323: iconst_0
    //   324: istore #7
    //   326: iload #7
    //   328: iload #9
    //   330: if_icmpge -> 417
    //   333: aload #20
    //   335: aload #20
    //   337: iload #7
    //   339: invokevirtual keyAt : (I)Ljava/lang/Object;
    //   342: checkcast android/animation/Animator
    //   345: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   348: checkcast android/support/transition/Transition$AnimationInfo
    //   351: astore #14
    //   353: aload #14
    //   355: getfield mValues : Landroid/support/transition/TransitionValues;
    //   358: ifnull -> 408
    //   361: aload #14
    //   363: getfield mView : Landroid/view/View;
    //   366: aload #17
    //   368: if_acmpne -> 408
    //   371: aload #14
    //   373: getfield mName : Ljava/lang/String;
    //   376: aload_0
    //   377: invokevirtual getName : ()Ljava/lang/String;
    //   380: invokevirtual equals : (Ljava/lang/Object;)Z
    //   383: ifeq -> 408
    //   386: aload #14
    //   388: getfield mValues : Landroid/support/transition/TransitionValues;
    //   391: aload #18
    //   393: invokevirtual equals : (Ljava/lang/Object;)Z
    //   396: ifeq -> 408
    //   399: aconst_null
    //   400: astore_2
    //   401: aload #18
    //   403: astore #14
    //   405: goto -> 427
    //   408: iload #7
    //   410: iconst_1
    //   411: iadd
    //   412: istore #7
    //   414: goto -> 326
    //   417: aload #18
    //   419: astore #14
    //   421: goto -> 427
    //   424: aconst_null
    //   425: astore #14
    //   427: goto -> 440
    //   430: aload #15
    //   432: getfield view : Landroid/view/View;
    //   435: astore #17
    //   437: aconst_null
    //   438: astore #14
    //   440: lload #10
    //   442: lstore #12
    //   444: iload #6
    //   446: istore #7
    //   448: aload_2
    //   449: ifnull -> 542
    //   452: lload #10
    //   454: lstore #12
    //   456: aload_0
    //   457: getfield mPropagation : Landroid/support/transition/TransitionPropagation;
    //   460: ifnull -> 502
    //   463: aload_0
    //   464: getfield mPropagation : Landroid/support/transition/TransitionPropagation;
    //   467: aload_1
    //   468: aload_0
    //   469: aload #15
    //   471: aload #16
    //   473: invokevirtual getStartDelay : (Landroid/view/ViewGroup;Landroid/support/transition/Transition;Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)J
    //   476: lstore #12
    //   478: aload #19
    //   480: aload_0
    //   481: getfield mAnimators : Ljava/util/ArrayList;
    //   484: invokevirtual size : ()I
    //   487: lload #12
    //   489: l2i
    //   490: invokevirtual put : (II)V
    //   493: lload #12
    //   495: lload #10
    //   497: invokestatic min : (JJ)J
    //   500: lstore #12
    //   502: aload #20
    //   504: aload_2
    //   505: new android/support/transition/Transition$AnimationInfo
    //   508: dup
    //   509: aload #17
    //   511: aload_0
    //   512: invokevirtual getName : ()Ljava/lang/String;
    //   515: aload_0
    //   516: aload_1
    //   517: invokestatic getWindowId : (Landroid/view/View;)Landroid/support/transition/WindowIdImpl;
    //   520: aload #14
    //   522: invokespecial <init> : (Landroid/view/View;Ljava/lang/String;Landroid/support/transition/Transition;Landroid/support/transition/WindowIdImpl;Landroid/support/transition/TransitionValues;)V
    //   525: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   528: pop
    //   529: aload_0
    //   530: getfield mAnimators : Ljava/util/ArrayList;
    //   533: aload_2
    //   534: invokevirtual add : (Ljava/lang/Object;)Z
    //   537: pop
    //   538: iload #6
    //   540: istore #7
    //   542: iload #7
    //   544: iconst_1
    //   545: iadd
    //   546: istore #6
    //   548: lload #12
    //   550: lstore #10
    //   552: goto -> 29
    //   555: lload #10
    //   557: lconst_0
    //   558: lcmp
    //   559: ifeq -> 626
    //   562: iconst_0
    //   563: istore #6
    //   565: iload #6
    //   567: aload #19
    //   569: invokevirtual size : ()I
    //   572: if_icmpge -> 626
    //   575: aload #19
    //   577: iload #6
    //   579: invokevirtual keyAt : (I)I
    //   582: istore #7
    //   584: aload_0
    //   585: getfield mAnimators : Ljava/util/ArrayList;
    //   588: iload #7
    //   590: invokevirtual get : (I)Ljava/lang/Object;
    //   593: checkcast android/animation/Animator
    //   596: astore_1
    //   597: aload_1
    //   598: aload #19
    //   600: iload #6
    //   602: invokevirtual valueAt : (I)I
    //   605: i2l
    //   606: lload #10
    //   608: lsub
    //   609: aload_1
    //   610: invokevirtual getStartDelay : ()J
    //   613: ladd
    //   614: invokevirtual setStartDelay : (J)V
    //   617: iload #6
    //   619: iconst_1
    //   620: iadd
    //   621: istore #6
    //   623: goto -> 565
    //   626: return }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void end() {
    this.mNumInstances--;
    if (this.mNumInstances == 0) {
      if (this.mListeners != null && this.mListeners.size() > 0) {
        ArrayList arrayList = (ArrayList)this.mListeners.clone();
        int i = arrayList.size();
        for (byte b1 = 0; b1 < i; b1++)
          ((TransitionListener)arrayList.get(b1)).onTransitionEnd(this); 
      } 
      byte b;
      for (b = 0; b < this.mStartValues.mItemIdValues.size(); b++) {
        View view = (View)this.mStartValues.mItemIdValues.valueAt(b);
        if (view != null)
          ViewCompat.setHasTransientState(view, false); 
      } 
      for (b = 0; b < this.mEndValues.mItemIdValues.size(); b++) {
        View view = (View)this.mEndValues.mItemIdValues.valueAt(b);
        if (view != null)
          ViewCompat.setHasTransientState(view, false); 
      } 
      this.mEnded = true;
    } 
  }
  
  @NonNull
  public Transition excludeChildren(@IdRes int paramInt, boolean paramBoolean) {
    this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, paramInt, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeChildren(@NonNull View paramView, boolean paramBoolean) {
    this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, paramView, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeChildren(@NonNull Class paramClass, boolean paramBoolean) {
    this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, paramClass, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeTarget(@IdRes int paramInt, boolean paramBoolean) {
    this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, paramInt, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeTarget(@NonNull View paramView, boolean paramBoolean) {
    this.mTargetExcludes = excludeView(this.mTargetExcludes, paramView, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeTarget(@NonNull Class paramClass, boolean paramBoolean) {
    this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, paramClass, paramBoolean);
    return this;
  }
  
  @NonNull
  public Transition excludeTarget(@NonNull String paramString, boolean paramBoolean) {
    this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, paramString, paramBoolean);
    return this;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void forceToEnd(ViewGroup paramViewGroup) {
    ArrayMap arrayMap = getRunningAnimators();
    int i = arrayMap.size();
    if (paramViewGroup != null) {
      WindowIdImpl windowIdImpl = ViewUtils.getWindowId(paramViewGroup);
      while (--i >= 0) {
        AnimationInfo animationInfo = (AnimationInfo)arrayMap.valueAt(i);
        if (animationInfo.mView != null && windowIdImpl != null && windowIdImpl.equals(animationInfo.mWindowId))
          ((Animator)arrayMap.keyAt(i)).end(); 
        i--;
      } 
    } 
  }
  
  public long getDuration() { return this.mDuration; }
  
  @Nullable
  public Rect getEpicenter() { return (this.mEpicenterCallback == null) ? null : this.mEpicenterCallback.onGetEpicenter(this); }
  
  @Nullable
  public EpicenterCallback getEpicenterCallback() { return this.mEpicenterCallback; }
  
  @Nullable
  public TimeInterpolator getInterpolator() { return this.mInterpolator; }
  
  TransitionValues getMatchedTransitionValues(View paramView, boolean paramBoolean) {
    ArrayList arrayList;
    byte b2;
    TransitionValues transitionValues;
    if (this.mParent != null)
      return this.mParent.getMatchedTransitionValues(paramView, paramBoolean); 
    if (paramBoolean) {
      arrayList = this.mStartValuesList;
    } else {
      arrayList = this.mEndValuesList;
    } 
    View view = null;
    if (arrayList == null)
      return null; 
    int i = arrayList.size();
    byte b3 = -1;
    byte b1 = 0;
    while (true) {
      b2 = b3;
      if (b1 < i) {
        TransitionValues transitionValues1 = (TransitionValues)arrayList.get(b1);
        if (transitionValues1 == null)
          return null; 
        if (transitionValues1.view == paramView) {
          b2 = b1;
          break;
        } 
        b1++;
        continue;
      } 
      break;
    } 
    paramView = view;
    if (b2 >= 0) {
      ArrayList arrayList1;
      if (paramBoolean) {
        arrayList1 = this.mEndValuesList;
      } else {
        arrayList1 = this.mStartValuesList;
      } 
      transitionValues = (TransitionValues)arrayList1.get(b2);
    } 
    return transitionValues;
  }
  
  @NonNull
  public String getName() { return this.mName; }
  
  @NonNull
  public PathMotion getPathMotion() { return this.mPathMotion; }
  
  @Nullable
  public TransitionPropagation getPropagation() { return this.mPropagation; }
  
  public long getStartDelay() { return this.mStartDelay; }
  
  @NonNull
  public List<Integer> getTargetIds() { return this.mTargetIds; }
  
  @Nullable
  public List<String> getTargetNames() { return this.mTargetNames; }
  
  @Nullable
  public List<Class> getTargetTypes() { return this.mTargetTypes; }
  
  @NonNull
  public List<View> getTargets() { return this.mTargets; }
  
  @Nullable
  public String[] getTransitionProperties() { return null; }
  
  @Nullable
  public TransitionValues getTransitionValues(@NonNull View paramView, boolean paramBoolean) {
    TransitionValuesMaps transitionValuesMaps;
    if (this.mParent != null)
      return this.mParent.getTransitionValues(paramView, paramBoolean); 
    if (paramBoolean) {
      transitionValuesMaps = this.mStartValues;
    } else {
      transitionValuesMaps = this.mEndValues;
    } 
    return (TransitionValues)transitionValuesMaps.mViewValues.get(paramView);
  }
  
  public boolean isTransitionRequired(@Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2) { // Byte code:
    //   0: iconst_0
    //   1: istore #6
    //   3: iload #6
    //   5: istore #5
    //   7: aload_1
    //   8: ifnull -> 120
    //   11: iload #6
    //   13: istore #5
    //   15: aload_2
    //   16: ifnull -> 120
    //   19: aload_0
    //   20: invokevirtual getTransitionProperties : ()[Ljava/lang/String;
    //   23: astore #7
    //   25: aload #7
    //   27: ifnull -> 69
    //   30: aload #7
    //   32: arraylength
    //   33: istore #4
    //   35: iconst_0
    //   36: istore_3
    //   37: iload #6
    //   39: istore #5
    //   41: iload_3
    //   42: iload #4
    //   44: if_icmpge -> 120
    //   47: aload_1
    //   48: aload_2
    //   49: aload #7
    //   51: iload_3
    //   52: aaload
    //   53: invokestatic isValueChanged : (Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;Ljava/lang/String;)Z
    //   56: ifeq -> 62
    //   59: goto -> 117
    //   62: iload_3
    //   63: iconst_1
    //   64: iadd
    //   65: istore_3
    //   66: goto -> 37
    //   69: aload_1
    //   70: getfield values : Ljava/util/Map;
    //   73: invokeinterface keySet : ()Ljava/util/Set;
    //   78: invokeinterface iterator : ()Ljava/util/Iterator;
    //   83: astore #7
    //   85: iload #6
    //   87: istore #5
    //   89: aload #7
    //   91: invokeinterface hasNext : ()Z
    //   96: ifeq -> 120
    //   99: aload_1
    //   100: aload_2
    //   101: aload #7
    //   103: invokeinterface next : ()Ljava/lang/Object;
    //   108: checkcast java/lang/String
    //   111: invokestatic isValueChanged : (Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;Ljava/lang/String;)Z
    //   114: ifeq -> 85
    //   117: iconst_1
    //   118: istore #5
    //   120: iload #5
    //   122: ireturn }
  
  boolean isValidTarget(View paramView) {
    int i = paramView.getId();
    if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf(i)))
      return false; 
    if (this.mTargetExcludes != null && this.mTargetExcludes.contains(paramView))
      return false; 
    if (this.mTargetTypeExcludes != null) {
      int j = this.mTargetTypeExcludes.size();
      for (byte b = 0; b < j; b++) {
        if (((Class)this.mTargetTypeExcludes.get(b)).isInstance(paramView))
          return false; 
      } 
    } 
    if (this.mTargetNameExcludes != null && ViewCompat.getTransitionName(paramView) != null && this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(paramView)))
      return false; 
    if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0 && (this.mTargetTypes == null || this.mTargetTypes.isEmpty()) && (this.mTargetNames == null || this.mTargetNames.isEmpty()))
      return true; 
    if (!this.mTargetIds.contains(Integer.valueOf(i))) {
      if (this.mTargets.contains(paramView))
        return true; 
      if (this.mTargetNames != null && this.mTargetNames.contains(ViewCompat.getTransitionName(paramView)))
        return true; 
      if (this.mTargetTypes != null)
        for (byte b = 0; b < this.mTargetTypes.size(); b++) {
          if (((Class)this.mTargetTypes.get(b)).isInstance(paramView))
            return true; 
        }  
      return false;
    } 
    return true;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void pause(View paramView) {
    if (!this.mEnded) {
      ArrayMap arrayMap = getRunningAnimators();
      int i = arrayMap.size();
      WindowIdImpl windowIdImpl = ViewUtils.getWindowId(paramView);
      while (--i >= 0) {
        AnimationInfo animationInfo = (AnimationInfo)arrayMap.valueAt(i);
        if (animationInfo.mView != null && windowIdImpl.equals(animationInfo.mWindowId))
          AnimatorUtils.pause((Animator)arrayMap.keyAt(i)); 
        i--;
      } 
      if (this.mListeners != null && this.mListeners.size() > 0) {
        ArrayList arrayList = (ArrayList)this.mListeners.clone();
        int j = arrayList.size();
        for (i = 0; i < j; i++)
          ((TransitionListener)arrayList.get(i)).onTransitionPause(this); 
      } 
      this.mPaused = true;
    } 
  }
  
  void playTransition(ViewGroup paramViewGroup) {
    this.mStartValuesList = new ArrayList();
    this.mEndValuesList = new ArrayList();
    matchStartAndEnd(this.mStartValues, this.mEndValues);
    ArrayMap arrayMap = getRunningAnimators();
    int i = arrayMap.size();
    WindowIdImpl windowIdImpl = ViewUtils.getWindowId(paramViewGroup);
    while (--i >= 0) {
      Animator animator = (Animator)arrayMap.keyAt(i);
      if (animator != null) {
        AnimationInfo animationInfo = (AnimationInfo)arrayMap.get(animator);
        if (animationInfo != null && animationInfo.mView != null && windowIdImpl.equals(animationInfo.mWindowId)) {
          boolean bool;
          TransitionValues transitionValues1 = animationInfo.mValues;
          View view = animationInfo.mView;
          TransitionValues transitionValues2 = getTransitionValues(view, true);
          TransitionValues transitionValues3 = getMatchedTransitionValues(view, true);
          if ((transitionValues2 != null || transitionValues3 != null) && animationInfo.mTransition.isTransitionRequired(transitionValues1, transitionValues3)) {
            bool = true;
          } else {
            bool = false;
          } 
          if (bool)
            if (animator.isRunning() || animator.isStarted()) {
              animator.cancel();
            } else {
              arrayMap.remove(animator);
            }  
        } 
      } 
      i--;
    } 
    createAnimators(paramViewGroup, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
    runAnimators();
  }
  
  @NonNull
  public Transition removeListener(@NonNull TransitionListener paramTransitionListener) {
    if (this.mListeners == null)
      return this; 
    this.mListeners.remove(paramTransitionListener);
    if (this.mListeners.size() == 0)
      this.mListeners = null; 
    return this;
  }
  
  @NonNull
  public Transition removeTarget(@IdRes int paramInt) {
    if (paramInt != 0)
      this.mTargetIds.remove(Integer.valueOf(paramInt)); 
    return this;
  }
  
  @NonNull
  public Transition removeTarget(@NonNull View paramView) {
    this.mTargets.remove(paramView);
    return this;
  }
  
  @NonNull
  public Transition removeTarget(@NonNull Class paramClass) {
    if (this.mTargetTypes != null)
      this.mTargetTypes.remove(paramClass); 
    return this;
  }
  
  @NonNull
  public Transition removeTarget(@NonNull String paramString) {
    if (this.mTargetNames != null)
      this.mTargetNames.remove(paramString); 
    return this;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void resume(View paramView) {
    if (this.mPaused) {
      if (!this.mEnded) {
        ArrayMap arrayMap = getRunningAnimators();
        int i = arrayMap.size();
        WindowIdImpl windowIdImpl = ViewUtils.getWindowId(paramView);
        while (--i >= 0) {
          AnimationInfo animationInfo = (AnimationInfo)arrayMap.valueAt(i);
          if (animationInfo.mView != null && windowIdImpl.equals(animationInfo.mWindowId))
            AnimatorUtils.resume((Animator)arrayMap.keyAt(i)); 
          i--;
        } 
        if (this.mListeners != null && this.mListeners.size() > 0) {
          ArrayList arrayList = (ArrayList)this.mListeners.clone();
          int j = arrayList.size();
          for (i = 0; i < j; i++)
            ((TransitionListener)arrayList.get(i)).onTransitionResume(this); 
        } 
      } 
      this.mPaused = false;
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void runAnimators() {
    start();
    ArrayMap arrayMap = getRunningAnimators();
    for (Animator animator : this.mAnimators) {
      if (arrayMap.containsKey(animator)) {
        start();
        runAnimator(animator, arrayMap);
      } 
    } 
    this.mAnimators.clear();
    end();
  }
  
  void setCanRemoveViews(boolean paramBoolean) { this.mCanRemoveViews = paramBoolean; }
  
  @NonNull
  public Transition setDuration(long paramLong) {
    this.mDuration = paramLong;
    return this;
  }
  
  public void setEpicenterCallback(@Nullable EpicenterCallback paramEpicenterCallback) { this.mEpicenterCallback = paramEpicenterCallback; }
  
  @NonNull
  public Transition setInterpolator(@Nullable TimeInterpolator paramTimeInterpolator) {
    this.mInterpolator = paramTimeInterpolator;
    return this;
  }
  
  public void setMatchOrder(int... paramVarArgs) {
    if (paramVarArgs == null || paramVarArgs.length == 0) {
      this.mMatchOrder = DEFAULT_MATCH_ORDER;
      return;
    } 
    for (byte b = 0; b < paramVarArgs.length; b++) {
      if (!isValidMatch(paramVarArgs[b]))
        throw new IllegalArgumentException("matches contains invalid value"); 
      if (alreadyContains(paramVarArgs, b))
        throw new IllegalArgumentException("matches contains a duplicate value"); 
    } 
    this.mMatchOrder = (int[])paramVarArgs.clone();
  }
  
  public void setPathMotion(@Nullable PathMotion paramPathMotion) {
    if (paramPathMotion == null) {
      this.mPathMotion = STRAIGHT_PATH_MOTION;
      return;
    } 
    this.mPathMotion = paramPathMotion;
  }
  
  public void setPropagation(@Nullable TransitionPropagation paramTransitionPropagation) { this.mPropagation = paramTransitionPropagation; }
  
  Transition setSceneRoot(ViewGroup paramViewGroup) {
    this.mSceneRoot = paramViewGroup;
    return this;
  }
  
  @NonNull
  public Transition setStartDelay(long paramLong) {
    this.mStartDelay = paramLong;
    return this;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected void start() {
    if (this.mNumInstances == 0) {
      if (this.mListeners != null && this.mListeners.size() > 0) {
        ArrayList arrayList = (ArrayList)this.mListeners.clone();
        int i = arrayList.size();
        for (byte b = 0; b < i; b++)
          ((TransitionListener)arrayList.get(b)).onTransitionStart(this); 
      } 
      this.mEnded = false;
    } 
    this.mNumInstances++;
  }
  
  public String toString() { return toString(""); }
  
  String toString(String paramString) { // Byte code:
    //   0: new java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore #4
    //   9: aload #4
    //   11: aload_1
    //   12: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: pop
    //   16: aload #4
    //   18: aload_0
    //   19: invokevirtual getClass : ()Ljava/lang/Class;
    //   22: invokevirtual getSimpleName : ()Ljava/lang/String;
    //   25: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: pop
    //   29: aload #4
    //   31: ldc_w '@'
    //   34: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: pop
    //   38: aload #4
    //   40: aload_0
    //   41: invokevirtual hashCode : ()I
    //   44: invokestatic toHexString : (I)Ljava/lang/String;
    //   47: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: pop
    //   51: aload #4
    //   53: ldc_w ': '
    //   56: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: pop
    //   60: aload #4
    //   62: invokevirtual toString : ()Ljava/lang/String;
    //   65: astore #4
    //   67: aload #4
    //   69: astore_1
    //   70: aload_0
    //   71: getfield mDuration : J
    //   74: ldc2_w -1
    //   77: lcmp
    //   78: ifeq -> 126
    //   81: new java/lang/StringBuilder
    //   84: dup
    //   85: invokespecial <init> : ()V
    //   88: astore_1
    //   89: aload_1
    //   90: aload #4
    //   92: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: pop
    //   96: aload_1
    //   97: ldc_w 'dur('
    //   100: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: pop
    //   104: aload_1
    //   105: aload_0
    //   106: getfield mDuration : J
    //   109: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: aload_1
    //   114: ldc_w ') '
    //   117: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: pop
    //   121: aload_1
    //   122: invokevirtual toString : ()Ljava/lang/String;
    //   125: astore_1
    //   126: aload_1
    //   127: astore #4
    //   129: aload_0
    //   130: getfield mStartDelay : J
    //   133: ldc2_w -1
    //   136: lcmp
    //   137: ifeq -> 191
    //   140: new java/lang/StringBuilder
    //   143: dup
    //   144: invokespecial <init> : ()V
    //   147: astore #4
    //   149: aload #4
    //   151: aload_1
    //   152: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: pop
    //   156: aload #4
    //   158: ldc_w 'dly('
    //   161: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload #4
    //   167: aload_0
    //   168: getfield mStartDelay : J
    //   171: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   174: pop
    //   175: aload #4
    //   177: ldc_w ') '
    //   180: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: pop
    //   184: aload #4
    //   186: invokevirtual toString : ()Ljava/lang/String;
    //   189: astore #4
    //   191: aload #4
    //   193: astore_1
    //   194: aload_0
    //   195: getfield mInterpolator : Landroid/animation/TimeInterpolator;
    //   198: ifnull -> 246
    //   201: new java/lang/StringBuilder
    //   204: dup
    //   205: invokespecial <init> : ()V
    //   208: astore_1
    //   209: aload_1
    //   210: aload #4
    //   212: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: pop
    //   216: aload_1
    //   217: ldc_w 'interp('
    //   220: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: pop
    //   224: aload_1
    //   225: aload_0
    //   226: getfield mInterpolator : Landroid/animation/TimeInterpolator;
    //   229: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   232: pop
    //   233: aload_1
    //   234: ldc_w ') '
    //   237: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: pop
    //   241: aload_1
    //   242: invokevirtual toString : ()Ljava/lang/String;
    //   245: astore_1
    //   246: aload_0
    //   247: getfield mTargetIds : Ljava/util/ArrayList;
    //   250: invokevirtual size : ()I
    //   253: ifgt -> 269
    //   256: aload_1
    //   257: astore #4
    //   259: aload_0
    //   260: getfield mTargets : Ljava/util/ArrayList;
    //   263: invokevirtual size : ()I
    //   266: ifle -> 550
    //   269: new java/lang/StringBuilder
    //   272: dup
    //   273: invokespecial <init> : ()V
    //   276: astore #4
    //   278: aload #4
    //   280: aload_1
    //   281: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: pop
    //   285: aload #4
    //   287: ldc_w 'tgts('
    //   290: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   293: pop
    //   294: aload #4
    //   296: invokevirtual toString : ()Ljava/lang/String;
    //   299: astore #4
    //   301: aload_0
    //   302: getfield mTargetIds : Ljava/util/ArrayList;
    //   305: invokevirtual size : ()I
    //   308: istore_2
    //   309: iconst_0
    //   310: istore_3
    //   311: aload #4
    //   313: astore_1
    //   314: iload_2
    //   315: ifle -> 413
    //   318: aload #4
    //   320: astore_1
    //   321: iconst_0
    //   322: istore_2
    //   323: iload_2
    //   324: aload_0
    //   325: getfield mTargetIds : Ljava/util/ArrayList;
    //   328: invokevirtual size : ()I
    //   331: if_icmpge -> 413
    //   334: aload_1
    //   335: astore #4
    //   337: iload_2
    //   338: ifle -> 373
    //   341: new java/lang/StringBuilder
    //   344: dup
    //   345: invokespecial <init> : ()V
    //   348: astore #4
    //   350: aload #4
    //   352: aload_1
    //   353: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   356: pop
    //   357: aload #4
    //   359: ldc_w ', '
    //   362: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   365: pop
    //   366: aload #4
    //   368: invokevirtual toString : ()Ljava/lang/String;
    //   371: astore #4
    //   373: new java/lang/StringBuilder
    //   376: dup
    //   377: invokespecial <init> : ()V
    //   380: astore_1
    //   381: aload_1
    //   382: aload #4
    //   384: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   387: pop
    //   388: aload_1
    //   389: aload_0
    //   390: getfield mTargetIds : Ljava/util/ArrayList;
    //   393: iload_2
    //   394: invokevirtual get : (I)Ljava/lang/Object;
    //   397: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   400: pop
    //   401: aload_1
    //   402: invokevirtual toString : ()Ljava/lang/String;
    //   405: astore_1
    //   406: iload_2
    //   407: iconst_1
    //   408: iadd
    //   409: istore_2
    //   410: goto -> 323
    //   413: aload_1
    //   414: astore #4
    //   416: aload_0
    //   417: getfield mTargets : Ljava/util/ArrayList;
    //   420: invokevirtual size : ()I
    //   423: ifle -> 521
    //   426: iload_3
    //   427: istore_2
    //   428: aload_1
    //   429: astore #4
    //   431: iload_2
    //   432: aload_0
    //   433: getfield mTargets : Ljava/util/ArrayList;
    //   436: invokevirtual size : ()I
    //   439: if_icmpge -> 521
    //   442: aload_1
    //   443: astore #4
    //   445: iload_2
    //   446: ifle -> 481
    //   449: new java/lang/StringBuilder
    //   452: dup
    //   453: invokespecial <init> : ()V
    //   456: astore #4
    //   458: aload #4
    //   460: aload_1
    //   461: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   464: pop
    //   465: aload #4
    //   467: ldc_w ', '
    //   470: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   473: pop
    //   474: aload #4
    //   476: invokevirtual toString : ()Ljava/lang/String;
    //   479: astore #4
    //   481: new java/lang/StringBuilder
    //   484: dup
    //   485: invokespecial <init> : ()V
    //   488: astore_1
    //   489: aload_1
    //   490: aload #4
    //   492: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   495: pop
    //   496: aload_1
    //   497: aload_0
    //   498: getfield mTargets : Ljava/util/ArrayList;
    //   501: iload_2
    //   502: invokevirtual get : (I)Ljava/lang/Object;
    //   505: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   508: pop
    //   509: aload_1
    //   510: invokevirtual toString : ()Ljava/lang/String;
    //   513: astore_1
    //   514: iload_2
    //   515: iconst_1
    //   516: iadd
    //   517: istore_2
    //   518: goto -> 428
    //   521: new java/lang/StringBuilder
    //   524: dup
    //   525: invokespecial <init> : ()V
    //   528: astore_1
    //   529: aload_1
    //   530: aload #4
    //   532: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: pop
    //   536: aload_1
    //   537: ldc_w ')'
    //   540: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   543: pop
    //   544: aload_1
    //   545: invokevirtual toString : ()Ljava/lang/String;
    //   548: astore #4
    //   550: aload #4
    //   552: areturn }
  
  private static class AnimationInfo {
    String mName;
    
    Transition mTransition;
    
    TransitionValues mValues;
    
    View mView;
    
    WindowIdImpl mWindowId;
    
    AnimationInfo(View param1View, String param1String, Transition param1Transition, WindowIdImpl param1WindowIdImpl, TransitionValues param1TransitionValues) {
      this.mView = param1View;
      this.mName = param1String;
      this.mValues = param1TransitionValues;
      this.mWindowId = param1WindowIdImpl;
      this.mTransition = param1Transition;
    }
  }
  
  private static class ArrayListManager {
    static <T> ArrayList<T> add(ArrayList<T> param1ArrayList, T param1T) {
      ArrayList<T> arrayList = param1ArrayList;
      if (param1ArrayList == null)
        arrayList = new ArrayList<T>(); 
      if (!arrayList.contains(param1T))
        arrayList.add(param1T); 
      return arrayList;
    }
    
    static <T> ArrayList<T> remove(ArrayList<T> param1ArrayList, T param1T) {
      ArrayList<T> arrayList = param1ArrayList;
      if (param1ArrayList != null) {
        param1ArrayList.remove(param1T);
        arrayList = param1ArrayList;
        if (param1ArrayList.isEmpty())
          arrayList = null; 
      } 
      return arrayList;
    }
  }
  
  public static abstract class EpicenterCallback {
    public abstract Rect onGetEpicenter(@NonNull Transition param1Transition);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface MatchOrder {}
  
  public static interface TransitionListener {
    void onTransitionCancel(@NonNull Transition param1Transition);
    
    void onTransitionEnd(@NonNull Transition param1Transition);
    
    void onTransitionPause(@NonNull Transition param1Transition);
    
    void onTransitionResume(@NonNull Transition param1Transition);
    
    void onTransitionStart(@NonNull Transition param1Transition);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/Transition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */