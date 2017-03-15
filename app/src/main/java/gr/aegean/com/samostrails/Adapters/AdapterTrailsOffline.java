package gr.aegean.com.samostrails.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.R;


public class AdapterTrailsOffline extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private  LayoutInflater mInflater = null;
    ArrayList<Trail> trails;
    public AdapterTrailsOffline(Context context,ArrayList<Trail> trails) {

        if (context != null) {
            mInflater = LayoutInflater.from(context);
        }
        this.trails=trails;
        for(Trail trail:trails){
            mItems.add(new Item(trail.getTitle(),trail.getImage(),trail.getTrailId()));
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        final ImageView picture;
        TextView name;
        TextView trailid;
        final ImageView favorite;
        if (v == null) {
            v = mInflater.inflate(R.layout.mobile, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
            v.setTag(R.id.trailid,v.findViewById(R.id.trailid));
            v.setTag(R.id.favorite,v.findViewById(R.id.favorite));
        }
        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);
        trailid = (TextView)v.getTag(R.id.trailid);
        favorite = (ImageView)v.getTag(R.id.favorite);
        Item item = getItem(i);
        final Bitmap[] bmp = new Bitmap[1];
        picture.setImageBitmap(trails.get(i).getDownlImage());
        trailid.setVisibility(View.GONE);
        name.setText(item.name);
        trailid.setText(String.valueOf(item.trailid));
        favorite.setVisibility(View.GONE);
        return v;
    }

    private static class Item {
        public final String name;
        public final String drawableId;
        public final int trailid;
        Item(String name, String drawableId,int trailid) {
            this.name = name;
            this.drawableId = drawableId;
            this.trailid=trailid;
        }
    }
}