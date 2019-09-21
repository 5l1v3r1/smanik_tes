package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class MediaBrowserCompatUtils {
  public static boolean areSameOptions(Bundle paramBundle1, Bundle paramBundle2) { return (paramBundle1 == paramBundle2) ? true : ((paramBundle1 == null) ? ((paramBundle2.getInt("android.media.browse.extra.PAGE", -1) == -1 && paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1)) : ((paramBundle2 == null) ? ((paramBundle1.getInt("android.media.browse.extra.PAGE", -1) == -1 && paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1)) : ((paramBundle1.getInt("android.media.browse.extra.PAGE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE", -1) && paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1) == paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1))))); }
  
  public static boolean hasDuplicatedItems(Bundle paramBundle1, Bundle paramBundle2) {
    int m;
    int k;
    int j;
    int i;
    if (paramBundle1 == null) {
      k = -1;
    } else {
      k = paramBundle1.getInt("android.media.browse.extra.PAGE", -1);
    } 
    if (paramBundle2 == null) {
      i = -1;
    } else {
      i = paramBundle2.getInt("android.media.browse.extra.PAGE", -1);
    } 
    if (paramBundle1 == null) {
      m = -1;
    } else {
      m = paramBundle1.getInt("android.media.browse.extra.PAGE_SIZE", -1);
    } 
    if (paramBundle2 == null) {
      j = -1;
    } else {
      j = paramBundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1);
    } 
    int n = Integer.MAX_VALUE;
    if (k == -1 || m == -1) {
      m = Integer.MAX_VALUE;
      k = 0;
    } else {
      k *= m;
      m = m + k - 1;
    } 
    if (i == -1 || j == -1) {
      i = 0;
      j = n;
    } else {
      i = j * i;
      j = j + i - 1;
    } 
    return (k <= i && i <= m) ? true : ((k <= j && j <= m));
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/MediaBrowserCompatUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */