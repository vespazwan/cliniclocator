package ihs.com.cliniclocator;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EMA on 9/3/2016.
 */
public class DataManager extends RecyclerView.Adapter<DataManager.RecyclerViewHolder> {

    private ArrayList<HashMap<String, String>> clinicList;

    public DataManager(ArrayList<HashMap<String, String>> clinicList) {
        this.clinicList = clinicList;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mPhone, mCircleJarak;
        View mCircle;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.CONTACT_name);
            mPhone = (TextView) itemView.findViewById(R.id.CONTACT_phone);
            mCircleJarak = (TextView) itemView.findViewById(R.id.CONTACT_circle_jarak);
//            mCircle = itemView.findViewById(R.id.CONTACT_circle);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.clinic_list, viewGroup, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int i) {
        // get the single element from the main array
        final HashMap<String, String> clinicMap = clinicList.get(i);
        // Set the values
        viewHolder.mName.setText(clinicMap.get("name"));
        viewHolder.mPhone.setText(clinicMap.get("address"));
        viewHolder.mCircleJarak.setText(clinicMap.get("jarak"));
        // Set the color of the shape
//        GradientDrawable bgShape = (GradientDrawable) viewHolder.mCircle.getBackground();
        //bgShape.setColor(Color.parseColor("#FF4081"));
//        bgShape.setColor(Color.parseColor("#03C9A9"));
    }

    @Override
    public int getItemCount() {
        //return Contact.CONTACTS.length;
        return clinicList.size();
    }
}
