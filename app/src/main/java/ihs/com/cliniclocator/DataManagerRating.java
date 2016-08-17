package ihs.com.cliniclocator;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EMA on 9/3/2016.
 */
public class DataManagerRating extends RecyclerView.Adapter<DataManagerRating.RecyclerViewHolder> {

    private ArrayList<HashMap<String, String>> ratingList;

    public DataManagerRating(ArrayList<HashMap<String, String>> ratingList) {
        this.ratingList = ratingList;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mName, mRatingTitle, mRatingDetails, mCircleBil;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.CONTACT_name);
            mRatingTitle = (TextView) itemView.findViewById(R.id.CONTACT_rating_title);
            mRatingDetails = (TextView) itemView.findViewById(R.id.CONTACT_rating_details);
            mCircleBil = (TextView) itemView.findViewById(R.id.CONTACT_circle_bil);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rating_list, viewGroup, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int i) {
        // get the single element from the main array
        final HashMap<String, String> ratingMap = ratingList.get(i);
        // Set the values
        //Log.e("signinUsername:= ", ratingMap.get("signinUsername"));
        if (ratingMap.get("signinUsername") != null && !"".equalsIgnoreCase(ratingMap.get("signinUsername")) && !"null".equalsIgnoreCase(ratingMap.get("signinUsername"))) {
            viewHolder.mName.setText(ratingMap.get("signinUsername"));
        } else {
            viewHolder.mName.setText("Anonymous");
        }
        //viewHolder.mName.setText(ratingMap.get("signinUsername"));
        viewHolder.mRatingTitle.setText(ratingMap.get("title"));
        viewHolder.mRatingDetails.setText(ratingMap.get("description"));
        viewHolder.mCircleBil.setText(ratingMap.get("rate"));
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}
