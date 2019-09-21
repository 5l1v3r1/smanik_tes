package android.support.design.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.WithHint;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class TextInputEditText extends AppCompatEditText {
  public TextInputEditText(Context paramContext) { super(paramContext); }
  
  public TextInputEditText(Context paramContext, AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  public TextInputEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) { super(paramContext, paramAttributeSet, paramInt); }
  
  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo) {
    InputConnection inputConnection = super.onCreateInputConnection(paramEditorInfo);
    if (inputConnection != null && paramEditorInfo.hintText == null)
      for (ViewParent viewParent = getParent(); viewParent instanceof android.view.View; viewParent = viewParent.getParent()) {
        if (viewParent instanceof WithHint) {
          paramEditorInfo.hintText = ((WithHint)viewParent).getHint();
          return inputConnection;
        } 
      }  
    return inputConnection;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/TextInputEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */