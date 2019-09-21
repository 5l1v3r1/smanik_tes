package android.support.v4.media;

import android.media.browse.MediaBrowser;
import android.support.annotation.RequiresApi;
import java.lang.reflect.Constructor;
import java.util.List;

@RequiresApi(21)
class ParceledListSliceAdapterApi21 {
  private static Constructor sConstructor;
  
  static  {
    try {
      sConstructor = Class.forName("android.content.pm.ParceledListSlice").getConstructor(new Class[] { List.class });
      return;
    } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {
      classNotFoundException.printStackTrace();
      return;
    } 
  }
  
  static Object newInstance(List<MediaBrowser.MediaItem> paramList) {
    try {
      return sConstructor.newInstance(new Object[] { paramList });
    } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException paramList) {
      paramList.printStackTrace();
      return null;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/ParceledListSliceAdapterApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */