package android.support.v4.media.session;

import android.media.session.MediaSession;
import android.support.annotation.RequiresApi;

@RequiresApi(22)
class MediaSessionCompatApi22 {
  public static void setRatingType(Object paramObject, int paramInt) { ((MediaSession)paramObject).setRatingType(paramInt); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/session/MediaSessionCompatApi22.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */