package gr.aegean.com.samostrails.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.aegean.com.samostrails.MainActivity;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.R;
import gr.aegean.com.samostrails.SQLDb.TrailDb;
import gr.aegean.com.samostrails.Utilities;

/**
 * Created by Ravi on 13/05/15.
 */
public class AdapterSwipeRefresh extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<>();
    private  LayoutInflater mInflater = null;
    ArrayList<Trail> trails;
    LruCache<Integer, Bitmap> bitmapCache ;

    public AdapterSwipeRefresh(Context context,ArrayList<Trail> trails ,LruCache<Integer, Bitmap> bitmapCache ) {
          if (context != null) {
            mInflater = LayoutInflater.from(context);
        }
        this.trails=trails;
        this.bitmapCache=bitmapCache;
        //Log.e("test","view");
        for(Trail trail:trails){
            //Log.e("test","view"+trail);
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
        // Log.e("test","view"+v);
        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);
        trailid = (TextView)v.getTag(R.id.trailid);
        favorite = (ImageView)v.getTag(R.id.favorite);
        // Log.e("test","view"+v);
        Item item = getItem(i);
        final Bitmap[] bmp = new Bitmap[1];
        //Log.e("test","view"+v);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(bitmapCache.get(trails.get(i).getTrailId())==null) {
                        if (Utilities.isNetworkAvailable(mInflater.getContext())) {
                            InputStream in = new URL(trails.get(i).getImage()).openStream();
                            bmp[0] = BitmapFactory.decodeStream(in);
                            trails.get(i).setDownlImage(bmp[0]);
                            bitmapCache.put(trails.get(i).getTrailId(),bmp[0]);
                        }
                    }else{
                        trails.get(i).setDownlImage(bitmapCache.get(trails.get(i).getTrailId()));
                    }

                } catch (Exception e) {
                    // log error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {


                picture.setImageBitmap(trails.get(i).getDownlImage());

            }

        }.execute();
        trailid.setVisibility(View.GONE);
        name.setText(item.name);
        trailid.setText(String.valueOf(item.trailid));
        if (TrailDb.ifExists(trails.get(i),TrailDb.initiateDB(v.getContext()))){
            favorite.setImageDrawable(v.getResources().getDrawable(R.drawable.heart_filled));
        }


        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TrailDb.ifExists(trails.get(i),TrailDb.initiateDB(v.getContext()))){

                    TrailDb.insertIntoDb(trails.get(i), TrailDb.initiateDB(v.getContext()));
                    favorite.setImageDrawable(v.getResources().getDrawable(R.drawable.heart_filled));

                }else{
                    TrailDb.delete(trails.get(i), TrailDb.initiateDB(v.getContext()));
                    favorite.setImageDrawable(v.getResources().getDrawable(R.drawable.heart));

                }
            }
        });
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