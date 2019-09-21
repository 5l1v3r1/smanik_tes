package android.support.v4.app;

import android.arch.lifecycle.ViewModelStore;
import java.util.List;

public class FragmentManagerNonConfig {
  private final List<FragmentManagerNonConfig> mChildNonConfigs;
  
  private final List<Fragment> mFragments;
  
  private final List<ViewModelStore> mViewModelStores;
  
  FragmentManagerNonConfig(List<Fragment> paramList1, List<FragmentManagerNonConfig> paramList2, List<ViewModelStore> paramList3) {
    this.mFragments = paramList1;
    this.mChildNonConfigs = paramList2;
    this.mViewModelStores = paramList3;
  }
  
  List<FragmentManagerNonConfig> getChildNonConfigs() { return this.mChildNonConfigs; }
  
  List<Fragment> getFragments() { return this.mFragments; }
  
  List<ViewModelStore> getViewModelStores() { return this.mViewModelStores; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/FragmentManagerNonConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */