package android.support.v4.media;

import android.media.AudioAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.Method;

@RequiresApi(21)
class AudioAttributesCompatApi21 {
  private static final String TAG = "AudioAttributesCompat";
  
  private static Method sAudioAttributesToLegacyStreamType;
  
  public static int toLegacyStreamType(Wrapper paramWrapper) {
    audioAttributes = paramWrapper.unwrap();
    try {
      if (sAudioAttributesToLegacyStreamType == null)
        sAudioAttributesToLegacyStreamType = AudioAttributes.class.getMethod("toLegacyStreamType", new Class[] { AudioAttributes.class }); 
      return ((Integer)sAudioAttributesToLegacyStreamType.invoke(null, new Object[] { audioAttributes })).intValue();
    } catch (NoSuchMethodException|java.lang.reflect.InvocationTargetException|IllegalAccessException|ClassCastException audioAttributes) {
      Log.w("AudioAttributesCompat", "getLegacyStreamType() failed on API21+", audioAttributes);
      return -1;
    } 
  }
  
  static final class Wrapper {
    private AudioAttributes mWrapped;
    
    private Wrapper(AudioAttributes param1AudioAttributes) { this.mWrapped = param1AudioAttributes; }
    
    public static Wrapper wrap(@NonNull AudioAttributes param1AudioAttributes) {
      if (param1AudioAttributes == null)
        throw new IllegalArgumentException("AudioAttributesApi21.Wrapper cannot wrap null"); 
      return new Wrapper(param1AudioAttributes);
    }
    
    public AudioAttributes unwrap() { return this.mWrapped; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/AudioAttributesCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */