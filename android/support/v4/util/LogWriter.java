package android.support.v4.util;

import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Writer;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class LogWriter extends Writer {
  private StringBuilder mBuilder = new StringBuilder(128);
  
  private final String mTag;
  
  public LogWriter(String paramString) { this.mTag = paramString; }
  
  private void flushBuilder() {
    if (this.mBuilder.length() > 0) {
      Log.d(this.mTag, this.mBuilder.toString());
      this.mBuilder.delete(0, this.mBuilder.length());
    } 
  }
  
  public void close() { flushBuilder(); }
  
  public void flush() { flushBuilder(); }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i;
    for (i = 0; i < paramInt2; i++) {
      char c = paramArrayOfChar[paramInt1 + i];
      if (c == '\n') {
        flushBuilder();
      } else {
        this.mBuilder.append(c);
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/LogWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */