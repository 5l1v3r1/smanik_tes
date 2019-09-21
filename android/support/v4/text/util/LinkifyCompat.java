package android.support.v4.text.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.webkit.WebView;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat {
  private static final Comparator<LinkSpec> COMPARATOR;
  
  private static final String[] EMPTY_STRING = new String[0];
  
  static  {
    COMPARATOR = new Comparator<LinkSpec>() {
        public int compare(LinkifyCompat.LinkSpec param1LinkSpec1, LinkifyCompat.LinkSpec param1LinkSpec2) { return (param1LinkSpec1.start < param1LinkSpec2.start) ? -1 : ((param1LinkSpec1.start > param1LinkSpec2.start) ? 1 : ((param1LinkSpec1.end < param1LinkSpec2.end) ? 1 : ((param1LinkSpec1.end > param1LinkSpec2.end) ? -1 : 0))); }
      };
  }
  
  private static void addLinkMovementMethod(@NonNull TextView paramTextView) {
    MovementMethod movementMethod = paramTextView.getMovementMethod();
    if ((movementMethod == null || !(movementMethod instanceof LinkMovementMethod)) && paramTextView.getLinksClickable())
      paramTextView.setMovementMethod(LinkMovementMethod.getInstance()); 
  }
  
  public static void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString) {
    if (Build.VERSION.SDK_INT >= 26) {
      Linkify.addLinks(paramTextView, paramPattern, paramString);
      return;
    } 
    addLinks(paramTextView, paramPattern, paramString, null, null, null);
  }
  
  public static void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter) {
    if (Build.VERSION.SDK_INT >= 26) {
      Linkify.addLinks(paramTextView, paramPattern, paramString, paramMatchFilter, paramTransformFilter);
      return;
    } 
    addLinks(paramTextView, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }
  
  public static void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter) {
    if (Build.VERSION.SDK_INT >= 26) {
      Linkify.addLinks(paramTextView, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter);
      return;
    } 
    SpannableString spannableString = SpannableString.valueOf(paramTextView.getText());
    if (addLinks(spannableString, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter)) {
      paramTextView.setText(spannableString);
      addLinkMovementMethod(paramTextView);
    } 
  }
  
  public static boolean addLinks(@NonNull Spannable paramSpannable, int paramInt) {
    if (Build.VERSION.SDK_INT >= 27)
      return Linkify.addLinks(paramSpannable, paramInt); 
    if (paramInt == 0)
      return false; 
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    for (int i = arrayOfURLSpan.length - 1; i >= 0; i--)
      paramSpannable.removeSpan(arrayOfURLSpan[i]); 
    if ((paramInt & 0x4) != 0)
      Linkify.addLinks(paramSpannable, 4); 
    ArrayList arrayList = new ArrayList();
    if ((paramInt & true) != 0) {
      Pattern pattern = PatternsCompat.AUTOLINK_WEB_URL;
      Linkify.MatchFilter matchFilter = Linkify.sUrlMatchFilter;
      gatherLinks(arrayList, paramSpannable, pattern, new String[] { "http://", "https://", "rtsp://" }, matchFilter, null);
    } 
    if ((paramInt & 0x2) != 0)
      gatherLinks(arrayList, paramSpannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[] { "mailto:" }, null, null); 
    if ((paramInt & 0x8) != 0)
      gatherMapLinks(arrayList, paramSpannable); 
    pruneOverlaps(arrayList, paramSpannable);
    if (arrayList.size() == 0)
      return false; 
    for (LinkSpec linkSpec : arrayList) {
      if (linkSpec.frameworkAddedSpan == null)
        applyLink(linkSpec.url, linkSpec.start, linkSpec.end, paramSpannable); 
    } 
    return true;
  }
  
  public static boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString) { return (Build.VERSION.SDK_INT >= 26) ? Linkify.addLinks(paramSpannable, paramPattern, paramString) : addLinks(paramSpannable, paramPattern, paramString, null, null, null); }
  
  public static boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter) { return (Build.VERSION.SDK_INT >= 26) ? Linkify.addLinks(paramSpannable, paramPattern, paramString, paramMatchFilter, paramTransformFilter) : addLinks(paramSpannable, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter); }
  
  public static boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter) { // Byte code:
    //   0: getstatic android/os/Build$VERSION.SDK_INT : I
    //   3: bipush #26
    //   5: if_icmplt -> 20
    //   8: aload_0
    //   9: aload_1
    //   10: aload_2
    //   11: aload_3
    //   12: aload #4
    //   14: aload #5
    //   16: invokestatic addLinks : (Landroid/text/Spannable;Ljava/util/regex/Pattern;Ljava/lang/String;[Ljava/lang/String;Landroid/text/util/Linkify$MatchFilter;Landroid/text/util/Linkify$TransformFilter;)Z
    //   19: ireturn
    //   20: aload_2
    //   21: astore #10
    //   23: aload_2
    //   24: ifnonnull -> 31
    //   27: ldc ''
    //   29: astore #10
    //   31: aload_3
    //   32: ifnull -> 43
    //   35: aload_3
    //   36: astore_2
    //   37: aload_3
    //   38: arraylength
    //   39: iconst_1
    //   40: if_icmpge -> 47
    //   43: getstatic android/support/v4/text/util/LinkifyCompat.EMPTY_STRING : [Ljava/lang/String;
    //   46: astore_2
    //   47: aload_2
    //   48: arraylength
    //   49: iconst_1
    //   50: iadd
    //   51: anewarray java/lang/String
    //   54: astore #11
    //   56: aload #11
    //   58: iconst_0
    //   59: aload #10
    //   61: getstatic java/util/Locale.ROOT : Ljava/util/Locale;
    //   64: invokevirtual toLowerCase : (Ljava/util/Locale;)Ljava/lang/String;
    //   67: aastore
    //   68: iconst_0
    //   69: istore #6
    //   71: iload #6
    //   73: aload_2
    //   74: arraylength
    //   75: if_icmpge -> 116
    //   78: aload_2
    //   79: iload #6
    //   81: aaload
    //   82: astore_3
    //   83: iload #6
    //   85: iconst_1
    //   86: iadd
    //   87: istore #6
    //   89: aload_3
    //   90: ifnonnull -> 99
    //   93: ldc ''
    //   95: astore_3
    //   96: goto -> 107
    //   99: aload_3
    //   100: getstatic java/util/Locale.ROOT : Ljava/util/Locale;
    //   103: invokevirtual toLowerCase : (Ljava/util/Locale;)Ljava/lang/String;
    //   106: astore_3
    //   107: aload #11
    //   109: iload #6
    //   111: aload_3
    //   112: aastore
    //   113: goto -> 71
    //   116: aload_1
    //   117: aload_0
    //   118: invokevirtual matcher : (Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   121: astore_1
    //   122: iconst_0
    //   123: istore #8
    //   125: aload_1
    //   126: invokevirtual find : ()Z
    //   129: ifeq -> 201
    //   132: aload_1
    //   133: invokevirtual start : ()I
    //   136: istore #6
    //   138: aload_1
    //   139: invokevirtual end : ()I
    //   142: istore #7
    //   144: aload #4
    //   146: ifnull -> 166
    //   149: aload #4
    //   151: aload_0
    //   152: iload #6
    //   154: iload #7
    //   156: invokeinterface acceptMatch : (Ljava/lang/CharSequence;II)Z
    //   161: istore #9
    //   163: goto -> 169
    //   166: iconst_1
    //   167: istore #9
    //   169: iload #9
    //   171: ifeq -> 125
    //   174: aload_1
    //   175: iconst_0
    //   176: invokevirtual group : (I)Ljava/lang/String;
    //   179: aload #11
    //   181: aload_1
    //   182: aload #5
    //   184: invokestatic makeUrl : (Ljava/lang/String;[Ljava/lang/String;Ljava/util/regex/Matcher;Landroid/text/util/Linkify$TransformFilter;)Ljava/lang/String;
    //   187: iload #6
    //   189: iload #7
    //   191: aload_0
    //   192: invokestatic applyLink : (Ljava/lang/String;IILandroid/text/Spannable;)V
    //   195: iconst_1
    //   196: istore #8
    //   198: goto -> 125
    //   201: iload #8
    //   203: ireturn }
  
  public static boolean addLinks(@NonNull TextView paramTextView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 26)
      return Linkify.addLinks(paramTextView, paramInt); 
    if (paramInt == 0)
      return false; 
    CharSequence charSequence = paramTextView.getText();
    if (charSequence instanceof Spannable) {
      if (addLinks((Spannable)charSequence, paramInt)) {
        addLinkMovementMethod(paramTextView);
        return true;
      } 
      return false;
    } 
    SpannableString spannableString = SpannableString.valueOf(charSequence);
    if (addLinks(spannableString, paramInt)) {
      addLinkMovementMethod(paramTextView);
      paramTextView.setText(spannableString);
      return true;
    } 
    return false;
  }
  
  private static void applyLink(String paramString, int paramInt1, int paramInt2, Spannable paramSpannable) { paramSpannable.setSpan(new URLSpan(paramString), paramInt1, paramInt2, 33); }
  
  private static void gatherLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable, Pattern paramPattern, String[] paramArrayOfString, Linkify.MatchFilter paramMatchFilter, Linkify.TransformFilter paramTransformFilter) {
    Matcher matcher = paramPattern.matcher(paramSpannable);
    while (matcher.find()) {
      int i = matcher.start();
      int j = matcher.end();
      if (paramMatchFilter == null || paramMatchFilter.acceptMatch(paramSpannable, i, j)) {
        LinkSpec linkSpec = new LinkSpec();
        linkSpec.url = makeUrl(matcher.group(0), paramArrayOfString, matcher, paramTransformFilter);
        linkSpec.start = i;
        linkSpec.end = j;
        paramArrayList.add(linkSpec);
      } 
    } 
  }
  
  private static void gatherMapLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable) {
    String str = paramSpannable.toString();
    int i = 0;
    while (true) {
      try {
        String str1 = WebView.findAddress(str);
        if (str1 != null) {
          int k = str.indexOf(str1);
          if (k < 0)
            return; 
          linkSpec = new LinkSpec();
          int j = str1.length() + k;
          linkSpec.start = k + i;
          i += j;
          linkSpec.end = i;
          str = str.substring(j);
          try {
            str1 = URLEncoder.encode(str1, "UTF-8");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("geo:0,0?q=");
            stringBuilder.append(str1);
            linkSpec.url = stringBuilder.toString();
            paramArrayList.add(linkSpec);
            continue;
          } catch (UnsupportedEncodingException linkSpec) {
            continue;
          } 
        } 
        return;
      } catch (UnsupportedOperationException paramArrayList) {
        return;
      } 
    } 
  }
  
  private static String makeUrl(@NonNull String paramString, @NonNull String[] paramArrayOfString, Matcher paramMatcher, @Nullable Linkify.TransformFilter paramTransformFilter) {
    int i;
    String str2 = paramString;
    if (paramTransformFilter != null)
      str2 = paramTransformFilter.transformUrl(paramMatcher, paramString); 
    byte b = 0;
    while (true) {
      i = paramArrayOfString.length;
      int j = 1;
      if (b < i) {
        if (str2.regionMatches(true, 0, paramArrayOfString[b], 0, paramArrayOfString[b].length())) {
          i = j;
          paramString = str2;
          if (!str2.regionMatches(false, 0, paramArrayOfString[b], 0, paramArrayOfString[b].length())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(paramArrayOfString[b]);
            stringBuilder.append(str2.substring(paramArrayOfString[b].length()));
            String str = stringBuilder.toString();
            i = j;
          } 
          break;
        } 
        b++;
        continue;
      } 
      i = 0;
      paramString = str2;
      break;
    } 
    String str1 = paramString;
    if (i == 0) {
      str1 = paramString;
      if (paramArrayOfString.length > 0) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramArrayOfString[0]);
        stringBuilder.append(paramString);
        str1 = stringBuilder.toString();
      } 
    } 
    return str1;
  }
  
  private static void pruneOverlaps(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable) {
    int i = paramSpannable.length();
    int j = 0;
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, i, URLSpan.class);
    for (i = 0; i < arrayOfURLSpan.length; i++) {
      LinkSpec linkSpec = new LinkSpec();
      linkSpec.frameworkAddedSpan = arrayOfURLSpan[i];
      linkSpec.start = paramSpannable.getSpanStart(arrayOfURLSpan[i]);
      linkSpec.end = paramSpannable.getSpanEnd(arrayOfURLSpan[i]);
      paramArrayList.add(linkSpec);
    } 
    Collections.sort(paramArrayList, COMPARATOR);
    int k = paramArrayList.size();
    for (i = j; i < k - 1; i = m) {
      LinkSpec linkSpec1 = (LinkSpec)paramArrayList.get(i);
      int m = i + 1;
      LinkSpec linkSpec2 = (LinkSpec)paramArrayList.get(m);
      if (linkSpec1.start <= linkSpec2.start && linkSpec1.end > linkSpec2.start) {
        if (linkSpec2.end <= linkSpec1.end || linkSpec1.end - linkSpec1.start > linkSpec2.end - linkSpec2.start) {
          j = m;
        } else if (linkSpec1.end - linkSpec1.start < linkSpec2.end - linkSpec2.start) {
          j = i;
        } else {
          j = -1;
        } 
        if (j != -1) {
          URLSpan uRLSpan = ((LinkSpec)paramArrayList.get(j)).frameworkAddedSpan;
          if (uRLSpan != null)
            paramSpannable.removeSpan(uRLSpan); 
          paramArrayList.remove(j);
          k--;
          continue;
        } 
      } 
    } 
  }
  
  private static class LinkSpec {
    int end;
    
    URLSpan frameworkAddedSpan;
    
    int start;
    
    String url;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface LinkifyMask {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/text/util/LinkifyCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */