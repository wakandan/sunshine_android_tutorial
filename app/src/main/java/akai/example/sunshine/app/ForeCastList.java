package akai.example.sunshine.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import akai.example.sunshine.model.ForeCast;

/**
 * Created by akai on 5/10/15.
 */
public class ForeCastList extends ArrayAdapter<ForeCast> {
    private final Activity context;

    public ForeCastList(Activity context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.forecast_list_single, parent, true);
        TextView textView = (TextView) rowView.findViewById(R.id.forecast_item_txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.forecast_item_img);
        return rowView;
    }
}
