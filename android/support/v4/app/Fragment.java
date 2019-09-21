package android.support.v4.app;

import android.animation.Animator;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class Fragment implements ComponentCallbacks, View.OnCreateContextMenuListener, LifecycleOwner, ViewModelStoreOwner {
  static final int ACTIVITY_CREATED = 2;
  
  static final int CREATED = 1;
  
  static final int INITIALIZING = 0;
  
  static final int RESUMED = 5;
  
  static final int STARTED = 4;
  
  static final int STOPPED = 3;
  
  static final Object USE_DEFAULT_TRANSITION;
  
  private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap();
  
  boolean mAdded;
  
  AnimationInfo mAnimationInfo;
  
  Bundle mArguments;
  
  int mBackStackNesting;
  
  boolean mCalled;
  
  FragmentManagerImpl mChildFragmentManager;
  
  FragmentManagerNonConfig mChildNonConfig;
  
  ViewGroup mContainer;
  
  int mContainerId;
  
  boolean mDeferStart;
  
  boolean mDetached;
  
  int mFragmentId;
  
  FragmentManagerImpl mFragmentManager;
  
  boolean mFromLayout;
  
  boolean mHasMenu;
  
  boolean mHidden;
  
  boolean mHiddenChanged;
  
  FragmentHostCallback mHost;
  
  boolean mInLayout;
  
  int mIndex = -1;
  
  View mInnerView;
  
  boolean mIsCreated;
  
  boolean mIsNewlyAdded;
  
  LayoutInflater mLayoutInflater;
  
  LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
  
  LoaderManagerImpl mLoaderManager;
  
  boolean mMenuVisible = true;
  
  Fragment mParentFragment;
  
  boolean mPerformedCreateView;
  
  float mPostponedAlpha;
  
  boolean mRemoving;
  
  boolean mRestored;
  
  boolean mRetainInstance;
  
  boolean mRetaining;
  
  Bundle mSavedFragmentState;
  
  @Nullable
  Boolean mSavedUserVisibleHint;
  
  SparseArray<Parcelable> mSavedViewState;
  
  int mState = 0;
  
  String mTag;
  
  Fragment mTarget;
  
  int mTargetIndex = -1;
  
  int mTargetRequestCode;
  
  boolean mUserVisibleHint = true;
  
  View mView;
  
  ViewModelStore mViewModelStore;
  
  String mWho;
  
  static  {
    USE_DEFAULT_TRANSITION = new Object();
  }
  
  private void callStartTransitionListener() {
    OnStartEnterTransitionListener onStartEnterTransitionListener;
    if (this.mAnimationInfo == null) {
      onStartEnterTransitionListener = null;
    } else {
      this.mAnimationInfo.mEnterTransitionPostponed = false;
      onStartEnterTransitionListener = this.mAnimationInfo.mStartEnterTransitionListener;
      this.mAnimationInfo.mStartEnterTransitionListener = null;
    } 
    if (onStartEnterTransitionListener != null)
      onStartEnterTransitionListener.onStartEnterTransition(); 
  }
  
  private AnimationInfo ensureAnimationInfo() {
    if (this.mAnimationInfo == null)
      this.mAnimationInfo = new AnimationInfo(); 
    return this.mAnimationInfo;
  }
  
  public static Fragment instantiate(Context paramContext, String paramString) { return instantiate(paramContext, paramString, null); }
  
  public static Fragment instantiate(Context paramContext, String paramString, @Nullable Bundle paramBundle) {
    try {
      Class clazz2 = (Class)sClassMap.get(paramString);
      Class clazz1 = clazz2;
      if (clazz2 == null) {
        clazz1 = paramContext.getClassLoader().loadClass(paramString);
        sClassMap.put(paramString, clazz1);
      } 
      Fragment fragment = (Fragment)clazz1.getConstructor(new Class[0]).newInstance(new Object[0]);
      if (paramBundle != null) {
        paramBundle.setClassLoader(fragment.getClass().getClassLoader());
        fragment.setArguments(paramBundle);
      } 
      return fragment;
    } catch (ClassNotFoundException paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Unable to instantiate fragment ");
      stringBuilder.append(paramString);
      stringBuilder.append(": make sure class name exists, is public, and has an");
      stringBuilder.append(" empty constructor that is public");
      throw new InstantiationException(stringBuilder.toString(), paramContext);
    } catch (InstantiationException paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Unable to instantiate fragment ");
      stringBuilder.append(paramString);
      stringBuilder.append(": make sure class name exists, is public, and has an");
      stringBuilder.append(" empty constructor that is public");
      throw new InstantiationException(stringBuilder.toString(), paramContext);
    } catch (IllegalAccessException paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Unable to instantiate fragment ");
      stringBuilder.append(paramString);
      stringBuilder.append(": make sure class name exists, is public, and has an");
      stringBuilder.append(" empty constructor that is public");
      throw new InstantiationException(stringBuilder.toString(), paramContext);
    } catch (NoSuchMethodException paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Unable to instantiate fragment ");
      stringBuilder.append(paramString);
      stringBuilder.append(": could not find Fragment constructor");
      throw new InstantiationException(stringBuilder.toString(), paramContext);
    } catch (InvocationTargetException paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Unable to instantiate fragment ");
      stringBuilder.append(paramString);
      stringBuilder.append(": calling Fragment constructor caused an exception");
      throw new InstantiationException(stringBuilder.toString(), paramContext);
    } 
  }
  
  static boolean isSupportFragmentClass(Context paramContext, String paramString) {
    try {
      Class clazz2 = (Class)sClassMap.get(paramString);
      Class clazz1 = clazz2;
      if (clazz2 == null) {
        clazz1 = paramContext.getClassLoader().loadClass(paramString);
        sClassMap.put(paramString, clazz1);
      } 
      return Fragment.class.isAssignableFrom(clazz1);
    } catch (ClassNotFoundException paramContext) {
      return false;
    } 
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFragmentId=#");
    paramPrintWriter.print(Integer.toHexString(this.mFragmentId));
    paramPrintWriter.print(" mContainerId=#");
    paramPrintWriter.print(Integer.toHexString(this.mContainerId));
    paramPrintWriter.print(" mTag=");
    paramPrintWriter.println(this.mTag);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mState=");
    paramPrintWriter.print(this.mState);
    paramPrintWriter.print(" mIndex=");
    paramPrintWriter.print(this.mIndex);
    paramPrintWriter.print(" mWho=");
    paramPrintWriter.print(this.mWho);
    paramPrintWriter.print(" mBackStackNesting=");
    paramPrintWriter.println(this.mBackStackNesting);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAdded=");
    paramPrintWriter.print(this.mAdded);
    paramPrintWriter.print(" mRemoving=");
    paramPrintWriter.print(this.mRemoving);
    paramPrintWriter.print(" mFromLayout=");
    paramPrintWriter.print(this.mFromLayout);
    paramPrintWriter.print(" mInLayout=");
    paramPrintWriter.println(this.mInLayout);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHidden=");
    paramPrintWriter.print(this.mHidden);
    paramPrintWriter.print(" mDetached=");
    paramPrintWriter.print(this.mDetached);
    paramPrintWriter.print(" mMenuVisible=");
    paramPrintWriter.print(this.mMenuVisible);
    paramPrintWriter.print(" mHasMenu=");
    paramPrintWriter.println(this.mHasMenu);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRetainInstance=");
    paramPrintWriter.print(this.mRetainInstance);
    paramPrintWriter.print(" mRetaining=");
    paramPrintWriter.print(this.mRetaining);
    paramPrintWriter.print(" mUserVisibleHint=");
    paramPrintWriter.println(this.mUserVisibleHint);
    if (this.mFragmentManager != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mFragmentManager=");
      paramPrintWriter.println(this.mFragmentManager);
    } 
    if (this.mHost != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mHost=");
      paramPrintWriter.println(this.mHost);
    } 
    if (this.mParentFragment != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mParentFragment=");
      paramPrintWriter.println(this.mParentFragment);
    } 
    if (this.mArguments != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mArguments=");
      paramPrintWriter.println(this.mArguments);
    } 
    if (this.mSavedFragmentState != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedFragmentState=");
      paramPrintWriter.println(this.mSavedFragmentState);
    } 
    if (this.mSavedViewState != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedViewState=");
      paramPrintWriter.println(this.mSavedViewState);
    } 
    if (this.mTarget != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTarget=");
      paramPrintWriter.print(this.mTarget);
      paramPrintWriter.print(" mTargetRequestCode=");
      paramPrintWriter.println(this.mTargetRequestCode);
    } 
    if (getNextAnim() != 0) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAnim=");
      paramPrintWriter.println(getNextAnim());
    } 
    if (this.mContainer != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mContainer=");
      paramPrintWriter.println(this.mContainer);
    } 
    if (this.mView != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mView=");
      paramPrintWriter.println(this.mView);
    } 
    if (this.mInnerView != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInnerView=");
      paramPrintWriter.println(this.mView);
    } 
    if (getAnimatingAway() != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimatingAway=");
      paramPrintWriter.println(getAnimatingAway());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStateAfterAnimating=");
      paramPrintWriter.println(getStateAfterAnimating());
    } 
    if (this.mLoaderManager != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Loader Manager:");
      LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append("  ");
      loaderManagerImpl.dump(stringBuilder.toString(), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    } 
    if (this.mChildFragmentManager != null) {
      paramPrintWriter.print(paramString);
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("Child ");
      stringBuilder1.append(this.mChildFragmentManager);
      stringBuilder1.append(":");
      paramPrintWriter.println(stringBuilder1.toString());
      FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append(paramString);
      stringBuilder2.append("  ");
      fragmentManagerImpl.dump(stringBuilder2.toString(), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    } 
  }
  
  public final boolean equals(Object paramObject) { return super.equals(paramObject); }
  
  Fragment findFragmentByWho(String paramString) { return paramString.equals(this.mWho) ? this : ((this.mChildFragmentManager != null) ? this.mChildFragmentManager.findFragmentByWho(paramString) : null); }
  
  @Nullable
  public final FragmentActivity getActivity() { return (this.mHost == null) ? null : (FragmentActivity)this.mHost.getActivity(); }
  
  public boolean getAllowEnterTransitionOverlap() { return (this.mAnimationInfo == null || this.mAnimationInfo.mAllowEnterTransitionOverlap == null) ? true : this.mAnimationInfo.mAllowEnterTransitionOverlap.booleanValue(); }
  
  public boolean getAllowReturnTransitionOverlap() { return (this.mAnimationInfo == null || this.mAnimationInfo.mAllowReturnTransitionOverlap == null) ? true : this.mAnimationInfo.mAllowReturnTransitionOverlap.booleanValue(); }
  
  View getAnimatingAway() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mAnimatingAway; }
  
  Animator getAnimator() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mAnimator; }
  
  @Nullable
  public final Bundle getArguments() { return this.mArguments; }
  
  @NonNull
  public final FragmentManager getChildFragmentManager() {
    if (this.mChildFragmentManager == null) {
      instantiateChildFragmentManager();
      if (this.mState >= 5) {
        this.mChildFragmentManager.dispatchResume();
      } else if (this.mState >= 4) {
        this.mChildFragmentManager.dispatchStart();
      } else if (this.mState >= 2) {
        this.mChildFragmentManager.dispatchActivityCreated();
      } else if (this.mState >= 1) {
        this.mChildFragmentManager.dispatchCreate();
      } 
    } 
    return this.mChildFragmentManager;
  }
  
  @Nullable
  public Context getContext() { return (this.mHost == null) ? null : this.mHost.getContext(); }
  
  @Nullable
  public Object getEnterTransition() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mEnterTransition; }
  
  SharedElementCallback getEnterTransitionCallback() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mEnterTransitionCallback; }
  
  @Nullable
  public Object getExitTransition() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mExitTransition; }
  
  SharedElementCallback getExitTransitionCallback() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mExitTransitionCallback; }
  
  @Nullable
  public final FragmentManager getFragmentManager() { return this.mFragmentManager; }
  
  @Nullable
  public final Object getHost() { return (this.mHost == null) ? null : this.mHost.onGetHost(); }
  
  public final int getId() { return this.mFragmentId; }
  
  public final LayoutInflater getLayoutInflater() { return (this.mLayoutInflater == null) ? performGetLayoutInflater(null) : this.mLayoutInflater; }
  
  @Deprecated
  @NonNull
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public LayoutInflater getLayoutInflater(@Nullable Bundle paramBundle) {
    if (this.mHost == null)
      throw new IllegalStateException("onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager."); 
    LayoutInflater layoutInflater = this.mHost.onGetLayoutInflater();
    getChildFragmentManager();
    LayoutInflaterCompat.setFactory2(layoutInflater, this.mChildFragmentManager.getLayoutInflaterFactory());
    return layoutInflater;
  }
  
  public Lifecycle getLifecycle() { return this.mLifecycleRegistry; }
  
  public LoaderManager getLoaderManager() {
    if (this.mLoaderManager != null)
      return this.mLoaderManager; 
    this.mLoaderManager = new LoaderManagerImpl(this, getViewModelStore());
    return this.mLoaderManager;
  }
  
  int getNextAnim() { return (this.mAnimationInfo == null) ? 0 : this.mAnimationInfo.mNextAnim; }
  
  int getNextTransition() { return (this.mAnimationInfo == null) ? 0 : this.mAnimationInfo.mNextTransition; }
  
  int getNextTransitionStyle() { return (this.mAnimationInfo == null) ? 0 : this.mAnimationInfo.mNextTransitionStyle; }
  
  @Nullable
  public final Fragment getParentFragment() { return this.mParentFragment; }
  
  public Object getReenterTransition() { return (this.mAnimationInfo == null) ? null : ((this.mAnimationInfo.mReenterTransition == USE_DEFAULT_TRANSITION) ? getExitTransition() : this.mAnimationInfo.mReenterTransition); }
  
  @NonNull
  public final Resources getResources() { return requireContext().getResources(); }
  
  public final boolean getRetainInstance() { return this.mRetainInstance; }
  
  @Nullable
  public Object getReturnTransition() { return (this.mAnimationInfo == null) ? null : ((this.mAnimationInfo.mReturnTransition == USE_DEFAULT_TRANSITION) ? getEnterTransition() : this.mAnimationInfo.mReturnTransition); }
  
  @Nullable
  public Object getSharedElementEnterTransition() { return (this.mAnimationInfo == null) ? null : this.mAnimationInfo.mSharedElementEnterTransition; }
  
  @Nullable
  public Object getSharedElementReturnTransition() { return (this.mAnimationInfo == null) ? null : ((this.mAnimationInfo.mSharedElementReturnTransition == USE_DEFAULT_TRANSITION) ? getSharedElementEnterTransition() : this.mAnimationInfo.mSharedElementReturnTransition); }
  
  int getStateAfterAnimating() { return (this.mAnimationInfo == null) ? 0 : this.mAnimationInfo.mStateAfterAnimating; }
  
  @NonNull
  public final String getString(@StringRes int paramInt) { return getResources().getString(paramInt); }
  
  @NonNull
  public final String getString(@StringRes int paramInt, Object... paramVarArgs) { return getResources().getString(paramInt, paramVarArgs); }
  
  @Nullable
  public final String getTag() { return this.mTag; }
  
  @Nullable
  public final Fragment getTargetFragment() { return this.mTarget; }
  
  public final int getTargetRequestCode() { return this.mTargetRequestCode; }
  
  @NonNull
  public final CharSequence getText(@StringRes int paramInt) { return getResources().getText(paramInt); }
  
  public boolean getUserVisibleHint() { return this.mUserVisibleHint; }
  
  @Nullable
  public View getView() { return this.mView; }
  
  @NonNull
  public ViewModelStore getViewModelStore() {
    if (getContext() == null)
      throw new IllegalStateException("Can't access ViewModels from detached fragment"); 
    if (this.mViewModelStore == null)
      this.mViewModelStore = new ViewModelStore(); 
    return this.mViewModelStore;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public final boolean hasOptionsMenu() { return this.mHasMenu; }
  
  public final int hashCode() { return super.hashCode(); }
  
  void initState() {
    this.mIndex = -1;
    this.mWho = null;
    this.mAdded = false;
    this.mRemoving = false;
    this.mFromLayout = false;
    this.mInLayout = false;
    this.mRestored = false;
    this.mBackStackNesting = 0;
    this.mFragmentManager = null;
    this.mChildFragmentManager = null;
    this.mHost = null;
    this.mFragmentId = 0;
    this.mContainerId = 0;
    this.mTag = null;
    this.mHidden = false;
    this.mDetached = false;
    this.mRetaining = false;
  }
  
  void instantiateChildFragmentManager() {
    if (this.mHost == null)
      throw new IllegalStateException("Fragment has not been attached yet."); 
    this.mChildFragmentManager = new FragmentManagerImpl();
    this.mChildFragmentManager.attachController(this.mHost, new FragmentContainer() {
          public Fragment instantiate(Context param1Context, String param1String, Bundle param1Bundle) { return Fragment.this.mHost.instantiate(param1Context, param1String, param1Bundle); }
          
          @Nullable
          public View onFindViewById(int param1Int) {
            if (Fragment.this.mView == null)
              throw new IllegalStateException("Fragment does not have a view"); 
            return Fragment.this.mView.findViewById(param1Int);
          }
          
          public boolean onHasView() { return (Fragment.this.mView != null); }
        }this);
  }
  
  public final boolean isAdded() { return (this.mHost != null && this.mAdded); }
  
  public final boolean isDetached() { return this.mDetached; }
  
  public final boolean isHidden() { return this.mHidden; }
  
  boolean isHideReplaced() { return (this.mAnimationInfo == null) ? false : this.mAnimationInfo.mIsHideReplaced; }
  
  final boolean isInBackStack() { return (this.mBackStackNesting > 0); }
  
  public final boolean isInLayout() { return this.mInLayout; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public final boolean isMenuVisible() { return this.mMenuVisible; }
  
  boolean isPostponed() { return (this.mAnimationInfo == null) ? false : this.mAnimationInfo.mEnterTransitionPostponed; }
  
  public final boolean isRemoving() { return this.mRemoving; }
  
  public final boolean isResumed() { return (this.mState >= 5); }
  
  public final boolean isStateSaved() { return (this.mFragmentManager == null) ? false : this.mFragmentManager.isStateSaved(); }
  
  public final boolean isVisible() { return (isAdded() && !isHidden() && this.mView != null && this.mView.getWindowToken() != null && this.mView.getVisibility() == 0); }
  
  void noteStateNotSaved() {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.noteStateNotSaved(); 
  }
  
  @CallSuper
  public void onActivityCreated(@Nullable Bundle paramBundle) { this.mCalled = true; }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  @Deprecated
  @CallSuper
  public void onAttach(Activity paramActivity) { this.mCalled = true; }
  
  @CallSuper
  public void onAttach(Context paramContext) {
    Activity activity;
    this.mCalled = true;
    if (this.mHost == null) {
      paramContext = null;
    } else {
      activity = this.mHost.getActivity();
    } 
    if (activity != null) {
      this.mCalled = false;
      onAttach(activity);
    } 
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  @CallSuper
  public void onConfigurationChanged(Configuration paramConfiguration) { this.mCalled = true; }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem) { return false; }
  
  @CallSuper
  public void onCreate(@Nullable Bundle paramBundle) {
    this.mCalled = true;
    restoreChildFragmentState(paramBundle);
    if (this.mChildFragmentManager != null && !this.mChildFragmentManager.isStateAtLeast(1))
      this.mChildFragmentManager.dispatchCreate(); 
  }
  
  public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2) { return null; }
  
  public Animator onCreateAnimator(int paramInt1, boolean paramBoolean, int paramInt2) { return null; }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo) { getActivity().onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo); }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {}
  
  @Nullable
  public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) { return null; }
  
  @CallSuper
  public void onDestroy() {
    this.mCalled = true;
    if (this.mViewModelStore != null && !this.mHost.mFragmentManager.mStateSaved)
      this.mViewModelStore.clear(); 
  }
  
  public void onDestroyOptionsMenu() {}
  
  @CallSuper
  public void onDestroyView() { this.mCalled = true; }
  
  @CallSuper
  public void onDetach() { this.mCalled = true; }
  
  @NonNull
  public LayoutInflater onGetLayoutInflater(@Nullable Bundle paramBundle) { return getLayoutInflater(paramBundle); }
  
  public void onHiddenChanged(boolean paramBoolean) {}
  
  @Deprecated
  @CallSuper
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle) { this.mCalled = true; }
  
  @CallSuper
  public void onInflate(Context paramContext, AttributeSet paramAttributeSet, Bundle paramBundle) {
    Activity activity;
    this.mCalled = true;
    if (this.mHost == null) {
      paramContext = null;
    } else {
      activity = this.mHost.getActivity();
    } 
    if (activity != null) {
      this.mCalled = false;
      onInflate(activity, paramAttributeSet, paramBundle);
    } 
  }
  
  @CallSuper
  public void onLowMemory() { this.mCalled = true; }
  
  public void onMultiWindowModeChanged(boolean paramBoolean) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem) { return false; }
  
  public void onOptionsMenuClosed(Menu paramMenu) {}
  
  @CallSuper
  public void onPause() { this.mCalled = true; }
  
  public void onPictureInPictureModeChanged(boolean paramBoolean) {}
  
  public void onPrepareOptionsMenu(Menu paramMenu) {}
  
  public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt) {}
  
  @CallSuper
  public void onResume() { this.mCalled = true; }
  
  public void onSaveInstanceState(@NonNull Bundle paramBundle) {}
  
  @CallSuper
  public void onStart() { this.mCalled = true; }
  
  @CallSuper
  public void onStop() { this.mCalled = true; }
  
  public void onViewCreated(@NonNull View paramView, @Nullable Bundle paramBundle) {}
  
  @CallSuper
  public void onViewStateRestored(@Nullable Bundle paramBundle) { this.mCalled = true; }
  
  @Nullable
  FragmentManager peekChildFragmentManager() { return this.mChildFragmentManager; }
  
  void performActivityCreated(Bundle paramBundle) {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.noteStateNotSaved(); 
    this.mState = 2;
    this.mCalled = false;
    onActivityCreated(paramBundle);
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onActivityCreated()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchActivityCreated(); 
  }
  
  void performConfigurationChanged(Configuration paramConfiguration) {
    onConfigurationChanged(paramConfiguration);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchConfigurationChanged(paramConfiguration); 
  }
  
  boolean performContextItemSelected(MenuItem paramMenuItem) {
    if (!this.mHidden) {
      if (onContextItemSelected(paramMenuItem))
        return true; 
      if (this.mChildFragmentManager != null && this.mChildFragmentManager.dispatchContextItemSelected(paramMenuItem))
        return true; 
    } 
    return false;
  }
  
  void performCreate(Bundle paramBundle) {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.noteStateNotSaved(); 
    this.mState = 1;
    this.mCalled = false;
    onCreate(paramBundle);
    this.mIsCreated = true;
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onCreate()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
  }
  
  boolean performCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {
    boolean bool2 = this.mHidden;
    boolean bool1 = false;
    boolean bool = false;
    if (!bool2) {
      bool2 = bool;
      if (this.mHasMenu) {
        bool2 = bool;
        if (this.mMenuVisible) {
          bool2 = true;
          onCreateOptionsMenu(paramMenu, paramMenuInflater);
        } 
      } 
      bool1 = bool2;
      if (this.mChildFragmentManager != null)
        bool1 = bool2 | this.mChildFragmentManager.dispatchCreateOptionsMenu(paramMenu, paramMenuInflater); 
    } 
    return bool1;
  }
  
  View performCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.noteStateNotSaved(); 
    this.mPerformedCreateView = true;
    return onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  void performDestroy() {
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchDestroy(); 
    this.mState = 0;
    this.mCalled = false;
    this.mIsCreated = false;
    onDestroy();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onDestroy()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    this.mChildFragmentManager = null;
  }
  
  void performDestroyView() {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchDestroyView(); 
    this.mState = 1;
    this.mCalled = false;
    onDestroyView();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onDestroyView()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    if (this.mLoaderManager != null)
      this.mLoaderManager.markForRedelivery(); 
    this.mPerformedCreateView = false;
  }
  
  void performDetach() {
    this.mCalled = false;
    onDetach();
    this.mLayoutInflater = null;
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onDetach()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    if (this.mChildFragmentManager != null) {
      if (!this.mRetaining) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Child FragmentManager of ");
        stringBuilder.append(this);
        stringBuilder.append(" was not ");
        stringBuilder.append(" destroyed and this fragment is not retaining instance");
        throw new IllegalStateException(stringBuilder.toString());
      } 
      this.mChildFragmentManager.dispatchDestroy();
      this.mChildFragmentManager = null;
    } 
  }
  
  @NonNull
  LayoutInflater performGetLayoutInflater(@Nullable Bundle paramBundle) {
    this.mLayoutInflater = onGetLayoutInflater(paramBundle);
    return this.mLayoutInflater;
  }
  
  void performLowMemory() {
    onLowMemory();
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchLowMemory(); 
  }
  
  void performMultiWindowModeChanged(boolean paramBoolean) {
    onMultiWindowModeChanged(paramBoolean);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchMultiWindowModeChanged(paramBoolean); 
  }
  
  boolean performOptionsItemSelected(MenuItem paramMenuItem) {
    if (!this.mHidden) {
      if (this.mHasMenu && this.mMenuVisible && onOptionsItemSelected(paramMenuItem))
        return true; 
      if (this.mChildFragmentManager != null && this.mChildFragmentManager.dispatchOptionsItemSelected(paramMenuItem))
        return true; 
    } 
    return false;
  }
  
  void performOptionsMenuClosed(Menu paramMenu) {
    if (!this.mHidden) {
      if (this.mHasMenu && this.mMenuVisible)
        onOptionsMenuClosed(paramMenu); 
      if (this.mChildFragmentManager != null)
        this.mChildFragmentManager.dispatchOptionsMenuClosed(paramMenu); 
    } 
  }
  
  void performPause() {
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchPause(); 
    this.mState = 4;
    this.mCalled = false;
    onPause();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onPause()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
  }
  
  void performPictureInPictureModeChanged(boolean paramBoolean) {
    onPictureInPictureModeChanged(paramBoolean);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchPictureInPictureModeChanged(paramBoolean); 
  }
  
  boolean performPrepareOptionsMenu(Menu paramMenu) {
    boolean bool2 = this.mHidden;
    boolean bool1 = false;
    boolean bool = false;
    if (!bool2) {
      bool2 = bool;
      if (this.mHasMenu) {
        bool2 = bool;
        if (this.mMenuVisible) {
          bool2 = true;
          onPrepareOptionsMenu(paramMenu);
        } 
      } 
      bool1 = bool2;
      if (this.mChildFragmentManager != null)
        bool1 = bool2 | this.mChildFragmentManager.dispatchPrepareOptionsMenu(paramMenu); 
    } 
    return bool1;
  }
  
  void performReallyStop() {
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchReallyStop(); 
    this.mState = 2;
  }
  
  void performResume() {
    if (this.mChildFragmentManager != null) {
      this.mChildFragmentManager.noteStateNotSaved();
      this.mChildFragmentManager.execPendingActions();
    } 
    this.mState = 5;
    this.mCalled = false;
    onResume();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onResume()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    if (this.mChildFragmentManager != null) {
      this.mChildFragmentManager.dispatchResume();
      this.mChildFragmentManager.execPendingActions();
    } 
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
  }
  
  void performSaveInstanceState(Bundle paramBundle) {
    onSaveInstanceState(paramBundle);
    if (this.mChildFragmentManager != null) {
      Parcelable parcelable = this.mChildFragmentManager.saveAllState();
      if (parcelable != null)
        paramBundle.putParcelable("android:support:fragments", parcelable); 
    } 
  }
  
  void performStart() {
    if (this.mChildFragmentManager != null) {
      this.mChildFragmentManager.noteStateNotSaved();
      this.mChildFragmentManager.execPendingActions();
    } 
    this.mState = 4;
    this.mCalled = false;
    onStart();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onStart()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchStart(); 
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
  }
  
  void performStop() {
    this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    if (this.mChildFragmentManager != null)
      this.mChildFragmentManager.dispatchStop(); 
    this.mState = 3;
    this.mCalled = false;
    onStop();
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onStop()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
  }
  
  public void postponeEnterTransition() { (ensureAnimationInfo()).mEnterTransitionPostponed = true; }
  
  public void registerForContextMenu(View paramView) { paramView.setOnCreateContextMenuListener(this); }
  
  public final void requestPermissions(@NonNull String[] paramArrayOfString, int paramInt) {
    StringBuilder stringBuilder;
    if (this.mHost == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to Activity");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    this.mHost.onRequestPermissionsFromFragment(this, stringBuilder, paramInt);
  }
  
  @NonNull
  public final FragmentActivity requireActivity() {
    StringBuilder stringBuilder = getActivity();
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to an activity.");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return stringBuilder;
  }
  
  @NonNull
  public final Context requireContext() {
    StringBuilder stringBuilder = getContext();
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to a context.");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return stringBuilder;
  }
  
  @NonNull
  public final FragmentManager requireFragmentManager() {
    StringBuilder stringBuilder = getFragmentManager();
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not associated with a fragment manager.");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return stringBuilder;
  }
  
  @NonNull
  public final Object requireHost() {
    Object object = getHost();
    if (object == null) {
      object = new StringBuilder();
      object.append("Fragment ");
      object.append(this);
      object.append(" not attached to a host.");
      throw new IllegalStateException(object.toString());
    } 
    return object;
  }
  
  void restoreChildFragmentState(@Nullable Bundle paramBundle) {
    if (paramBundle != null) {
      Parcelable parcelable = paramBundle.getParcelable("android:support:fragments");
      if (parcelable != null) {
        if (this.mChildFragmentManager == null)
          instantiateChildFragmentManager(); 
        this.mChildFragmentManager.restoreAllState(parcelable, this.mChildNonConfig);
        this.mChildNonConfig = null;
        this.mChildFragmentManager.dispatchCreate();
      } 
    } 
  }
  
  final void restoreViewState(Bundle paramBundle) {
    if (this.mSavedViewState != null) {
      this.mInnerView.restoreHierarchyState(this.mSavedViewState);
      this.mSavedViewState = null;
    } 
    this.mCalled = false;
    onViewStateRestored(paramBundle);
    if (!this.mCalled) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" did not call through to super.onViewStateRestored()");
      throw new SuperNotCalledException(stringBuilder.toString());
    } 
  }
  
  public void setAllowEnterTransitionOverlap(boolean paramBoolean) { AnimationInfo.access$602(ensureAnimationInfo(), Boolean.valueOf(paramBoolean)); }
  
  public void setAllowReturnTransitionOverlap(boolean paramBoolean) { AnimationInfo.access$702(ensureAnimationInfo(), Boolean.valueOf(paramBoolean)); }
  
  void setAnimatingAway(View paramView) { (ensureAnimationInfo()).mAnimatingAway = paramView; }
  
  void setAnimator(Animator paramAnimator) { (ensureAnimationInfo()).mAnimator = paramAnimator; }
  
  public void setArguments(@Nullable Bundle paramBundle) {
    if (this.mIndex >= 0 && isStateSaved())
      throw new IllegalStateException("Fragment already active and state has been saved"); 
    this.mArguments = paramBundle;
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback) { (ensureAnimationInfo()).mEnterTransitionCallback = paramSharedElementCallback; }
  
  public void setEnterTransition(@Nullable Object paramObject) { AnimationInfo.access$002(ensureAnimationInfo(), paramObject); }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback) { (ensureAnimationInfo()).mExitTransitionCallback = paramSharedElementCallback; }
  
  public void setExitTransition(@Nullable Object paramObject) { AnimationInfo.access$202(ensureAnimationInfo(), paramObject); }
  
  public void setHasOptionsMenu(boolean paramBoolean) {
    if (this.mHasMenu != paramBoolean) {
      this.mHasMenu = paramBoolean;
      if (isAdded() && !isHidden())
        this.mHost.onSupportInvalidateOptionsMenu(); 
    } 
  }
  
  void setHideReplaced(boolean paramBoolean) { (ensureAnimationInfo()).mIsHideReplaced = paramBoolean; }
  
  final void setIndex(int paramInt, Fragment paramFragment) {
    this.mIndex = paramInt;
    if (paramFragment != null) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(paramFragment.mWho);
      stringBuilder1.append(":");
      stringBuilder1.append(this.mIndex);
      this.mWho = stringBuilder1.toString();
      return;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("android:fragment:");
    stringBuilder.append(this.mIndex);
    this.mWho = stringBuilder.toString();
  }
  
  public void setInitialSavedState(@Nullable SavedState paramSavedState) {
    if (this.mIndex >= 0)
      throw new IllegalStateException("Fragment already active"); 
    if (paramSavedState != null && paramSavedState.mState != null) {
      Bundle bundle = paramSavedState.mState;
    } else {
      paramSavedState = null;
    } 
    this.mSavedFragmentState = paramSavedState;
  }
  
  public void setMenuVisibility(boolean paramBoolean) {
    if (this.mMenuVisible != paramBoolean) {
      this.mMenuVisible = paramBoolean;
      if (this.mHasMenu && isAdded() && !isHidden())
        this.mHost.onSupportInvalidateOptionsMenu(); 
    } 
  }
  
  void setNextAnim(int paramInt) {
    if (this.mAnimationInfo == null && paramInt == 0)
      return; 
    (ensureAnimationInfo()).mNextAnim = paramInt;
  }
  
  void setNextTransition(int paramInt1, int paramInt2) {
    if (this.mAnimationInfo == null && paramInt1 == 0 && paramInt2 == 0)
      return; 
    ensureAnimationInfo();
    this.mAnimationInfo.mNextTransition = paramInt1;
    this.mAnimationInfo.mNextTransitionStyle = paramInt2;
  }
  
  void setOnStartEnterTransitionListener(OnStartEnterTransitionListener paramOnStartEnterTransitionListener) {
    StringBuilder stringBuilder;
    ensureAnimationInfo();
    if (paramOnStartEnterTransitionListener == this.mAnimationInfo.mStartEnterTransitionListener)
      return; 
    if (paramOnStartEnterTransitionListener != null && this.mAnimationInfo.mStartEnterTransitionListener != null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Trying to set a replacement startPostponedEnterTransition on ");
      stringBuilder.append(this);
      throw new IllegalStateException(stringBuilder.toString());
    } 
    if (this.mAnimationInfo.mEnterTransitionPostponed)
      this.mAnimationInfo.mStartEnterTransitionListener = stringBuilder; 
    if (stringBuilder != null)
      stringBuilder.startListening(); 
  }
  
  public void setReenterTransition(@Nullable Object paramObject) { AnimationInfo.access$302(ensureAnimationInfo(), paramObject); }
  
  public void setRetainInstance(boolean paramBoolean) { this.mRetainInstance = paramBoolean; }
  
  public void setReturnTransition(@Nullable Object paramObject) { AnimationInfo.access$102(ensureAnimationInfo(), paramObject); }
  
  public void setSharedElementEnterTransition(@Nullable Object paramObject) { AnimationInfo.access$402(ensureAnimationInfo(), paramObject); }
  
  public void setSharedElementReturnTransition(@Nullable Object paramObject) { AnimationInfo.access$502(ensureAnimationInfo(), paramObject); }
  
  void setStateAfterAnimating(int paramInt) { (ensureAnimationInfo()).mStateAfterAnimating = paramInt; }
  
  public void setTargetFragment(@Nullable Fragment paramFragment, int paramInt) {
    StringBuilder stringBuilder;
    FragmentManager fragmentManager = getFragmentManager();
    if (paramFragment != null) {
      stringBuilder = paramFragment.getFragmentManager();
    } else {
      stringBuilder = null;
    } 
    if (fragmentManager != null && stringBuilder != null && fragmentManager != stringBuilder) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(paramFragment);
      stringBuilder.append(" must share the same FragmentManager to be set as a target fragment");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    Fragment fragment = paramFragment;
    while (fragment != null) {
      StringBuilder stringBuilder1;
      if (fragment == this) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Setting ");
        stringBuilder1.append(paramFragment);
        stringBuilder1.append(" as the target of ");
        stringBuilder1.append(this);
        stringBuilder1.append(" would create a target cycle");
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      Fragment fragment1 = stringBuilder1.getTargetFragment();
    } 
    this.mTarget = paramFragment;
    this.mTargetRequestCode = paramInt;
  }
  
  public void setUserVisibleHint(boolean paramBoolean) {
    if (!this.mUserVisibleHint && paramBoolean && this.mState < 4 && this.mFragmentManager != null && isAdded())
      this.mFragmentManager.performPendingDeferredStart(this); 
    this.mUserVisibleHint = paramBoolean;
    if (this.mState < 4 && !paramBoolean) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    } 
    this.mDeferStart = paramBoolean;
    if (this.mSavedFragmentState != null)
      this.mSavedUserVisibleHint = Boolean.valueOf(this.mUserVisibleHint); 
  }
  
  public boolean shouldShowRequestPermissionRationale(@NonNull String paramString) { return (this.mHost != null) ? this.mHost.onShouldShowRequestPermissionRationale(paramString) : 0; }
  
  public void startActivity(Intent paramIntent) { startActivity(paramIntent, null); }
  
  public void startActivity(Intent paramIntent, @Nullable Bundle paramBundle) {
    StringBuilder stringBuilder;
    if (this.mHost == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to Activity");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    this.mHost.onStartActivityFromFragment(this, stringBuilder, -1, paramBundle);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt) { startActivityForResult(paramIntent, paramInt, null); }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, @Nullable Bundle paramBundle) {
    StringBuilder stringBuilder;
    if (this.mHost == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to Activity");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    this.mHost.onStartActivityFromFragment(this, stringBuilder, paramInt, paramBundle);
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle) throws IntentSender.SendIntentException {
    StringBuilder stringBuilder;
    if (this.mHost == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(this);
      stringBuilder.append(" not attached to Activity");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    this.mHost.onStartIntentSenderFromFragment(this, stringBuilder, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
  }
  
  public void startPostponedEnterTransition() {
    if (this.mFragmentManager == null || this.mFragmentManager.mHost == null) {
      (ensureAnimationInfo()).mEnterTransitionPostponed = false;
      return;
    } 
    if (Looper.myLooper() != this.mFragmentManager.mHost.getHandler().getLooper()) {
      this.mFragmentManager.mHost.getHandler().postAtFrontOfQueue(new Runnable() {
            public void run() { Fragment.this.callStartTransitionListener(); }
          });
      return;
    } 
    callStartTransitionListener();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(128);
    DebugUtils.buildShortClassTag(this, stringBuilder);
    if (this.mIndex >= 0) {
      stringBuilder.append(" #");
      stringBuilder.append(this.mIndex);
    } 
    if (this.mFragmentId != 0) {
      stringBuilder.append(" id=0x");
      stringBuilder.append(Integer.toHexString(this.mFragmentId));
    } 
    if (this.mTag != null) {
      stringBuilder.append(" ");
      stringBuilder.append(this.mTag);
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public void unregisterForContextMenu(View paramView) { paramView.setOnCreateContextMenuListener(null); }
  
  static class AnimationInfo {
    private Boolean mAllowEnterTransitionOverlap;
    
    private Boolean mAllowReturnTransitionOverlap;
    
    View mAnimatingAway;
    
    Animator mAnimator;
    
    private Object mEnterTransition = null;
    
    SharedElementCallback mEnterTransitionCallback = null;
    
    boolean mEnterTransitionPostponed;
    
    private Object mExitTransition = null;
    
    SharedElementCallback mExitTransitionCallback = null;
    
    boolean mIsHideReplaced;
    
    int mNextAnim;
    
    int mNextTransition;
    
    int mNextTransitionStyle;
    
    private Object mReenterTransition = Fragment.USE_DEFAULT_TRANSITION;
    
    private Object mReturnTransition = Fragment.USE_DEFAULT_TRANSITION;
    
    private Object mSharedElementEnterTransition = null;
    
    private Object mSharedElementReturnTransition = Fragment.USE_DEFAULT_TRANSITION;
    
    Fragment.OnStartEnterTransitionListener mStartEnterTransitionListener;
    
    int mStateAfterAnimating;
  }
  
  public static class InstantiationException extends RuntimeException {
    public InstantiationException(String param1String, Exception param1Exception) { super(param1String, param1Exception); }
  }
  
  static interface OnStartEnterTransitionListener {
    void onStartEnterTransition();
    
    void startListening();
  }
  
  public static class SavedState implements Parcelable {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public Fragment.SavedState createFromParcel(Parcel param2Parcel) { return new Fragment.SavedState(param2Parcel, null); }
        
        public Fragment.SavedState[] newArray(int param2Int) { return new Fragment.SavedState[param2Int]; }
      };
    
    final Bundle mState;
    
    SavedState(Bundle param1Bundle) { this.mState = param1Bundle; }
    
    SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      this.mState = param1Parcel.readBundle();
      if (param1ClassLoader != null && this.mState != null)
        this.mState.setClassLoader(param1ClassLoader); 
    }
    
    public int describeContents() { return 0; }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { param1Parcel.writeBundle(this.mState); }
  }
  
  static final class null extends Object implements Parcelable.Creator<SavedState> {
    public Fragment.SavedState createFromParcel(Parcel param1Parcel) { return new Fragment.SavedState(param1Parcel, null); }
    
    public Fragment.SavedState[] newArray(int param1Int) { return new Fragment.SavedState[param1Int]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/Fragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */