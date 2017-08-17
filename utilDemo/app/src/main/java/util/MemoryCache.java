package util;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片缓存
 */
public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache= Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());//软引用
    public Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref=cache.get(id);
        return ref.get();
    }
    
    public void put(String id, Bitmap bitmap){
        cache.put(id, new SoftReference<>(bitmap));
    }

    public void remove(String id){
        cache.remove(id);
    }

    public void clear() {
        cache.clear();
    }
}