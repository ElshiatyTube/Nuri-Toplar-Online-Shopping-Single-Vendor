package com.eatitappclient.tws.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eatitappclient.tws.R;

public class ChoiseLanguageAdapter extends BaseAdapter {

    private Context mContext;
    private final int[] Imageid;
    private final int[] web;
    ImageView imageView;
    public ChoiseLanguageAdapter(Context c, int[] web, int[] Imageid )
    {
        mContext = c;
        this.Imageid = Imageid;
        this.web=web;
    }

    @Override
    public int getCount()
    {
        return Imageid.length;
    }
    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup
            parent)
    {

        // TODO Auto-generated method stub

        TextView textView;

        if (convertView == null) {
            LayoutInflater layoutInflator = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            convertView = layoutInflator.inflate(R.layout.frageticket, parent, false);
        }

        textView = (TextView) convertView.findViewById(R.id.Text2);
        textView.setText(web[position]);
        imageView  = (ImageView) convertView.findViewById(R.id.Image);



        // tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        //   tvUsername.setText(map.get(FragmentHome.USERNAME));
        //  tvTitle.setText(map.get(FragmentHome.TITLE));

        imageView.setImageResource(Imageid[position]);



        return convertView;
    }
}
