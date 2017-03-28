package gr.aegean.com.samostrails.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import gr.aegean.com.samostrails.R;

/**
 * Created by phantomas on 3/21/2017.
 */

public class Constants {
    public interface ACTION {
         String MAIN_ACTION = "gr.aegean.com.samostrails.action.main";
         String PREV_ACTION = "gr.aegean.com.samostrails.action.prev";
         String PLAY_ACTION = "gr.aegean.com.samostrails.action.play";
         String STARTFOREGROUND_ACTION = "gr.aegean.com.samostrails.action.startforeground";
         String STOPFOREGROUND_ACTION = "gr.aegean.com.samostrails.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
         int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.white_0, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}