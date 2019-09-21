package android.support.v4.text;

import java.nio.CharBuffer;
import java.util.Locale;

public final class TextDirectionHeuristicsCompat {
  public static final TextDirectionHeuristicCompat ANYRTL_LTR;
  
  public static final TextDirectionHeuristicCompat FIRSTSTRONG_LTR;
  
  public static final TextDirectionHeuristicCompat FIRSTSTRONG_RTL;
  
  public static final TextDirectionHeuristicCompat LOCALE;
  
  public static final TextDirectionHeuristicCompat LTR = new TextDirectionHeuristicInternal(null, false);
  
  public static final TextDirectionHeuristicCompat RTL = new TextDirectionHeuristicInternal(null, true);
  
  private static final int STATE_FALSE = 1;
  
  private static final int STATE_TRUE = 0;
  
  private static final int STATE_UNKNOWN = 2;
  
  static  {
    FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
    FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, true);
    ANYRTL_LTR = new TextDirectionHeuristicInternal(AnyStrong.INSTANCE_RTL, false);
    LOCALE = TextDirectionHeuristicLocale.INSTANCE;
  }
  
  static int isRtlText(int paramInt) {
    switch (paramInt) {
      default:
        return 2;
      case 1:
      case 2:
        return 0;
      case 0:
        break;
    } 
    return 1;
  }
  
  static int isRtlTextOrFormat(int paramInt) {
    switch (paramInt) {
      default:
        switch (paramInt) {
          default:
            return 2;
          case 16:
          case 17:
            return 0;
          case 14:
          case 15:
            break;
        } 
        break;
      case 1:
      case 2:
      
      case 0:
        break;
    } 
    return 1;
  }
  
  private static class AnyStrong implements TextDirectionAlgorithm {
    static final AnyStrong INSTANCE_LTR;
    
    static final AnyStrong INSTANCE_RTL = new AnyStrong(true);
    
    private final boolean mLookForRtl;
    
    static  {
      INSTANCE_LTR = new AnyStrong(false);
    }
    
    private AnyStrong(boolean param1Boolean) { this.mLookForRtl = param1Boolean; }
    
    public int checkRtl(CharSequence param1CharSequence, int param1Int1, int param1Int2) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  private static class FirstStrong implements TextDirectionAlgorithm {
    static final FirstStrong INSTANCE = new FirstStrong();
    
    public int checkRtl(CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      int j = 2;
      int i;
      for (i = param1Int1; i < param1Int2 + param1Int1 && j == 2; i++)
        j = TextDirectionHeuristicsCompat.isRtlTextOrFormat(Character.getDirectionality(param1CharSequence.charAt(i))); 
      return j;
    }
  }
  
  private static interface TextDirectionAlgorithm {
    int checkRtl(CharSequence param1CharSequence, int param1Int1, int param1Int2);
  }
  
  private static abstract class TextDirectionHeuristicImpl implements TextDirectionHeuristicCompat {
    private final TextDirectionHeuristicsCompat.TextDirectionAlgorithm mAlgorithm;
    
    TextDirectionHeuristicImpl(TextDirectionHeuristicsCompat.TextDirectionAlgorithm param1TextDirectionAlgorithm) { this.mAlgorithm = param1TextDirectionAlgorithm; }
    
    private boolean doCheck(CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      switch (this.mAlgorithm.checkRtl(param1CharSequence, param1Int1, param1Int2)) {
        default:
          return defaultIsRtl();
        case 1:
          return false;
        case 0:
          break;
      } 
      return true;
    }
    
    protected abstract boolean defaultIsRtl();
    
    public boolean isRtl(CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      if (param1CharSequence == null || param1Int1 < 0 || param1Int2 < 0 || param1CharSequence.length() - param1Int2 < param1Int1)
        throw new IllegalArgumentException(); 
      return (this.mAlgorithm == null) ? defaultIsRtl() : doCheck(param1CharSequence, param1Int1, param1Int2);
    }
    
    public boolean isRtl(char[] param1ArrayOfChar, int param1Int1, int param1Int2) { return isRtl(CharBuffer.wrap(param1ArrayOfChar), param1Int1, param1Int2); }
  }
  
  private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl {
    private final boolean mDefaultIsRtl;
    
    TextDirectionHeuristicInternal(TextDirectionHeuristicsCompat.TextDirectionAlgorithm param1TextDirectionAlgorithm, boolean param1Boolean) {
      super(param1TextDirectionAlgorithm);
      this.mDefaultIsRtl = param1Boolean;
    }
    
    protected boolean defaultIsRtl() { return this.mDefaultIsRtl; }
  }
  
  private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl {
    static final TextDirectionHeuristicLocale INSTANCE = new TextDirectionHeuristicLocale();
    
    TextDirectionHeuristicLocale() { super(null); }
    
    protected boolean defaultIsRtl() { return (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == 1); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/text/TextDirectionHeuristicsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */