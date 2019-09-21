package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Xml;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class AnimationUtilsCompat {
  private static Interpolator createInterpolatorFromXml(Context paramContext, Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser) throws XmlPullParserException, IOException {
    PathInterpolatorCompat pathInterpolatorCompat;
    int i = paramXmlPullParser.getDepth();
    paramResources = null;
    while (true) {
      int j = paramXmlPullParser.next();
      if ((j != 3 || paramXmlPullParser.getDepth() > i) && j != 1) {
        if (j != 2)
          continue; 
        pathInterpolatorCompat = Xml.asAttributeSet(paramXmlPullParser);
        String str = paramXmlPullParser.getName();
        if (str.equals("linearInterpolator")) {
          pathInterpolatorCompat = new LinearInterpolator();
          continue;
        } 
        if (str.equals("accelerateInterpolator")) {
          pathInterpolatorCompat = new AccelerateInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("decelerateInterpolator")) {
          DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("accelerateDecelerateInterpolator")) {
          pathInterpolatorCompat = new AccelerateDecelerateInterpolator();
          continue;
        } 
        if (str.equals("cycleInterpolator")) {
          pathInterpolatorCompat = new CycleInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("anticipateInterpolator")) {
          pathInterpolatorCompat = new AnticipateInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("overshootInterpolator")) {
          pathInterpolatorCompat = new OvershootInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("anticipateOvershootInterpolator")) {
          AnticipateOvershootInterpolator anticipateOvershootInterpolator = new AnticipateOvershootInterpolator(paramContext, pathInterpolatorCompat);
          continue;
        } 
        if (str.equals("bounceInterpolator")) {
          pathInterpolatorCompat = new BounceInterpolator();
          continue;
        } 
        if (str.equals("pathInterpolator")) {
          pathInterpolatorCompat = new PathInterpolatorCompat(paramContext, pathInterpolatorCompat, paramXmlPullParser);
          continue;
        } 
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown interpolator name: ");
        stringBuilder.append(paramXmlPullParser.getName());
        throw new RuntimeException(stringBuilder.toString());
      } 
      break;
    } 
    return pathInterpolatorCompat;
  }
  
  public static Interpolator loadInterpolator(Context paramContext, int paramInt) throws Resources.NotFoundException {
    if (Build.VERSION.SDK_INT >= 21)
      return AnimationUtils.loadInterpolator(paramContext, paramInt); 
    context4 = null;
    context1 = null;
    Context context3 = null;
    if (paramInt == 17563663) {
      try {
        return new FastOutLinearInInterpolator();
      } catch (XmlPullParserException null) {
      
      } catch (IOException null) {
        paramContext = context3;
        context1 = paramContext;
        StringBuilder stringBuilder1 = new StringBuilder();
        context1 = paramContext;
        stringBuilder1.append("Can't load animation resource ID #0x");
        context1 = paramContext;
        stringBuilder1.append(Integer.toHexString(paramInt));
        context1 = paramContext;
        Resources.NotFoundException notFoundException1 = new Resources.NotFoundException(stringBuilder1.toString());
        context1 = paramContext;
        notFoundException1.initCause(context2);
        context1 = paramContext;
        throw notFoundException1;
      } finally {
        if (context1 != null)
          context1.close(); 
      } 
    } else {
      if (paramInt == 17563661)
        return new FastOutSlowInInterpolator(); 
      if (paramInt == 17563662)
        return new LinearOutSlowInInterpolator(); 
      context2 = paramContext.getResources().getAnimation(paramInt);
      try {
        return createInterpolatorFromXml(paramContext, paramContext.getResources(), paramContext.getTheme(), context2);
      } catch (XmlPullParserException context1) {
        XmlResourceParser xmlResourceParser = context2;
      } catch (IOException context1) {
        paramContext = context2;
      } finally {
        paramContext = null;
      } 
    } 
    context1 = paramContext;
    StringBuilder stringBuilder = new StringBuilder();
    context1 = paramContext;
    stringBuilder.append("Can't load animation resource ID #0x");
    context1 = paramContext;
    stringBuilder.append(Integer.toHexString(paramInt));
    context1 = paramContext;
    Resources.NotFoundException notFoundException = new Resources.NotFoundException(stringBuilder.toString());
    context1 = paramContext;
    notFoundException.initCause(context2);
    context1 = paramContext;
    throw notFoundException;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/graphics/drawable/AnimationUtilsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */