package android.support.v4.media;

import android.content.Context;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.service.media.MediaBrowserService;
import android.support.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(21)
class MediaBrowserServiceCompatApi21 {
  public static Object createService(Context paramContext, ServiceCompatProxy paramServiceCompatProxy) { return new MediaBrowserServiceAdaptor(paramContext, paramServiceCompatProxy); }
  
  public static void notifyChildrenChanged(Object paramObject, String paramString) { ((MediaBrowserService)paramObject).notifyChildrenChanged(paramString); }
  
  public static IBinder onBind(Object paramObject, Intent paramIntent) { return ((MediaBrowserService)paramObject).onBind(paramIntent); }
  
  public static void onCreate(Object paramObject) { ((MediaBrowserService)paramObject).onCreate(); }
  
  public static void setSessionToken(Object paramObject1, Object paramObject2) { ((MediaBrowserService)paramObject1).setSessionToken((MediaSession.Token)paramObject2); }
  
  static class BrowserRoot {
    final Bundle mExtras;
    
    final String mRootId;
    
    BrowserRoot(String param1String, Bundle param1Bundle) {
      this.mRootId = param1String;
      this.mExtras = param1Bundle;
    }
  }
  
  static class MediaBrowserServiceAdaptor extends MediaBrowserService {
    final MediaBrowserServiceCompatApi21.ServiceCompatProxy mServiceProxy;
    
    MediaBrowserServiceAdaptor(Context param1Context, MediaBrowserServiceCompatApi21.ServiceCompatProxy param1ServiceCompatProxy) {
      attachBaseContext(param1Context);
      this.mServiceProxy = param1ServiceCompatProxy;
    }
    
    public MediaBrowserService.BrowserRoot onGetRoot(String param1String, int param1Int, Bundle param1Bundle) {
      MediaBrowserServiceCompatApi21.ServiceCompatProxy serviceCompatProxy = this.mServiceProxy;
      if (param1Bundle == null) {
        param1Bundle = null;
      } else {
        param1Bundle = new Bundle(param1Bundle);
      } 
      MediaBrowserServiceCompatApi21.BrowserRoot browserRoot = serviceCompatProxy.onGetRoot(param1String, param1Int, param1Bundle);
      return (browserRoot == null) ? null : new MediaBrowserService.BrowserRoot(browserRoot.mRootId, browserRoot.mExtras);
    }
    
    public void onLoadChildren(String param1String, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> param1Result) { this.mServiceProxy.onLoadChildren(param1String, new MediaBrowserServiceCompatApi21.ResultWrapper(param1Result)); }
  }
  
  static class ResultWrapper<T> extends Object {
    MediaBrowserService.Result mResultObj;
    
    ResultWrapper(MediaBrowserService.Result param1Result) { this.mResultObj = param1Result; }
    
    public void detach() { this.mResultObj.detach(); }
    
    List<MediaBrowser.MediaItem> parcelListToItemList(List<Parcel> param1List) {
      if (param1List == null)
        return null; 
      ArrayList arrayList = new ArrayList();
      for (Parcel parcel : param1List) {
        parcel.setDataPosition(0);
        arrayList.add(MediaBrowser.MediaItem.CREATOR.createFromParcel(parcel));
        parcel.recycle();
      } 
      return arrayList;
    }
    
    public void sendResult(T param1T) {
      if (param1T instanceof List) {
        this.mResultObj.sendResult(parcelListToItemList((List)param1T));
        return;
      } 
      if (param1T instanceof Parcel) {
        param1T = (T)(Parcel)param1T;
        param1T.setDataPosition(0);
        this.mResultObj.sendResult(MediaBrowser.MediaItem.CREATOR.createFromParcel(param1T));
        param1T.recycle();
        return;
      } 
      this.mResultObj.sendResult(null);
    }
  }
  
  public static interface ServiceCompatProxy {
    MediaBrowserServiceCompatApi21.BrowserRoot onGetRoot(String param1String, int param1Int, Bundle param1Bundle);
    
    void onLoadChildren(String param1String, MediaBrowserServiceCompatApi21.ResultWrapper<List<Parcel>> param1ResultWrapper);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/MediaBrowserServiceCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */