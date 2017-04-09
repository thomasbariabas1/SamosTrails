package gr.aegean.com.samostrails;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by phantomas on 4/9/2017.
 */


public class CustomGridView extends GridView {

    public CustomGridView(Context context) {
        super(context);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* ADD THIS */
    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }
}