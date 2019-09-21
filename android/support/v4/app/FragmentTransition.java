package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FragmentTransition {
  private static final int[] INVERSE_OPS;
  
  private static final FragmentTransitionImpl PLATFORM_IMPL;
  
  private static final FragmentTransitionImpl SUPPORT_IMPL;
  
  static  {
    Object object;
    INVERSE_OPS = new int[] { 0, 3, 0, 1, 5, 4, 7, 6, 9, 8 };
    if (Build.VERSION.SDK_INT >= 21) {
      object = new FragmentTransitionCompat21();
    } else {
      object = null;
    } 
    PLATFORM_IMPL = object;
    SUPPORT_IMPL = resolveSupportImpl();
  }
  
  private static void addSharedElementsWithMatchingNames(ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap, Collection<String> paramCollection) {
    for (int i = paramArrayMap.size() - 1; i >= 0; i--) {
      View view = (View)paramArrayMap.valueAt(i);
      if (paramCollection.contains(ViewCompat.getTransitionName(view)))
        paramArrayList.add(view); 
    } 
  }
  
  private static void addToFirstInLastOut(BackStackRecord paramBackStackRecord, BackStackRecord.Op paramOp, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean1, boolean paramBoolean2) { // Byte code:
    //   0: aload_1
    //   1: getfield fragment : Landroid/support/v4/app/Fragment;
    //   4: astore #12
    //   6: aload #12
    //   8: ifnonnull -> 12
    //   11: return
    //   12: aload #12
    //   14: getfield mContainerId : I
    //   17: istore #8
    //   19: iload #8
    //   21: ifne -> 25
    //   24: return
    //   25: iload_3
    //   26: ifeq -> 42
    //   29: getstatic android/support/v4/app/FragmentTransition.INVERSE_OPS : [I
    //   32: aload_1
    //   33: getfield cmd : I
    //   36: iaload
    //   37: istore #5
    //   39: goto -> 48
    //   42: aload_1
    //   43: getfield cmd : I
    //   46: istore #5
    //   48: iconst_0
    //   49: istore #10
    //   51: iconst_0
    //   52: istore #9
    //   54: iload #5
    //   56: iconst_1
    //   57: if_icmpeq -> 291
    //   60: iload #5
    //   62: tableswitch default -> 96, 3 -> 201, 4 -> 150, 5 -> 108, 6 -> 201, 7 -> 291
    //   96: iconst_0
    //   97: istore #5
    //   99: iconst_0
    //   100: istore #7
    //   102: iconst_0
    //   103: istore #6
    //   105: goto -> 337
    //   108: iload #4
    //   110: ifeq -> 140
    //   113: aload #12
    //   115: getfield mHiddenChanged : Z
    //   118: ifeq -> 328
    //   121: aload #12
    //   123: getfield mHidden : Z
    //   126: ifne -> 328
    //   129: aload #12
    //   131: getfield mAdded : Z
    //   134: ifeq -> 328
    //   137: goto -> 322
    //   140: aload #12
    //   142: getfield mHidden : Z
    //   145: istore #9
    //   147: goto -> 331
    //   150: iload #4
    //   152: ifeq -> 182
    //   155: aload #12
    //   157: getfield mHiddenChanged : Z
    //   160: ifeq -> 249
    //   163: aload #12
    //   165: getfield mAdded : Z
    //   168: ifeq -> 249
    //   171: aload #12
    //   173: getfield mHidden : Z
    //   176: ifeq -> 249
    //   179: goto -> 243
    //   182: aload #12
    //   184: getfield mAdded : Z
    //   187: ifeq -> 249
    //   190: aload #12
    //   192: getfield mHidden : Z
    //   195: ifne -> 249
    //   198: goto -> 179
    //   201: iload #4
    //   203: ifeq -> 255
    //   206: aload #12
    //   208: getfield mAdded : Z
    //   211: ifne -> 249
    //   214: aload #12
    //   216: getfield mView : Landroid/view/View;
    //   219: ifnull -> 249
    //   222: aload #12
    //   224: getfield mView : Landroid/view/View;
    //   227: invokevirtual getVisibility : ()I
    //   230: ifne -> 249
    //   233: aload #12
    //   235: getfield mPostponedAlpha : F
    //   238: fconst_0
    //   239: fcmpl
    //   240: iflt -> 249
    //   243: iconst_1
    //   244: istore #5
    //   246: goto -> 274
    //   249: iconst_0
    //   250: istore #5
    //   252: goto -> 274
    //   255: aload #12
    //   257: getfield mAdded : Z
    //   260: ifeq -> 249
    //   263: aload #12
    //   265: getfield mHidden : Z
    //   268: ifne -> 249
    //   271: goto -> 243
    //   274: iload #5
    //   276: istore #6
    //   278: iconst_0
    //   279: istore #5
    //   281: iconst_1
    //   282: istore #7
    //   284: iload #10
    //   286: istore #9
    //   288: goto -> 337
    //   291: iload #4
    //   293: ifeq -> 306
    //   296: aload #12
    //   298: getfield mIsNewlyAdded : Z
    //   301: istore #9
    //   303: goto -> 331
    //   306: aload #12
    //   308: getfield mAdded : Z
    //   311: ifne -> 328
    //   314: aload #12
    //   316: getfield mHidden : Z
    //   319: ifne -> 328
    //   322: iconst_1
    //   323: istore #9
    //   325: goto -> 331
    //   328: iconst_0
    //   329: istore #9
    //   331: iconst_1
    //   332: istore #5
    //   334: goto -> 99
    //   337: aload_2
    //   338: iload #8
    //   340: invokevirtual get : (I)Ljava/lang/Object;
    //   343: checkcast android/support/v4/app/FragmentTransition$FragmentContainerTransition
    //   346: astore #11
    //   348: aload #11
    //   350: astore_1
    //   351: iload #9
    //   353: ifeq -> 381
    //   356: aload #11
    //   358: aload_2
    //   359: iload #8
    //   361: invokestatic ensureContainer : (Landroid/support/v4/app/FragmentTransition$FragmentContainerTransition;Landroid/util/SparseArray;I)Landroid/support/v4/app/FragmentTransition$FragmentContainerTransition;
    //   364: astore_1
    //   365: aload_1
    //   366: aload #12
    //   368: putfield lastIn : Landroid/support/v4/app/Fragment;
    //   371: aload_1
    //   372: iload_3
    //   373: putfield lastInIsPop : Z
    //   376: aload_1
    //   377: aload_0
    //   378: putfield lastInTransaction : Landroid/support/v4/app/BackStackRecord;
    //   381: iload #4
    //   383: ifne -> 458
    //   386: iload #5
    //   388: ifeq -> 458
    //   391: aload_1
    //   392: ifnull -> 409
    //   395: aload_1
    //   396: getfield firstOut : Landroid/support/v4/app/Fragment;
    //   399: aload #12
    //   401: if_acmpne -> 409
    //   404: aload_1
    //   405: aconst_null
    //   406: putfield firstOut : Landroid/support/v4/app/Fragment;
    //   409: aload_0
    //   410: getfield mManager : Landroid/support/v4/app/FragmentManagerImpl;
    //   413: astore #11
    //   415: aload #12
    //   417: getfield mState : I
    //   420: iconst_1
    //   421: if_icmpge -> 458
    //   424: aload #11
    //   426: getfield mCurState : I
    //   429: iconst_1
    //   430: if_icmplt -> 458
    //   433: aload_0
    //   434: getfield mReorderingAllowed : Z
    //   437: ifne -> 458
    //   440: aload #11
    //   442: aload #12
    //   444: invokevirtual makeActive : (Landroid/support/v4/app/Fragment;)V
    //   447: aload #11
    //   449: aload #12
    //   451: iconst_1
    //   452: iconst_0
    //   453: iconst_0
    //   454: iconst_0
    //   455: invokevirtual moveToState : (Landroid/support/v4/app/Fragment;IIIZ)V
    //   458: aload_1
    //   459: astore #11
    //   461: iload #6
    //   463: ifeq -> 508
    //   466: aload_1
    //   467: ifnull -> 480
    //   470: aload_1
    //   471: astore #11
    //   473: aload_1
    //   474: getfield firstOut : Landroid/support/v4/app/Fragment;
    //   477: ifnonnull -> 508
    //   480: aload_1
    //   481: aload_2
    //   482: iload #8
    //   484: invokestatic ensureContainer : (Landroid/support/v4/app/FragmentTransition$FragmentContainerTransition;Landroid/util/SparseArray;I)Landroid/support/v4/app/FragmentTransition$FragmentContainerTransition;
    //   487: astore #11
    //   489: aload #11
    //   491: aload #12
    //   493: putfield firstOut : Landroid/support/v4/app/Fragment;
    //   496: aload #11
    //   498: iload_3
    //   499: putfield firstOutIsPop : Z
    //   502: aload #11
    //   504: aload_0
    //   505: putfield firstOutTransaction : Landroid/support/v4/app/BackStackRecord;
    //   508: iload #4
    //   510: ifne -> 539
    //   513: iload #7
    //   515: ifeq -> 539
    //   518: aload #11
    //   520: ifnull -> 539
    //   523: aload #11
    //   525: getfield lastIn : Landroid/support/v4/app/Fragment;
    //   528: aload #12
    //   530: if_acmpne -> 539
    //   533: aload #11
    //   535: aconst_null
    //   536: putfield lastIn : Landroid/support/v4/app/Fragment;
    //   539: return }
  
  public static void calculateFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean) {
    int i = paramBackStackRecord.mOps.size();
    for (byte b = 0; b < i; b++)
      addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(b), paramSparseArray, false, paramBoolean); 
  }
  
  private static ArrayMap<String, String> calculateNameOverrides(int paramInt1, ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, int paramInt2, int paramInt3) {
    ArrayMap arrayMap = new ArrayMap();
    while (--paramInt3 >= paramInt2) {
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(paramInt3);
      if (backStackRecord.interactsWith(paramInt1)) {
        boolean bool = ((Boolean)paramArrayList2.get(paramInt3)).booleanValue();
        if (backStackRecord.mSharedElementSourceNames != null) {
          ArrayList arrayList2;
          ArrayList arrayList1;
          int i = backStackRecord.mSharedElementSourceNames.size();
          if (bool) {
            arrayList2 = backStackRecord.mSharedElementSourceNames;
            arrayList1 = backStackRecord.mSharedElementTargetNames;
          } else {
            arrayList1 = backStackRecord.mSharedElementSourceNames;
            arrayList2 = backStackRecord.mSharedElementTargetNames;
          } 
          byte b;
          for (b = 0; b < i; b++) {
            String str1 = (String)arrayList1.get(b);
            String str2 = (String)arrayList2.get(b);
            String str3 = (String)arrayMap.remove(str2);
            if (str3 != null) {
              arrayMap.put(str1, str3);
            } else {
              arrayMap.put(str1, str2);
            } 
          } 
        } 
      } 
      paramInt3--;
    } 
    return arrayMap;
  }
  
  public static void calculatePopFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean) {
    if (!paramBackStackRecord.mManager.mContainer.onHasView())
      return; 
    for (int i = paramBackStackRecord.mOps.size() - 1; i >= 0; i--)
      addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(i), paramSparseArray, true, paramBoolean); 
  }
  
  private static void callSharedElementStartEnd(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean1, ArrayMap<String, View> paramArrayMap, boolean paramBoolean2) {
    SharedElementCallback sharedElementCallback;
    if (paramBoolean1) {
      sharedElementCallback = paramFragment2.getEnterTransitionCallback();
    } else {
      sharedElementCallback = sharedElementCallback.getEnterTransitionCallback();
    } 
    if (sharedElementCallback != null) {
      int i;
      ArrayList arrayList1 = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      byte b = 0;
      if (paramArrayMap == null) {
        i = 0;
      } else {
        i = paramArrayMap.size();
      } 
      while (b < i) {
        arrayList2.add(paramArrayMap.keyAt(b));
        arrayList1.add(paramArrayMap.valueAt(b));
        b++;
      } 
      if (paramBoolean2) {
        sharedElementCallback.onSharedElementStart(arrayList2, arrayList1, null);
        return;
      } 
      sharedElementCallback.onSharedElementEnd(arrayList2, arrayList1, null);
    } 
  }
  
  private static boolean canHandleAll(FragmentTransitionImpl paramFragmentTransitionImpl, List<Object> paramList) {
    int i = paramList.size();
    for (byte b = 0; b < i; b++) {
      if (!paramFragmentTransitionImpl.canHandle(paramList.get(b)))
        return false; 
    } 
    return true;
  }
  
  private static ArrayMap<String, View> captureInSharedElements(FragmentTransitionImpl paramFragmentTransitionImpl, ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition) {
    Fragment fragment = paramFragmentContainerTransition.lastIn;
    View view = fragment.getView();
    if (paramArrayMap.isEmpty() || paramObject == null || view == null) {
      paramArrayMap.clear();
      return null;
    } 
    ArrayMap arrayMap = new ArrayMap();
    paramFragmentTransitionImpl.findNamedViews(arrayMap, view);
    ArrayList arrayList = paramFragmentContainerTransition.lastInTransaction;
    if (paramFragmentContainerTransition.lastInIsPop) {
      paramObject = fragment.getExitTransitionCallback();
      arrayList = arrayList.mSharedElementSourceNames;
    } else {
      paramObject = fragment.getEnterTransitionCallback();
      arrayList = arrayList.mSharedElementTargetNames;
    } 
    if (arrayList != null) {
      arrayMap.retainAll(arrayList);
      arrayMap.retainAll(paramArrayMap.values());
    } 
    if (paramObject != null) {
      paramObject.onMapSharedElements(arrayList, arrayMap);
      int i;
      for (i = arrayList.size() - 1; i >= 0; i--) {
        String str = (String)arrayList.get(i);
        paramObject = (View)arrayMap.get(str);
        if (paramObject == null) {
          paramObject = findKeyForValue(paramArrayMap, str);
          if (paramObject != null)
            paramArrayMap.remove(paramObject); 
        } else if (!str.equals(ViewCompat.getTransitionName(paramObject))) {
          str = findKeyForValue(paramArrayMap, str);
          if (str != null)
            paramArrayMap.put(str, ViewCompat.getTransitionName(paramObject)); 
        } 
      } 
    } else {
      retainValues(paramArrayMap, arrayMap);
    } 
    return arrayMap;
  }
  
  private static ArrayMap<String, View> captureOutSharedElements(FragmentTransitionImpl paramFragmentTransitionImpl, ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition) {
    if (paramArrayMap.isEmpty() || paramObject == null) {
      paramArrayMap.clear();
      return null;
    } 
    paramObject = paramFragmentContainerTransition.firstOut;
    ArrayMap arrayMap = new ArrayMap();
    paramFragmentTransitionImpl.findNamedViews(arrayMap, paramObject.getView());
    ArrayList arrayList = paramFragmentContainerTransition.firstOutTransaction;
    if (paramFragmentContainerTransition.firstOutIsPop) {
      paramObject = paramObject.getEnterTransitionCallback();
      arrayList = arrayList.mSharedElementTargetNames;
    } else {
      paramObject = paramObject.getExitTransitionCallback();
      arrayList = arrayList.mSharedElementSourceNames;
    } 
    arrayMap.retainAll(arrayList);
    if (paramObject != null) {
      paramObject.onMapSharedElements(arrayList, arrayMap);
      int i;
      for (i = arrayList.size() - 1; i >= 0; i--) {
        String str = (String)arrayList.get(i);
        paramObject = (View)arrayMap.get(str);
        if (paramObject == null) {
          paramArrayMap.remove(str);
        } else if (!str.equals(ViewCompat.getTransitionName(paramObject))) {
          str = (String)paramArrayMap.remove(str);
          paramArrayMap.put(ViewCompat.getTransitionName(paramObject), str);
        } 
      } 
    } else {
      paramArrayMap.retainAll(arrayMap.keySet());
    } 
    return arrayMap;
  }
  
  private static FragmentTransitionImpl chooseImpl(Fragment paramFragment1, Fragment paramFragment2) {
    ArrayList arrayList = new ArrayList();
    if (paramFragment1 != null) {
      Object object2 = paramFragment1.getExitTransition();
      if (object2 != null)
        arrayList.add(object2); 
      object2 = paramFragment1.getReturnTransition();
      if (object2 != null)
        arrayList.add(object2); 
      Object object1 = paramFragment1.getSharedElementReturnTransition();
      if (object1 != null)
        arrayList.add(object1); 
    } 
    if (paramFragment2 != null) {
      Object object = paramFragment2.getEnterTransition();
      if (object != null)
        arrayList.add(object); 
      object = paramFragment2.getReenterTransition();
      if (object != null)
        arrayList.add(object); 
      object = paramFragment2.getSharedElementEnterTransition();
      if (object != null)
        arrayList.add(object); 
    } 
    if (arrayList.isEmpty())
      return null; 
    if (PLATFORM_IMPL != null && canHandleAll(PLATFORM_IMPL, arrayList))
      return PLATFORM_IMPL; 
    if (SUPPORT_IMPL != null && canHandleAll(SUPPORT_IMPL, arrayList))
      return SUPPORT_IMPL; 
    if (PLATFORM_IMPL != null || SUPPORT_IMPL != null)
      throw new IllegalArgumentException("Invalid Transition types"); 
    return null;
  }
  
  private static ArrayList<View> configureEnteringExitingViews(FragmentTransitionImpl paramFragmentTransitionImpl, Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, View paramView) {
    if (paramObject != null) {
      ArrayList arrayList2 = new ArrayList();
      View view = paramFragment.getView();
      if (view != null)
        paramFragmentTransitionImpl.captureTransitioningViews(arrayList2, view); 
      if (paramArrayList != null)
        arrayList2.removeAll(paramArrayList); 
      ArrayList arrayList1 = arrayList2;
      if (!arrayList2.isEmpty()) {
        arrayList2.add(paramView);
        paramFragmentTransitionImpl.addTargets(paramObject, arrayList2);
        return arrayList2;
      } 
    } else {
      paramFragment = null;
    } 
    return paramFragment;
  }
  
  private static Object configureSharedElementsOrdered(final FragmentTransitionImpl impl, ViewGroup paramViewGroup, final View nonExistentView, ArrayMap<String, String> paramArrayMap, final FragmentContainerTransition fragments, final ArrayList<View> sharedElementsOut, final ArrayList<View> sharedElementsIn, final Object enterTransition, final Object inEpicenter) {
    final Fragment inFragment = paramFragmentContainerTransition.lastIn;
    final Fragment outFragment = paramFragmentContainerTransition.firstOut;
    if (fragment1 != null) {
      Object object2;
      final Object finalSharedElementTransition;
      if (fragment2 == null)
        return null; 
      final boolean inIsPop = paramFragmentContainerTransition.lastInIsPop;
      if (paramArrayMap.isEmpty()) {
        object2 = null;
      } else {
        object2 = getSharedElementTransition(paramFragmentTransitionImpl, fragment1, fragment2, bool);
      } 
      final ArrayMap<String, String> nameOverrides = paramArrayMap;
      ArrayMap arrayMap2 = captureOutSharedElements(paramFragmentTransitionImpl, arrayMap1, object2, paramFragmentContainerTransition);
      if (paramArrayMap.isEmpty()) {
        paramArrayMap = null;
      } else {
        paramArrayList1.addAll(arrayMap2.values());
        object1 = object2;
      } 
      if (paramObject1 == null && paramObject2 == null && object1 == null)
        return null; 
      callSharedElementStartEnd(fragment1, fragment2, bool, arrayMap2, true);
      if (object1 != null) {
        object2 = new Rect();
        paramFragmentTransitionImpl.setSharedElementTargets(object1, paramView, paramArrayList1);
        setOutEpicenter(paramFragmentTransitionImpl, object1, paramObject2, arrayMap2, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
        paramObject2 = object2;
        if (paramObject1 != null) {
          paramFragmentTransitionImpl.setEpicenter(paramObject1, object2);
          paramObject2 = object2;
        } 
      } else {
        paramObject2 = null;
      } 
      OneShotPreDrawListener.add(paramViewGroup, new Runnable() {
            public void run() {
              ArrayMap arrayMap = FragmentTransition.captureInSharedElements(impl, nameOverrides, finalSharedElementTransition, fragments);
              if (arrayMap != null) {
                sharedElementsIn.addAll(arrayMap.values());
                sharedElementsIn.add(nonExistentView);
              } 
              FragmentTransition.callSharedElementStartEnd(inFragment, outFragment, inIsPop, arrayMap, false);
              if (finalSharedElementTransition != null) {
                impl.swapSharedElementTargets(finalSharedElementTransition, sharedElementsOut, sharedElementsIn);
                View view = FragmentTransition.getInEpicenterView(arrayMap, fragments, enterTransition, inIsPop);
                if (view != null)
                  impl.getBoundsOnScreen(view, inEpicenter); 
              } 
            }
          });
      return object1;
    } 
    return null;
  }
  
  private static Object configureSharedElementsReordered(final FragmentTransitionImpl impl, ViewGroup paramViewGroup, View paramView, ArrayMap<String, String> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2, Object paramObject1, Object paramObject2) {
    final Fragment inFragment = paramFragmentContainerTransition.lastIn;
    final Fragment outFragment = paramFragmentContainerTransition.firstOut;
    if (fragment1 != null)
      fragment1.getView().setVisibility(0); 
    if (fragment1 != null) {
      Object object2;
      final View epicenter;
      final View epicenterView;
      Object object1;
      if (fragment2 == null)
        return null; 
      final boolean inIsPop = paramFragmentContainerTransition.lastInIsPop;
      if (paramArrayMap.isEmpty()) {
        object2 = null;
      } else {
        object2 = getSharedElementTransition(paramFragmentTransitionImpl, fragment1, fragment2, bool);
      } 
      ArrayMap arrayMap2 = captureOutSharedElements(paramFragmentTransitionImpl, paramArrayMap, object2, paramFragmentContainerTransition);
      final ArrayMap inSharedElements = captureInSharedElements(paramFragmentTransitionImpl, paramArrayMap, object2, paramFragmentContainerTransition);
      if (paramArrayMap.isEmpty()) {
        if (arrayMap2 != null)
          arrayMap2.clear(); 
        if (arrayMap1 != null)
          arrayMap1.clear(); 
        paramArrayMap = null;
      } else {
        addSharedElementsWithMatchingNames(paramArrayList1, arrayMap2, paramArrayMap.keySet());
        addSharedElementsWithMatchingNames(paramArrayList2, arrayMap1, paramArrayMap.values());
        object1 = object2;
      } 
      if (paramObject1 == null && paramObject2 == null && object1 == null)
        return null; 
      callSharedElementStartEnd(fragment1, fragment2, bool, arrayMap2, true);
      if (object1 != null) {
        paramArrayList2.add(paramView);
        paramFragmentTransitionImpl.setSharedElementTargets(object1, paramView, paramArrayList1);
        setOutEpicenter(paramFragmentTransitionImpl, object1, paramObject2, arrayMap2, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
        Rect rect = new Rect();
        view1 = getInEpicenterView(arrayMap1, paramFragmentContainerTransition, paramObject1, bool);
        if (view1 != null)
          paramFragmentTransitionImpl.setEpicenter(paramObject1, rect); 
        view2 = rect;
      } else {
        paramView = null;
        view2 = paramView;
        view1 = paramView;
      } 
      OneShotPreDrawListener.add(paramViewGroup, new Runnable() {
            public void run() {
              FragmentTransition.callSharedElementStartEnd(inFragment, outFragment, inIsPop, inSharedElements, false);
              if (epicenterView != null)
                impl.getBoundsOnScreen(epicenterView, epicenter); 
            }
          });
      return object1;
    } 
    return null;
  }
  
  private static void configureTransitionsOrdered(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap) {
    if (paramFragmentManagerImpl.mContainer.onHasView()) {
      ViewGroup viewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    } else {
      paramFragmentManagerImpl = null;
    } 
    if (paramFragmentManagerImpl == null)
      return; 
    Fragment fragment1 = paramFragmentContainerTransition.lastIn;
    Fragment fragment2 = paramFragmentContainerTransition.firstOut;
    FragmentTransitionImpl fragmentTransitionImpl = chooseImpl(fragment2, fragment1);
    if (fragmentTransitionImpl == null)
      return; 
    boolean bool1 = paramFragmentContainerTransition.lastInIsPop;
    boolean bool2 = paramFragmentContainerTransition.firstOutIsPop;
    Object object3 = getEnterTransition(fragmentTransitionImpl, fragment1, bool1);
    Object object2 = getExitTransition(fragmentTransitionImpl, fragment2, bool2);
    ArrayList arrayList3 = new ArrayList();
    ArrayList arrayList1 = new ArrayList();
    Object object4 = configureSharedElementsOrdered(fragmentTransitionImpl, paramFragmentManagerImpl, paramView, paramArrayMap, paramFragmentContainerTransition, arrayList3, arrayList1, object3, object2);
    if (object3 == null && object4 == null && object2 == null)
      return; 
    ArrayList arrayList2 = configureEnteringExitingViews(fragmentTransitionImpl, object2, fragment2, arrayList3, paramView);
    if (arrayList2 == null || arrayList2.isEmpty())
      object2 = null; 
    fragmentTransitionImpl.addTarget(object3, paramView);
    Object object1 = mergeTransitions(fragmentTransitionImpl, object3, object2, object4, fragment1, paramFragmentContainerTransition.lastInIsPop);
    if (object1 != null) {
      arrayList3 = new ArrayList();
      fragmentTransitionImpl.scheduleRemoveTargets(object1, object3, arrayList3, object2, arrayList2, object4, arrayList1);
      scheduleTargetChange(fragmentTransitionImpl, paramFragmentManagerImpl, fragment1, paramView, arrayList1, object3, arrayList3, object2, arrayList2);
      fragmentTransitionImpl.setNameOverridesOrdered(paramFragmentManagerImpl, arrayList1, paramArrayMap);
      fragmentTransitionImpl.beginDelayedTransition(paramFragmentManagerImpl, object1);
      fragmentTransitionImpl.scheduleNameReset(paramFragmentManagerImpl, arrayList1, paramArrayMap);
    } 
  }
  
  private static void configureTransitionsReordered(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap) {
    if (paramFragmentManagerImpl.mContainer.onHasView()) {
      ViewGroup viewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    } else {
      paramFragmentManagerImpl = null;
    } 
    if (paramFragmentManagerImpl == null)
      return; 
    Fragment fragment2 = paramFragmentContainerTransition.lastIn;
    Fragment fragment1 = paramFragmentContainerTransition.firstOut;
    FragmentTransitionImpl fragmentTransitionImpl = chooseImpl(fragment1, fragment2);
    if (fragmentTransitionImpl == null)
      return; 
    boolean bool1 = paramFragmentContainerTransition.lastInIsPop;
    boolean bool2 = paramFragmentContainerTransition.firstOutIsPop;
    ArrayList arrayList2 = new ArrayList();
    ArrayList arrayList3 = new ArrayList();
    Object object3 = getEnterTransition(fragmentTransitionImpl, fragment2, bool1);
    Object object2 = getExitTransition(fragmentTransitionImpl, fragment1, bool2);
    Object object4 = configureSharedElementsReordered(fragmentTransitionImpl, paramFragmentManagerImpl, paramView, paramArrayMap, paramFragmentContainerTransition, arrayList3, arrayList2, object3, object2);
    if (object3 == null && object4 == null && object2 == null)
      return; 
    Object object1 = object2;
    object2 = configureEnteringExitingViews(fragmentTransitionImpl, object1, fragment1, arrayList3, paramView);
    ArrayList arrayList1 = configureEnteringExitingViews(fragmentTransitionImpl, object3, fragment2, arrayList2, paramView);
    setViewVisibility(arrayList1, 4);
    Object object5 = mergeTransitions(fragmentTransitionImpl, object3, object1, object4, fragment2, bool1);
    if (object5 != null) {
      replaceHide(fragmentTransitionImpl, object1, fragment1, object2);
      ArrayList arrayList = fragmentTransitionImpl.prepareSetNameOverridesReordered(arrayList2);
      fragmentTransitionImpl.scheduleRemoveTargets(object5, object3, arrayList1, object1, object2, object4, arrayList2);
      fragmentTransitionImpl.beginDelayedTransition(paramFragmentManagerImpl, object5);
      fragmentTransitionImpl.setNameOverridesReordered(paramFragmentManagerImpl, arrayList3, arrayList2, arrayList, paramArrayMap);
      setViewVisibility(arrayList1, 0);
      fragmentTransitionImpl.swapSharedElementTargets(object4, arrayList3, arrayList2);
    } 
  }
  
  private static FragmentContainerTransition ensureContainer(FragmentContainerTransition paramFragmentContainerTransition, SparseArray<FragmentContainerTransition> paramSparseArray, int paramInt) {
    FragmentContainerTransition fragmentContainerTransition = paramFragmentContainerTransition;
    if (paramFragmentContainerTransition == null) {
      fragmentContainerTransition = new FragmentContainerTransition();
      paramSparseArray.put(paramInt, fragmentContainerTransition);
    } 
    return fragmentContainerTransition;
  }
  
  private static String findKeyForValue(ArrayMap<String, String> paramArrayMap, String paramString) {
    int i = paramArrayMap.size();
    for (byte b = 0; b < i; b++) {
      if (paramString.equals(paramArrayMap.valueAt(b)))
        return (String)paramArrayMap.keyAt(b); 
    } 
    return null;
  }
  
  private static Object getEnterTransition(FragmentTransitionImpl paramFragmentTransitionImpl, Fragment paramFragment, boolean paramBoolean) {
    Object object;
    if (paramFragment == null)
      return null; 
    if (paramBoolean) {
      object = paramFragment.getReenterTransition();
    } else {
      object = object.getEnterTransition();
    } 
    return paramFragmentTransitionImpl.cloneTransition(object);
  }
  
  private static Object getExitTransition(FragmentTransitionImpl paramFragmentTransitionImpl, Fragment paramFragment, boolean paramBoolean) {
    Object object;
    if (paramFragment == null)
      return null; 
    if (paramBoolean) {
      object = paramFragment.getReturnTransition();
    } else {
      object = object.getExitTransition();
    } 
    return paramFragmentTransitionImpl.cloneTransition(object);
  }
  
  private static View getInEpicenterView(ArrayMap<String, View> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, Object paramObject, boolean paramBoolean) {
    BackStackRecord backStackRecord = paramFragmentContainerTransition.lastInTransaction;
    if (paramObject != null && paramArrayMap != null && backStackRecord.mSharedElementSourceNames != null && !backStackRecord.mSharedElementSourceNames.isEmpty()) {
      String str;
      if (paramBoolean) {
        str = (String)backStackRecord.mSharedElementSourceNames.get(0);
      } else {
        str = (String)str.mSharedElementTargetNames.get(0);
      } 
      return (View)paramArrayMap.get(str);
    } 
    return null;
  }
  
  private static Object getSharedElementTransition(FragmentTransitionImpl paramFragmentTransitionImpl, Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean) {
    Object object;
    if (paramFragment1 == null || paramFragment2 == null)
      return null; 
    if (paramBoolean) {
      object = paramFragment2.getSharedElementReturnTransition();
    } else {
      object = object.getSharedElementEnterTransition();
    } 
    return paramFragmentTransitionImpl.wrapTransitionInSet(paramFragmentTransitionImpl.cloneTransition(object));
  }
  
  private static Object mergeTransitions(FragmentTransitionImpl paramFragmentTransitionImpl, Object paramObject1, Object paramObject2, Object paramObject3, Fragment paramFragment, boolean paramBoolean) {
    if (paramObject1 != null && paramObject2 != null && paramFragment != null) {
      if (paramBoolean) {
        paramBoolean = paramFragment.getAllowReturnTransitionOverlap();
      } else {
        paramBoolean = paramFragment.getAllowEnterTransitionOverlap();
      } 
    } else {
      paramBoolean = true;
    } 
    return paramBoolean ? paramFragmentTransitionImpl.mergeTransitionsTogether(paramObject2, paramObject1, paramObject3) : paramFragmentTransitionImpl.mergeTransitionsInSequence(paramObject2, paramObject1, paramObject3);
  }
  
  private static void replaceHide(FragmentTransitionImpl paramFragmentTransitionImpl, Object paramObject, Fragment paramFragment, final ArrayList<View> exitingViews) {
    if (paramFragment != null && paramObject != null && paramFragment.mAdded && paramFragment.mHidden && paramFragment.mHiddenChanged) {
      paramFragment.setHideReplaced(true);
      paramFragmentTransitionImpl.scheduleHideFragmentView(paramObject, paramFragment.getView(), paramArrayList);
      OneShotPreDrawListener.add(paramFragment.mContainer, new Runnable() {
            public void run() { FragmentTransition.setViewVisibility(exitingViews, 4); }
          });
    } 
  }
  
  private static FragmentTransitionImpl resolveSupportImpl() {
    try {
      return (FragmentTransitionImpl)Class.forName("android.support.transition.FragmentTransitionSupport").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private static void retainValues(ArrayMap<String, String> paramArrayMap1, ArrayMap<String, View> paramArrayMap2) {
    for (int i = paramArrayMap1.size() - 1; i >= 0; i--) {
      if (!paramArrayMap2.containsKey((String)paramArrayMap1.valueAt(i)))
        paramArrayMap1.removeAt(i); 
    } 
  }
  
  private static void scheduleTargetChange(final FragmentTransitionImpl impl, ViewGroup paramViewGroup, final Fragment inFragment, final View nonExistentView, final ArrayList<View> sharedElementsIn, final Object enterTransition, final ArrayList<View> enteringViews, final Object exitTransition, final ArrayList<View> exitingViews) { OneShotPreDrawListener.add(paramViewGroup, new Runnable() {
          public void run() {
            if (enterTransition != null) {
              impl.removeTarget(enterTransition, nonExistentView);
              ArrayList arrayList = FragmentTransition.configureEnteringExitingViews(impl, enterTransition, inFragment, sharedElementsIn, nonExistentView);
              enteringViews.addAll(arrayList);
            } 
            if (exitingViews != null) {
              if (exitTransition != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(nonExistentView);
                impl.replaceTargets(exitTransition, exitingViews, arrayList);
              } 
              exitingViews.clear();
              exitingViews.add(nonExistentView);
            } 
          }
        }); }
  
  private static void setOutEpicenter(FragmentTransitionImpl paramFragmentTransitionImpl, Object paramObject1, Object paramObject2, ArrayMap<String, View> paramArrayMap, boolean paramBoolean, BackStackRecord paramBackStackRecord) {
    if (paramBackStackRecord.mSharedElementSourceNames != null && !paramBackStackRecord.mSharedElementSourceNames.isEmpty()) {
      String str;
      if (paramBoolean) {
        str = (String)paramBackStackRecord.mSharedElementTargetNames.get(0);
      } else {
        str = (String)str.mSharedElementSourceNames.get(0);
      } 
      View view = (View)paramArrayMap.get(str);
      paramFragmentTransitionImpl.setEpicenter(paramObject1, view);
      if (paramObject2 != null)
        paramFragmentTransitionImpl.setEpicenter(paramObject2, view); 
    } 
  }
  
  private static void setViewVisibility(ArrayList<View> paramArrayList, int paramInt) {
    if (paramArrayList == null)
      return; 
    for (int i = paramArrayList.size() - 1; i >= 0; i--)
      ((View)paramArrayList.get(i)).setVisibility(paramInt); 
  }
  
  static void startTransitions(FragmentManagerImpl paramFragmentManagerImpl, ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramFragmentManagerImpl.mCurState < 1)
      return; 
    SparseArray sparseArray = new SparseArray();
    int i;
    for (i = paramInt1; i < paramInt2; i++) {
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(i);
      if (((Boolean)paramArrayList2.get(i)).booleanValue()) {
        calculatePopFragments(backStackRecord, sparseArray, paramBoolean);
      } else {
        calculateFragments(backStackRecord, sparseArray, paramBoolean);
      } 
    } 
    if (sparseArray.size() != 0) {
      View view = new View(paramFragmentManagerImpl.mHost.getContext());
      int j = sparseArray.size();
      for (i = 0; i < j; i++) {
        int k = sparseArray.keyAt(i);
        ArrayMap arrayMap = calculateNameOverrides(k, paramArrayList1, paramArrayList2, paramInt1, paramInt2);
        FragmentContainerTransition fragmentContainerTransition = (FragmentContainerTransition)sparseArray.valueAt(i);
        if (paramBoolean) {
          configureTransitionsReordered(paramFragmentManagerImpl, k, fragmentContainerTransition, view, arrayMap);
        } else {
          configureTransitionsOrdered(paramFragmentManagerImpl, k, fragmentContainerTransition, view, arrayMap);
        } 
      } 
    } 
  }
  
  static boolean supportsTransition() { return (PLATFORM_IMPL != null || SUPPORT_IMPL != null); }
  
  static class FragmentContainerTransition {
    public Fragment firstOut;
    
    public boolean firstOutIsPop;
    
    public BackStackRecord firstOutTransaction;
    
    public Fragment lastIn;
    
    public boolean lastInIsPop;
    
    public BackStackRecord lastInTransaction;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/FragmentTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */