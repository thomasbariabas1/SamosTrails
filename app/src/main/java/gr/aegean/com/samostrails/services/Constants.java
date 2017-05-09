package gr.aegean.com.samostrails.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import gr.aegean.com.samostrails.R;

public class Constants {
    public interface ACTION {
         String MAIN_ACTION = "gr.aegean.com.samostrails.action.main";
         String TONGLE_ACTION = "gr.aegean.com.samostrails.action.prev";
         String PLAY_ACTION = "gr.aegean.com.samostrails.action.play";
        String CHECK_STATE = "gr.aegean.com.samostrails.action.checkstate";
         String STARTFOREGROUND_ACTION = "gr.aegean.com.samostrails.action.startforeground";
         String STOPFOREGROUND_ACTION = "gr.aegean.com.samostrails.action.stopforeground";
        String BACK_PRESSED = "gr.aegean.com.samostrails.action.backpressed";
        String REQUEST_ARGS="gr.aegean.com.samostrails.action.requestargs";
    }

     interface NOTIFICATION_ID {
         int FOREGROUND_SERVICE = 101;
    }

     static Bitmap getDefaultAlbumArt(Context context) {
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