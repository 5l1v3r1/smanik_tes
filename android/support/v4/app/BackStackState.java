package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

final class BackStackState implements Parcelable {
  public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator<BackStackState>() {
      public BackStackState createFromParcel(Parcel param1Parcel) { return new BackStackState(param1Parcel); }
      
      public BackStackState[] newArray(int param1Int) { return new BackStackState[param1Int]; }
    };
  
  final int mBreadCrumbShortTitleRes;
  
  final CharSequence mBreadCrumbShortTitleText;
  
  final int mBreadCrumbTitleRes;
  
  final CharSequence mBreadCrumbTitleText;
  
  final int mIndex;
  
  final String mName;
  
  final int[] mOps;
  
  final boolean mReorderingAllowed;
  
  final ArrayList<String> mSharedElementSourceNames;
  
  final ArrayList<String> mSharedElementTargetNames;
  
  final int mTransition;
  
  final int mTransitionStyle;
  
  public BackStackState(Parcel paramParcel) {
    this.mOps = paramParcel.createIntArray();
    this.mTransition = paramParcel.readInt();
    this.mTransitionStyle = paramParcel.readInt();
    this.mName = paramParcel.readString();
    this.mIndex = paramParcel.readInt();
    this.mBreadCrumbTitleRes = paramParcel.readInt();
    this.mBreadCrumbTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
    this.mBreadCrumbShortTitleRes = paramParcel.readInt();
    this.mBreadCrumbShortTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
    this.mSharedElementSourceNames = paramParcel.createStringArrayList();
    this.mSharedElementTargetNames = paramParcel.createStringArrayList();
    if (paramParcel.readInt() != 0) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mReorderingAllowed = bool;
  }
  
  public BackStackState(BackStackRecord paramBackStackRecord) {
    int j = paramBackStackRecord.mOps.size();
    this.mOps = new int[j * 6];
    if (!paramBackStackRecord.mAddToBackStack)
      throw new IllegalStateException("Not on back stack"); 
    byte b = 0;
    for (int i = 0; b < j; i++) {
      BackStackRecord.Op op = (BackStackRecord.Op)paramBackStackRecord.mOps.get(b);
      int[] arrayOfInt = this.mOps;
      int k = i + true;
      arrayOfInt[i] = op.cmd;
      arrayOfInt = this.mOps;
      boolean bool = k + true;
      if (op.fragment != null) {
        i = op.fragment.mIndex;
      } else {
        i = -1;
      } 
      arrayOfInt[k] = i;
      arrayOfInt = this.mOps;
      i = bool + true;
      arrayOfInt[bool] = op.enterAnim;
      arrayOfInt = this.mOps;
      k = i + 1;
      arrayOfInt[i] = op.exitAnim;
      arrayOfInt = this.mOps;
      i = k + 1;
      arrayOfInt[k] = op.popEnterAnim;
      this.mOps[i] = op.popExitAnim;
      b++;
    } 
    this.mTransition = paramBackStackRecord.mTransition;
    this.mTransitionStyle = paramBackStackRecord.mTransitionStyle;
    this.mName = paramBackStackRecord.mName;
    this.mIndex = paramBackStackRecord.mIndex;
    this.mBreadCrumbTitleRes = paramBackStackRecord.mBreadCrumbTitleRes;
    this.mBreadCrumbTitleText = paramBackStackRecord.mBreadCrumbTitleText;
    this.mBreadCrumbShortTitleRes = paramBackStackRecord.mBreadCrumbShortTitleRes;
    this.mBreadCrumbShortTitleText = paramBackStackRecord.mBreadCrumbShortTitleText;
    this.mSharedElementSourceNames = paramBackStackRecord.mSharedElementSourceNames;
    this.mSharedElementTargetNames = paramBackStackRecord.mSharedElementTargetNames;
    this.mReorderingAllowed = paramBackStackRecord.mReorderingAllowed;
  }
  
  public int describeContents() { return 0; }
  
  public BackStackRecord instantiate(FragmentManagerImpl paramFragmentManagerImpl) {
    BackStackRecord backStackRecord = new BackStackRecord(paramFragmentManagerImpl);
    int i = 0;
    byte b = 0;
    while (i < this.mOps.length) {
      BackStackRecord.Op op = new BackStackRecord.Op();
      int[] arrayOfInt = this.mOps;
      int j = i + true;
      op.cmd = arrayOfInt[i];
      if (FragmentManagerImpl.DEBUG) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Instantiate ");
        stringBuilder.append(backStackRecord);
        stringBuilder.append(" op #");
        stringBuilder.append(b);
        stringBuilder.append(" base fragment #");
        stringBuilder.append(this.mOps[j]);
        Log.v("FragmentManager", stringBuilder.toString());
      } 
      arrayOfInt = this.mOps;
      i = j + true;
      j = arrayOfInt[j];
      if (j >= 0) {
        op.fragment = (Fragment)paramFragmentManagerImpl.mActive.get(j);
      } else {
        op.fragment = null;
      } 
      arrayOfInt = this.mOps;
      j = i + true;
      op.enterAnim = arrayOfInt[i];
      arrayOfInt = this.mOps;
      i = j + 1;
      op.exitAnim = arrayOfInt[j];
      arrayOfInt = this.mOps;
      j = i + 1;
      op.popEnterAnim = arrayOfInt[i];
      op.popExitAnim = this.mOps[j];
      backStackRecord.mEnterAnim = op.enterAnim;
      backStackRecord.mExitAnim = op.exitAnim;
      backStackRecord.mPopEnterAnim = op.popEnterAnim;
      backStackRecord.mPopExitAnim = op.popExitAnim;
      backStackRecord.addOp(op);
      b++;
      i = j + 1;
    } 
    backStackRecord.mTransition = this.mTransition;
    backStackRecord.mTransitionStyle = this.mTransitionStyle;
    backStackRecord.mName = this.mName;
    backStackRecord.mIndex = this.mIndex;
    backStackRecord.mAddToBackStack = true;
    backStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
    backStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
    backStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
    backStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
    backStackRecord.mSharedElementSourceNames = this.mSharedElementSourceNames;
    backStackRecord.mSharedElementTargetNames = this.mSharedElementTargetNames;
    backStackRecord.mReorderingAllowed = this.mReorderingAllowed;
    backStackRecord.bumpBackStackNesting(1);
    return backStackRecord;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/BackStackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */