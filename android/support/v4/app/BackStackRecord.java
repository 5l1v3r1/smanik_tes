package android.support.v4.app;

import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

final class BackStackRecord extends FragmentTransaction implements FragmentManager.BackStackEntry, FragmentManagerImpl.OpGenerator {
  static final int OP_ADD = 1;
  
  static final int OP_ATTACH = 7;
  
  static final int OP_DETACH = 6;
  
  static final int OP_HIDE = 4;
  
  static final int OP_NULL = 0;
  
  static final int OP_REMOVE = 3;
  
  static final int OP_REPLACE = 2;
  
  static final int OP_SET_PRIMARY_NAV = 8;
  
  static final int OP_SHOW = 5;
  
  static final int OP_UNSET_PRIMARY_NAV = 9;
  
  static final String TAG = "FragmentManager";
  
  boolean mAddToBackStack;
  
  boolean mAllowAddToBackStack = true;
  
  int mBreadCrumbShortTitleRes;
  
  CharSequence mBreadCrumbShortTitleText;
  
  int mBreadCrumbTitleRes;
  
  CharSequence mBreadCrumbTitleText;
  
  ArrayList<Runnable> mCommitRunnables;
  
  boolean mCommitted;
  
  int mEnterAnim;
  
  int mExitAnim;
  
  int mIndex = -1;
  
  final FragmentManagerImpl mManager;
  
  String mName;
  
  ArrayList<Op> mOps = new ArrayList();
  
  int mPopEnterAnim;
  
  int mPopExitAnim;
  
  boolean mReorderingAllowed = false;
  
  ArrayList<String> mSharedElementSourceNames;
  
  ArrayList<String> mSharedElementTargetNames;
  
  int mTransition;
  
  int mTransitionStyle;
  
  public BackStackRecord(FragmentManagerImpl paramFragmentManagerImpl) { this.mManager = paramFragmentManagerImpl; }
  
  private void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2) {
    StringBuilder stringBuilder;
    Class clazz = paramFragment.getClass();
    int i = clazz.getModifiers();
    if (clazz.isAnonymousClass() || !Modifier.isPublic(i) || (clazz.isMemberClass() && !Modifier.isStatic(i))) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(clazz.getCanonicalName());
      stringBuilder.append(" must be a public static class to be  properly recreated from");
      stringBuilder.append(" instance state.");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    stringBuilder.mFragmentManager = this.mManager;
    if (paramString != null) {
      if (stringBuilder.mTag != null && !paramString.equals(stringBuilder.mTag)) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Can't change tag of fragment ");
        stringBuilder1.append(stringBuilder);
        stringBuilder1.append(": was ");
        stringBuilder1.append(stringBuilder.mTag);
        stringBuilder1.append(" now ");
        stringBuilder1.append(paramString);
        throw new IllegalStateException(stringBuilder1.toString());
      } 
      stringBuilder.mTag = paramString;
    } 
    if (paramInt1 != 0) {
      if (paramInt1 == -1) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Can't add fragment ");
        stringBuilder1.append(stringBuilder);
        stringBuilder1.append(" with tag ");
        stringBuilder1.append(paramString);
        stringBuilder1.append(" to container view with no id");
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      if (stringBuilder.mFragmentId != 0 && stringBuilder.mFragmentId != paramInt1) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Can't change container ID of fragment ");
        stringBuilder1.append(stringBuilder);
        stringBuilder1.append(": was ");
        stringBuilder1.append(stringBuilder.mFragmentId);
        stringBuilder1.append(" now ");
        stringBuilder1.append(paramInt1);
        throw new IllegalStateException(stringBuilder1.toString());
      } 
      stringBuilder.mFragmentId = paramInt1;
      stringBuilder.mContainerId = paramInt1;
    } 
    addOp(new Op(paramInt2, stringBuilder));
  }
  
  private static boolean isFragmentPostponed(Op paramOp) {
    Fragment fragment = paramOp.fragment;
    return (fragment != null && fragment.mAdded && fragment.mView != null && !fragment.mDetached && !fragment.mHidden && fragment.isPostponed());
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment) {
    doAddOp(paramInt, paramFragment, null, 1);
    return this;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment, String paramString) {
    doAddOp(paramInt, paramFragment, paramString, 1);
    return this;
  }
  
  public FragmentTransaction add(Fragment paramFragment, String paramString) {
    doAddOp(0, paramFragment, paramString, 1);
    return this;
  }
  
  void addOp(Op paramOp) {
    this.mOps.add(paramOp);
    paramOp.enterAnim = this.mEnterAnim;
    paramOp.exitAnim = this.mExitAnim;
    paramOp.popEnterAnim = this.mPopEnterAnim;
    paramOp.popExitAnim = this.mPopExitAnim;
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString) {
    if (FragmentTransition.supportsTransition()) {
      StringBuilder stringBuilder2;
      StringBuilder stringBuilder1 = ViewCompat.getTransitionName(paramView);
      if (stringBuilder1 == null)
        throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements"); 
      if (this.mSharedElementSourceNames == null) {
        this.mSharedElementSourceNames = new ArrayList();
        this.mSharedElementTargetNames = new ArrayList();
      } else {
        if (this.mSharedElementTargetNames.contains(paramString)) {
          stringBuilder1 = new StringBuilder();
          stringBuilder1.append("A shared element with the target name '");
          stringBuilder1.append(paramString);
          stringBuilder1.append("' has already been added to the transaction.");
          throw new IllegalArgumentException(stringBuilder1.toString());
        } 
        if (this.mSharedElementSourceNames.contains(stringBuilder1)) {
          stringBuilder2 = new StringBuilder();
          stringBuilder2.append("A shared element with the source name '");
          stringBuilder2.append(stringBuilder1);
          stringBuilder2.append(" has already been added to the transaction.");
          throw new IllegalArgumentException(stringBuilder2.toString());
        } 
      } 
      this.mSharedElementSourceNames.add(stringBuilder1);
      this.mSharedElementTargetNames.add(stringBuilder2);
    } 
    return this;
  }
  
  public FragmentTransaction addToBackStack(String paramString) {
    if (!this.mAllowAddToBackStack)
      throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack."); 
    this.mAddToBackStack = true;
    this.mName = paramString;
    return this;
  }
  
  public FragmentTransaction attach(Fragment paramFragment) {
    addOp(new Op(7, paramFragment));
    return this;
  }
  
  void bumpBackStackNesting(int paramInt) {
    if (!this.mAddToBackStack)
      return; 
    if (FragmentManagerImpl.DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Bump nesting in ");
      stringBuilder.append(this);
      stringBuilder.append(" by ");
      stringBuilder.append(paramInt);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    int i = this.mOps.size();
    for (byte b = 0; b < i; b++) {
      Op op = (Op)this.mOps.get(b);
      if (op.fragment != null) {
        Fragment fragment = op.fragment;
        fragment.mBackStackNesting += paramInt;
        if (FragmentManagerImpl.DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Bump nesting of ");
          stringBuilder.append(op.fragment);
          stringBuilder.append(" to ");
          stringBuilder.append(op.fragment.mBackStackNesting);
          Log.v("FragmentManager", stringBuilder.toString());
        } 
      } 
    } 
  }
  
  public int commit() { return commitInternal(false); }
  
  public int commitAllowingStateLoss() { return commitInternal(true); }
  
  int commitInternal(boolean paramBoolean) {
    if (this.mCommitted)
      throw new IllegalStateException("commit already called"); 
    if (FragmentManagerImpl.DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Commit: ");
      stringBuilder.append(this);
      Log.v("FragmentManager", stringBuilder.toString());
      PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
      dump("  ", null, printWriter, null);
      printWriter.close();
    } 
    this.mCommitted = true;
    if (this.mAddToBackStack) {
      this.mIndex = this.mManager.allocBackStackIndex(this);
    } else {
      this.mIndex = -1;
    } 
    this.mManager.enqueueAction(this, paramBoolean);
    return this.mIndex;
  }
  
  public void commitNow() {
    disallowAddToBackStack();
    this.mManager.execSingleAction(this, false);
  }
  
  public void commitNowAllowingStateLoss() {
    disallowAddToBackStack();
    this.mManager.execSingleAction(this, true);
  }
  
  public FragmentTransaction detach(Fragment paramFragment) {
    addOp(new Op(6, paramFragment));
    return this;
  }
  
  public FragmentTransaction disallowAddToBackStack() {
    if (this.mAddToBackStack)
      throw new IllegalStateException("This transaction is already being added to the back stack"); 
    this.mAllowAddToBackStack = false;
    return this;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) { dump(paramString, paramPrintWriter, true); }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean) {
    if (paramBoolean) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(this.mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(this.mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(this.mCommitted);
      if (this.mTransition != 0) {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(this.mTransition));
        paramPrintWriter.print(" mTransitionStyle=#");
        paramPrintWriter.println(Integer.toHexString(this.mTransitionStyle));
      } 
      if (this.mEnterAnim != 0 || this.mExitAnim != 0) {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mExitAnim));
      } 
      if (this.mPopEnterAnim != 0 || this.mPopExitAnim != 0) {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(this.mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(this.mPopExitAnim));
      } 
      if (this.mBreadCrumbTitleRes != 0 || this.mBreadCrumbTitleText != null) {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(this.mBreadCrumbTitleText);
      } 
      if (this.mBreadCrumbShortTitleRes != 0 || this.mBreadCrumbShortTitleText != null) {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(this.mBreadCrumbShortTitleText);
      } 
    } 
    if (!this.mOps.isEmpty()) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append("    ");
      stringBuilder.toString();
      int i = this.mOps.size();
      byte b;
      for (b = 0; b < i; b++) {
        String str;
        Op op = (Op)this.mOps.get(b);
        switch (op.cmd) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("cmd=");
            stringBuilder.append(op.cmd);
            str = stringBuilder.toString();
            break;
          case 9:
            str = "UNSET_PRIMARY_NAV";
            break;
          case 8:
            str = "SET_PRIMARY_NAV";
            break;
          case 7:
            str = "ATTACH";
            break;
          case 6:
            str = "DETACH";
            break;
          case 5:
            str = "SHOW";
            break;
          case 4:
            str = "HIDE";
            break;
          case 3:
            str = "REMOVE";
            break;
          case 2:
            str = "REPLACE";
            break;
          case 1:
            str = "ADD";
            break;
          case 0:
            str = "NULL";
            break;
        } 
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Op #");
        paramPrintWriter.print(b);
        paramPrintWriter.print(": ");
        paramPrintWriter.print(str);
        paramPrintWriter.print(" ");
        paramPrintWriter.println(op.fragment);
        if (paramBoolean) {
          if (op.enterAnim != 0 || op.exitAnim != 0) {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("enterAnim=#");
            paramPrintWriter.print(Integer.toHexString(op.enterAnim));
            paramPrintWriter.print(" exitAnim=#");
            paramPrintWriter.println(Integer.toHexString(op.exitAnim));
          } 
          if (op.popEnterAnim != 0 || op.popExitAnim != 0) {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("popEnterAnim=#");
            paramPrintWriter.print(Integer.toHexString(op.popEnterAnim));
            paramPrintWriter.print(" popExitAnim=#");
            paramPrintWriter.println(Integer.toHexString(op.popExitAnim));
          } 
        } 
      } 
    } 
  }
  
  void executeOps() {
    int i = this.mOps.size();
    for (byte b = 0; b < i; b++) {
      Op op = (Op)this.mOps.get(b);
      StringBuilder stringBuilder = op.fragment;
      if (stringBuilder != null)
        stringBuilder.setNextTransition(this.mTransition, this.mTransitionStyle); 
      int j = op.cmd;
      if (j != 1) {
        switch (j) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown cmd: ");
            stringBuilder.append(op.cmd);
            throw new IllegalArgumentException(stringBuilder.toString());
          case 9:
            this.mManager.setPrimaryNavigationFragment(null);
            break;
          case 8:
            this.mManager.setPrimaryNavigationFragment(stringBuilder);
            break;
          case 7:
            stringBuilder.setNextAnim(op.enterAnim);
            this.mManager.attachFragment(stringBuilder);
            break;
          case 6:
            stringBuilder.setNextAnim(op.exitAnim);
            this.mManager.detachFragment(stringBuilder);
            break;
          case 5:
            stringBuilder.setNextAnim(op.enterAnim);
            this.mManager.showFragment(stringBuilder);
            break;
          case 4:
            stringBuilder.setNextAnim(op.exitAnim);
            this.mManager.hideFragment(stringBuilder);
            break;
          case 3:
            stringBuilder.setNextAnim(op.exitAnim);
            this.mManager.removeFragment(stringBuilder);
            break;
        } 
      } else {
        stringBuilder.setNextAnim(op.enterAnim);
        this.mManager.addFragment(stringBuilder, false);
      } 
      if (!this.mReorderingAllowed && op.cmd != 1 && stringBuilder != null)
        this.mManager.moveFragmentToExpectedState(stringBuilder); 
    } 
    if (!this.mReorderingAllowed)
      this.mManager.moveToState(this.mManager.mCurState, true); 
  }
  
  void executePopOps(boolean paramBoolean) {
    for (int i = this.mOps.size() - 1; i >= 0; i--) {
      Op op = (Op)this.mOps.get(i);
      StringBuilder stringBuilder = op.fragment;
      if (stringBuilder != null)
        stringBuilder.setNextTransition(FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle); 
      int j = op.cmd;
      if (j != 1) {
        switch (j) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown cmd: ");
            stringBuilder.append(op.cmd);
            throw new IllegalArgumentException(stringBuilder.toString());
          case 9:
            this.mManager.setPrimaryNavigationFragment(stringBuilder);
            break;
          case 8:
            this.mManager.setPrimaryNavigationFragment(null);
            break;
          case 7:
            stringBuilder.setNextAnim(op.popExitAnim);
            this.mManager.detachFragment(stringBuilder);
            break;
          case 6:
            stringBuilder.setNextAnim(op.popEnterAnim);
            this.mManager.attachFragment(stringBuilder);
            break;
          case 5:
            stringBuilder.setNextAnim(op.popExitAnim);
            this.mManager.hideFragment(stringBuilder);
            break;
          case 4:
            stringBuilder.setNextAnim(op.popEnterAnim);
            this.mManager.showFragment(stringBuilder);
            break;
          case 3:
            stringBuilder.setNextAnim(op.popEnterAnim);
            this.mManager.addFragment(stringBuilder, false);
            break;
        } 
      } else {
        stringBuilder.setNextAnim(op.popExitAnim);
        this.mManager.removeFragment(stringBuilder);
      } 
      if (!this.mReorderingAllowed && op.cmd != 3 && stringBuilder != null)
        this.mManager.moveFragmentToExpectedState(stringBuilder); 
    } 
    if (!this.mReorderingAllowed && paramBoolean)
      this.mManager.moveToState(this.mManager.mCurState, true); 
  }
  
  Fragment expandOps(ArrayList<Fragment> paramArrayList, Fragment paramFragment) {
    int i = 0;
    Fragment fragment;
    for (fragment = paramFragment; i < this.mOps.size(); fragment = paramFragment) {
      Fragment fragment1;
      int k;
      byte b;
      int j;
      Op op = (Op)this.mOps.get(i);
      switch (op.cmd) {
        default:
          paramFragment = fragment;
          j = i;
          break;
        case 8:
          this.mOps.add(i, new Op(9, fragment));
          j = i + 1;
          paramFragment = op.fragment;
          break;
        case 3:
        case 6:
          paramArrayList.remove(op.fragment);
          paramFragment = fragment;
          j = i;
          if (op.fragment == fragment) {
            this.mOps.add(i, new Op(9, op.fragment));
            j = i + 1;
            paramFragment = null;
          } 
          break;
        case 2:
          fragment1 = op.fragment;
          k = fragment1.mContainerId;
          j = paramArrayList.size() - 1;
          paramFragment = fragment;
          for (b = 0; j >= 0; b = b2) {
            Fragment fragment2 = (Fragment)paramArrayList.get(j);
            byte b1 = i;
            fragment = paramFragment;
            byte b2 = b;
            if (fragment2.mContainerId == k)
              if (fragment2 == fragment1) {
                b2 = 1;
                b1 = i;
                fragment = paramFragment;
              } else {
                b1 = i;
                fragment = paramFragment;
                if (fragment2 == paramFragment) {
                  this.mOps.add(i, new Op(9, fragment2));
                  b1 = i + 1;
                  fragment = null;
                } 
                Op op1 = new Op(3, fragment2);
                op1.enterAnim = op.enterAnim;
                op1.popEnterAnim = op.popEnterAnim;
                op1.exitAnim = op.exitAnim;
                op1.popExitAnim = op.popExitAnim;
                this.mOps.add(b1, op1);
                paramArrayList.remove(fragment2);
                b1++;
                b2 = b;
              }  
            j--;
            i = b1;
            paramFragment = fragment;
          } 
          if (b != 0) {
            this.mOps.remove(i);
            i--;
          } else {
            op.cmd = 1;
            paramArrayList.add(fragment1);
          } 
          j = i;
          break;
        case 1:
        case 7:
          paramArrayList.add(op.fragment);
          j = i;
          paramFragment = fragment;
          break;
      } 
      i = j + 1;
    } 
    return fragment;
  }
  
  public boolean generateOps(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2) {
    if (FragmentManagerImpl.DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Run: ");
      stringBuilder.append(this);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    paramArrayList1.add(this);
    paramArrayList2.add(Boolean.valueOf(false));
    if (this.mAddToBackStack)
      this.mManager.addBackStackState(this); 
    return true;
  }
  
  public CharSequence getBreadCrumbShortTitle() { return (this.mBreadCrumbShortTitleRes != 0) ? this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes) : this.mBreadCrumbShortTitleText; }
  
  public int getBreadCrumbShortTitleRes() { return this.mBreadCrumbShortTitleRes; }
  
  public CharSequence getBreadCrumbTitle() { return (this.mBreadCrumbTitleRes != 0) ? this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes) : this.mBreadCrumbTitleText; }
  
  public int getBreadCrumbTitleRes() { return this.mBreadCrumbTitleRes; }
  
  public int getId() { return this.mIndex; }
  
  public String getName() { return this.mName; }
  
  public int getTransition() { return this.mTransition; }
  
  public int getTransitionStyle() { return this.mTransitionStyle; }
  
  public FragmentTransaction hide(Fragment paramFragment) {
    addOp(new Op(4, paramFragment));
    return this;
  }
  
  boolean interactsWith(int paramInt) {
    int i = this.mOps.size();
    for (byte b = 0; b < i; b++) {
      boolean bool;
      Op op = (Op)this.mOps.get(b);
      if (op.fragment != null) {
        bool = op.fragment.mContainerId;
      } else {
        bool = false;
      } 
      if (bool && bool == paramInt)
        return true; 
    } 
    return false;
  }
  
  boolean interactsWith(ArrayList<BackStackRecord> paramArrayList, int paramInt1, int paramInt2) {
    if (paramInt2 == paramInt1)
      return false; 
    int j = this.mOps.size();
    byte b = 0;
    int i;
    for (i = -1; b < j; i = k) {
      byte b1;
      Op op = (Op)this.mOps.get(b);
      if (op.fragment != null) {
        b1 = op.fragment.mContainerId;
      } else {
        b1 = 0;
      } 
      int k = i;
      if (b1) {
        k = i;
        if (b1 != i) {
          for (i = paramInt1; i < paramInt2; i++) {
            BackStackRecord backStackRecord = (BackStackRecord)paramArrayList.get(i);
            int m = backStackRecord.mOps.size();
            for (k = 0; k < m; k++) {
              boolean bool;
              Op op1 = (Op)backStackRecord.mOps.get(k);
              if (op1.fragment != null) {
                bool = op1.fragment.mContainerId;
              } else {
                bool = false;
              } 
              if (bool == b1)
                return true; 
            } 
          } 
          k = b1;
        } 
      } 
      b++;
    } 
    return false;
  }
  
  public boolean isAddToBackStackAllowed() { return this.mAllowAddToBackStack; }
  
  public boolean isEmpty() { return this.mOps.isEmpty(); }
  
  boolean isPostponed() {
    for (byte b = 0; b < this.mOps.size(); b++) {
      if (isFragmentPostponed((Op)this.mOps.get(b)))
        return true; 
    } 
    return false;
  }
  
  public FragmentTransaction remove(Fragment paramFragment) {
    addOp(new Op(3, paramFragment));
    return this;
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment) { return replace(paramInt, paramFragment, null); }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment, String paramString) {
    if (paramInt == 0)
      throw new IllegalArgumentException("Must use non-zero containerViewId"); 
    doAddOp(paramInt, paramFragment, paramString, 2);
    return this;
  }
  
  public FragmentTransaction runOnCommit(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new IllegalArgumentException("runnable cannot be null"); 
    disallowAddToBackStack();
    if (this.mCommitRunnables == null)
      this.mCommitRunnables = new ArrayList(); 
    this.mCommitRunnables.add(paramRunnable);
    return this;
  }
  
  public void runOnCommitRunnables() {
    if (this.mCommitRunnables != null) {
      byte b = 0;
      int i = this.mCommitRunnables.size();
      while (b < i) {
        ((Runnable)this.mCommitRunnables.get(b)).run();
        b++;
      } 
      this.mCommitRunnables = null;
    } 
  }
  
  public FragmentTransaction setAllowOptimization(boolean paramBoolean) { return setReorderingAllowed(paramBoolean); }
  
  public FragmentTransaction setBreadCrumbShortTitle(int paramInt) {
    this.mBreadCrumbShortTitleRes = paramInt;
    this.mBreadCrumbShortTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(CharSequence paramCharSequence) {
    this.mBreadCrumbShortTitleRes = 0;
    this.mBreadCrumbShortTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(int paramInt) {
    this.mBreadCrumbTitleRes = paramInt;
    this.mBreadCrumbTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(CharSequence paramCharSequence) {
    this.mBreadCrumbTitleRes = 0;
    this.mBreadCrumbTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2) { return setCustomAnimations(paramInt1, paramInt2, 0, 0); }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mEnterAnim = paramInt1;
    this.mExitAnim = paramInt2;
    this.mPopEnterAnim = paramInt3;
    this.mPopExitAnim = paramInt4;
    return this;
  }
  
  void setOnStartPostponedListener(Fragment.OnStartEnterTransitionListener paramOnStartEnterTransitionListener) {
    for (byte b = 0; b < this.mOps.size(); b++) {
      Op op = (Op)this.mOps.get(b);
      if (isFragmentPostponed(op))
        op.fragment.setOnStartEnterTransitionListener(paramOnStartEnterTransitionListener); 
    } 
  }
  
  public FragmentTransaction setPrimaryNavigationFragment(Fragment paramFragment) {
    addOp(new Op(8, paramFragment));
    return this;
  }
  
  public FragmentTransaction setReorderingAllowed(boolean paramBoolean) {
    this.mReorderingAllowed = paramBoolean;
    return this;
  }
  
  public FragmentTransaction setTransition(int paramInt) {
    this.mTransition = paramInt;
    return this;
  }
  
  public FragmentTransaction setTransitionStyle(int paramInt) {
    this.mTransitionStyle = paramInt;
    return this;
  }
  
  public FragmentTransaction show(Fragment paramFragment) {
    addOp(new Op(5, paramFragment));
    return this;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(128);
    stringBuilder.append("BackStackEntry{");
    stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    if (this.mIndex >= 0) {
      stringBuilder.append(" #");
      stringBuilder.append(this.mIndex);
    } 
    if (this.mName != null) {
      stringBuilder.append(" ");
      stringBuilder.append(this.mName);
    } 
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
  
  Fragment trackAddedFragmentsInPop(ArrayList<Fragment> paramArrayList, Fragment paramFragment) {
    byte b = 0;
    while (b < this.mOps.size()) {
      Op op = (Op)this.mOps.get(b);
      int i = op.cmd;
      if (i != 1)
        if (i != 3) {
          switch (i) {
            case 9:
              paramFragment = op.fragment;
              break;
            case 8:
              paramFragment = null;
              break;
            case 6:
              paramArrayList.add(op.fragment);
              break;
            case 7:
              paramArrayList.remove(op.fragment);
              break;
          } 
          b++;
        }  
    } 
    return paramFragment;
  }
  
  static final class Op {
    int cmd;
    
    int enterAnim;
    
    int exitAnim;
    
    Fragment fragment;
    
    int popEnterAnim;
    
    int popExitAnim;
    
    Op() {}
    
    Op(int param1Int, Fragment param1Fragment) {
      this.cmd = param1Int;
      this.fragment = param1Fragment;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/BackStackRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */