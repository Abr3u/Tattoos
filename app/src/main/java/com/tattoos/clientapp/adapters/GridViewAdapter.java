package com.tattoos.clientapp.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tattoos.clientapp.R;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter<GridItem>implements Filterable {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    private ArrayList<GridItem> mGridDataFiltered = new ArrayList<GridItem>();
    private ItemFilter mFilter = new ItemFilter();

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = data;
        this.mGridDataFiltered = data;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridDataFiltered.get(position);

        if(item.isTattoo()){
            holder.titleTextView.setText(item.getTattoo_title());
            Glide.with(mContext)
                    .load(item.getTattoo_url())
                    .centerCrop()
                    .crossFade()
                    .into(holder.imageView);
        }else{
            holder.titleTextView.setText(item.getArtist_name());
            Glide.with(mContext)
                    .load(item.getArtist_url())
                    .centerCrop()
                    .crossFade()
                    .into(holder.imageView);
        }
        return row;
    }

    @Override
    public int getCount() {
        return mGridDataFiltered.size();
    }

    @Override
    public GridItem getItem(int position) {
        return mGridDataFiltered.get(position);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<GridItem> list = mGridData;

            int count = list.size();
            final ArrayList<GridItem> nlist = new ArrayList<GridItem>(count);

            String titleStr ;
            String styleStr ;
            String bodyPartStr ;
            String artistStr ;
            if(count != 0 && list.get(0).isTattoo()){
                //title
                for (int i = 0; i < count; i++) {
                    titleStr = list.get(i).getTattoo_title();
                    styleStr = list.get(i).getTattoo_style();
                    bodyPartStr = list.get(i).getTatto_body_part();
                    artistStr = list.get(i).getTattoo_artist();
                    if (titleStr.toLowerCase().contains(filterString) || styleStr.toLowerCase().contains(filterString)
                            || bodyPartStr.toLowerCase().contains(filterString) || artistStr.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
            }else{
                String nameStr ;
                String localityStr ;
                //artist name
                for (int i = 0; i < count; i++) {
                    nameStr = list.get(i).getArtist_name();
                    localityStr = list.get(i).getArtist_locality();
                    if (nameStr.toLowerCase().contains(filterString) || localityStr.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
            }


            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mGridDataFiltered = (ArrayList<GridItem>) results.values;
            notifyDataSetChanged();
        }

    }
}