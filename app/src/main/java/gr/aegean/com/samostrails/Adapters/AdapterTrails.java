package gr.aegean.com.samostrails.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.R;




public class AdapterTrails extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private  LayoutInflater mInflater = null;
    ArrayList<Trail> trails;
    public AdapterTrails(Context context,ArrayList<Trail> trails) {

        if (context != null) {
            mInflater = LayoutInflater.from(context);
        }
        this.trails=trails;
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
        //Log.e("test","view"+v);
        if (v == null) {
            v = mInflater.inflate(R.layout.mobile, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
            v.setTag(R.id.trailid,v.findViewById(R.id.trailid));
        }
       // Log.e("test","view"+v);
        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);
        trailid = (TextView)v.getTag(R.id.trailid);
       // Log.e("test","view"+v);
        Item item = getItem(i);
        final Bitmap[] bmp = new Bitmap[1];
        //Log.e("test","view"+v);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = new URL(trails.get(i).getImage()).openStream();
                    bmp[0] = BitmapFactory.decodeStream(in);
                    trails.get(i).setDownlImage(bmp[0]);
                } catch (Exception e) {
                    // log error
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp[0] != null)

                    picture.setImageBitmap(bmp[0]);

            }

        }.execute();
        trailid.setVisibility(View.GONE);
        name.setText(item.name);
        trailid.setText(String.valueOf(item.trailid));
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