package android.support.design.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class BottomSheetDialogFragment extends AppCompatDialogFragment {
  public Dialog onCreateDialog(Bundle paramBundle) { return new BottomSheetDialog(getContext(), getTheme()); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/BottomSheetDialogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */