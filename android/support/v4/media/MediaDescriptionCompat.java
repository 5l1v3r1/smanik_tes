package android.support.v4.media;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;

public final class MediaDescriptionCompat implements Parcelable {
  public static final long BT_FOLDER_TYPE_ALBUMS = 2L;
  
  public static final long BT_FOLDER_TYPE_ARTISTS = 3L;
  
  public static final long BT_FOLDER_TYPE_GENRES = 4L;
  
  public static final long BT_FOLDER_TYPE_MIXED = 0L;
  
  public static final long BT_FOLDER_TYPE_PLAYLISTS = 5L;
  
  public static final long BT_FOLDER_TYPE_TITLES = 1L;
  
  public static final long BT_FOLDER_TYPE_YEARS = 6L;
  
  public static final Parcelable.Creator<MediaDescriptionCompat> CREATOR = new Parcelable.Creator<MediaDescriptionCompat>() {
      public MediaDescriptionCompat createFromParcel(Parcel param1Parcel) { return (Build.VERSION.SDK_INT < 21) ? new MediaDescriptionCompat(param1Parcel) : MediaDescriptionCompat.fromMediaDescription(MediaDescriptionCompatApi21.fromParcel(param1Parcel)); }
      
      public MediaDescriptionCompat[] newArray(int param1Int) { return new MediaDescriptionCompat[param1Int]; }
    };
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final String DESCRIPTION_KEY_MEDIA_URI = "android.support.v4.media.description.MEDIA_URI";
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final String DESCRIPTION_KEY_NULL_BUNDLE_FLAG = "android.support.v4.media.description.NULL_BUNDLE_FLAG";
  
  public static final String EXTRA_BT_FOLDER_TYPE = "android.media.extra.BT_FOLDER_TYPE";
  
  public static final String EXTRA_DOWNLOAD_STATUS = "android.media.extra.DOWNLOAD_STATUS";
  
  public static final long STATUS_DOWNLOADED = 2L;
  
  public static final long STATUS_DOWNLOADING = 1L;
  
  public static final long STATUS_NOT_DOWNLOADED = 0L;
  
  private final CharSequence mDescription;
  
  private Object mDescriptionObj;
  
  private final Bundle mExtras;
  
  private final Bitmap mIcon;
  
  private final Uri mIconUri;
  
  private final String mMediaId;
  
  private final Uri mMediaUri;
  
  private final CharSequence mSubtitle;
  
  private final CharSequence mTitle;
  
  MediaDescriptionCompat(Parcel paramParcel) {
    this.mMediaId = paramParcel.readString();
    this.mTitle = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
    this.mSubtitle = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
    this.mDescription = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
    this.mIcon = (Bitmap)paramParcel.readParcelable(null);
    this.mIconUri = (Uri)paramParcel.readParcelable(null);
    this.mExtras = paramParcel.readBundle();
    this.mMediaUri = (Uri)paramParcel.readParcelable(null);
  }
  
  MediaDescriptionCompat(String paramString, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, Bitmap paramBitmap, Uri paramUri1, Bundle paramBundle, Uri paramUri2) {
    this.mMediaId = paramString;
    this.mTitle = paramCharSequence1;
    this.mSubtitle = paramCharSequence2;
    this.mDescription = paramCharSequence3;
    this.mIcon = paramBitmap;
    this.mIconUri = paramUri1;
    this.mExtras = paramBundle;
    this.mMediaUri = paramUri2;
  }
  
  public static MediaDescriptionCompat fromMediaDescription(Object paramObject) { // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_0
    //   3: ifnull -> 197
    //   6: getstatic android/os/Build$VERSION.SDK_INT : I
    //   9: bipush #21
    //   11: if_icmplt -> 197
    //   14: new android/support/v4/media/MediaDescriptionCompat$Builder
    //   17: dup
    //   18: invokespecial <init> : ()V
    //   21: astore #4
    //   23: aload #4
    //   25: aload_0
    //   26: invokestatic getMediaId : (Ljava/lang/Object;)Ljava/lang/String;
    //   29: invokevirtual setMediaId : (Ljava/lang/String;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   32: pop
    //   33: aload #4
    //   35: aload_0
    //   36: invokestatic getTitle : (Ljava/lang/Object;)Ljava/lang/CharSequence;
    //   39: invokevirtual setTitle : (Ljava/lang/CharSequence;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   42: pop
    //   43: aload #4
    //   45: aload_0
    //   46: invokestatic getSubtitle : (Ljava/lang/Object;)Ljava/lang/CharSequence;
    //   49: invokevirtual setSubtitle : (Ljava/lang/CharSequence;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   52: pop
    //   53: aload #4
    //   55: aload_0
    //   56: invokestatic getDescription : (Ljava/lang/Object;)Ljava/lang/CharSequence;
    //   59: invokevirtual setDescription : (Ljava/lang/CharSequence;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   62: pop
    //   63: aload #4
    //   65: aload_0
    //   66: invokestatic getIconBitmap : (Ljava/lang/Object;)Landroid/graphics/Bitmap;
    //   69: invokevirtual setIconBitmap : (Landroid/graphics/Bitmap;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   72: pop
    //   73: aload #4
    //   75: aload_0
    //   76: invokestatic getIconUri : (Ljava/lang/Object;)Landroid/net/Uri;
    //   79: invokevirtual setIconUri : (Landroid/net/Uri;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   82: pop
    //   83: aload_0
    //   84: invokestatic getExtras : (Ljava/lang/Object;)Landroid/os/Bundle;
    //   87: astore_3
    //   88: aload_3
    //   89: ifnonnull -> 97
    //   92: aconst_null
    //   93: astore_1
    //   94: goto -> 107
    //   97: aload_3
    //   98: ldc 'android.support.v4.media.description.MEDIA_URI'
    //   100: invokevirtual getParcelable : (Ljava/lang/String;)Landroid/os/Parcelable;
    //   103: checkcast android/net/Uri
    //   106: astore_1
    //   107: aload_1
    //   108: ifnull -> 143
    //   111: aload_3
    //   112: ldc 'android.support.v4.media.description.NULL_BUNDLE_FLAG'
    //   114: invokevirtual containsKey : (Ljava/lang/String;)Z
    //   117: ifeq -> 131
    //   120: aload_3
    //   121: invokevirtual size : ()I
    //   124: iconst_2
    //   125: if_icmpne -> 131
    //   128: goto -> 145
    //   131: aload_3
    //   132: ldc 'android.support.v4.media.description.MEDIA_URI'
    //   134: invokevirtual remove : (Ljava/lang/String;)V
    //   137: aload_3
    //   138: ldc 'android.support.v4.media.description.NULL_BUNDLE_FLAG'
    //   140: invokevirtual remove : (Ljava/lang/String;)V
    //   143: aload_3
    //   144: astore_2
    //   145: aload #4
    //   147: aload_2
    //   148: invokevirtual setExtras : (Landroid/os/Bundle;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   151: pop
    //   152: aload_1
    //   153: ifnull -> 166
    //   156: aload #4
    //   158: aload_1
    //   159: invokevirtual setMediaUri : (Landroid/net/Uri;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   162: pop
    //   163: goto -> 184
    //   166: getstatic android/os/Build$VERSION.SDK_INT : I
    //   169: bipush #23
    //   171: if_icmplt -> 184
    //   174: aload #4
    //   176: aload_0
    //   177: invokestatic getMediaUri : (Ljava/lang/Object;)Landroid/net/Uri;
    //   180: invokevirtual setMediaUri : (Landroid/net/Uri;)Landroid/support/v4/media/MediaDescriptionCompat$Builder;
    //   183: pop
    //   184: aload #4
    //   186: invokevirtual build : ()Landroid/support/v4/media/MediaDescriptionCompat;
    //   189: astore_1
    //   190: aload_1
    //   191: aload_0
    //   192: putfield mDescriptionObj : Ljava/lang/Object;
    //   195: aload_1
    //   196: areturn
    //   197: aconst_null
    //   198: areturn }
  
  public int describeContents() { return 0; }
  
  @Nullable
  public CharSequence getDescription() { return this.mDescription; }
  
  @Nullable
  public Bundle getExtras() { return this.mExtras; }
  
  @Nullable
  public Bitmap getIconBitmap() { return this.mIcon; }
  
  @Nullable
  public Uri getIconUri() { return this.mIconUri; }
  
  public Object getMediaDescription() {
    if (this.mDescriptionObj != null || Build.VERSION.SDK_INT < 21)
      return this.mDescriptionObj; 
    Object object = MediaDescriptionCompatApi21.Builder.newInstance();
    MediaDescriptionCompatApi21.Builder.setMediaId(object, this.mMediaId);
    MediaDescriptionCompatApi21.Builder.setTitle(object, this.mTitle);
    MediaDescriptionCompatApi21.Builder.setSubtitle(object, this.mSubtitle);
    MediaDescriptionCompatApi21.Builder.setDescription(object, this.mDescription);
    MediaDescriptionCompatApi21.Builder.setIconBitmap(object, this.mIcon);
    MediaDescriptionCompatApi21.Builder.setIconUri(object, this.mIconUri);
    Bundle bundle2 = this.mExtras;
    Bundle bundle1 = bundle2;
    if (Build.VERSION.SDK_INT < 23) {
      bundle1 = bundle2;
      if (this.mMediaUri != null) {
        bundle1 = bundle2;
        if (bundle2 == null) {
          bundle1 = new Bundle();
          bundle1.putBoolean("android.support.v4.media.description.NULL_BUNDLE_FLAG", true);
        } 
        bundle1.putParcelable("android.support.v4.media.description.MEDIA_URI", this.mMediaUri);
      } 
    } 
    MediaDescriptionCompatApi21.Builder.setExtras(object, bundle1);
    if (Build.VERSION.SDK_INT >= 23)
      MediaDescriptionCompatApi23.Builder.setMediaUri(object, this.mMediaUri); 
    this.mDescriptionObj = MediaDescriptionCompatApi21.Builder.build(object);
    return this.mDescriptionObj;
  }
  
  @Nullable
  public String getMediaId() { return this.mMediaId; }
  
  @Nullable
  public Uri getMediaUri() { return this.mMediaUri; }
  
  @Nullable
  public CharSequence getSubtitle() { return this.mSubtitle; }
  
  @Nullable
  public CharSequence getTitle() { return this.mTitle; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.mTitle);
    stringBuilder.append(", ");
    stringBuilder.append(this.mSubtitle);
    stringBuilder.append(", ");
    stringBuilder.append(this.mDescription);
    return stringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt) {
    if (Build.VERSION.SDK_INT < 21) {
      paramParcel.writeString(this.mMediaId);
      TextUtils.writeToParcel(this.mTitle, paramParcel, paramInt);
      TextUtils.writeToParcel(this.mSubtitle, paramParcel, paramInt);
      TextUtils.writeToParcel(this.mDescription, paramParcel, paramInt);
      paramParcel.writeParcelable(this.mIcon, paramInt);
      paramParcel.writeParcelable(this.mIconUri, paramInt);
      paramParcel.writeBundle(this.mExtras);
      paramParcel.writeParcelable(this.mMediaUri, paramInt);
      return;
    } 
    MediaDescriptionCompatApi21.writeToParcel(getMediaDescription(), paramParcel, paramInt);
  }
  
  public static final class Builder {
    private CharSequence mDescription;
    
    private Bundle mExtras;
    
    private Bitmap mIcon;
    
    private Uri mIconUri;
    
    private String mMediaId;
    
    private Uri mMediaUri;
    
    private CharSequence mSubtitle;
    
    private CharSequence mTitle;
    
    public MediaDescriptionCompat build() { return new MediaDescriptionCompat(this.mMediaId, this.mTitle, this.mSubtitle, this.mDescription, this.mIcon, this.mIconUri, this.mExtras, this.mMediaUri); }
    
    public Builder setDescription(@Nullable CharSequence param1CharSequence) {
      this.mDescription = param1CharSequence;
      return this;
    }
    
    public Builder setExtras(@Nullable Bundle param1Bundle) {
      this.mExtras = param1Bundle;
      return this;
    }
    
    public Builder setIconBitmap(@Nullable Bitmap param1Bitmap) {
      this.mIcon = param1Bitmap;
      return this;
    }
    
    public Builder setIconUri(@Nullable Uri param1Uri) {
      this.mIconUri = param1Uri;
      return this;
    }
    
    public Builder setMediaId(@Nullable String param1String) {
      this.mMediaId = param1String;
      return this;
    }
    
    public Builder setMediaUri(@Nullable Uri param1Uri) {
      this.mMediaUri = param1Uri;
      return this;
    }
    
    public Builder setSubtitle(@Nullable CharSequence param1CharSequence) {
      this.mSubtitle = param1CharSequence;
      return this;
    }
    
    public Builder setTitle(@Nullable CharSequence param1CharSequence) {
      this.mTitle = param1CharSequence;
      return this;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/MediaDescriptionCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */