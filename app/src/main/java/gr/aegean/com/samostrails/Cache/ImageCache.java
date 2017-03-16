package gr.aegean.com.samostrails.Cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by phantomas on 3/16/2017.
 */

public class ImageCache extends LruCache<String, Bitmap> {

    public ImageCache( int maxSize ) {
        super( maxSize );
    }

    @Override
    protected int sizeOf( String key, Bitmap value ) {
        return value.getByteCount();
    }

    @Override
    protected void entryRemoved( boolean evicted, String key, Bitmap oldValue, Bitmap newValue ) {
        oldValue.recycle();
    }

}