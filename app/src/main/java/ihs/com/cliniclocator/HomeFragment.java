package ihs.com.cliniclocator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link -HomeFragment.-OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String parcelableKey = "clinicparcelable";
    private static ArrayList<Clinic> clinics;
    private static ArrayList<HashMap<String, String>> clinicList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rv;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadActivity();
    }

    private void loadActivity(){
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);

            //get parcelable
            clinics = getArguments().getParcelableArrayList(parcelableKey);
            LoadClinicLists(clinics);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.homeSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        rv = (RecyclerView) rootView.findViewById(R.id.rv); // layout reference

        //animation
        DataManager adapter = new DataManager(clinicList);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);
        alphaAdapter.setDuration(1000);
        alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));
        rv.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true); // to improve performance

        rv.addOnItemTouchListener( // and the click is handled
                new RecyclerClickListener(getContext(), new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Clinic clinic = clinics.get(position);

                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(DetailsActivity.ID, clinic);
                        intent.putExtra(DetailsActivity.FROM, "");
                        intent.putParcelableArrayListExtra(parcelableKey, clinics);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                new Pair<View, String>(view.findViewById(R.id.CONTACT_circle_jarak),
                                        getString(R.string.transition_name_circle)),
                                new Pair<View, String>(view.findViewById(R.id.CONTACT_name),
                                        getString(R.string.transition_name_name)),
                                new Pair<View, String>(view.findViewById(R.id.CONTACT_phone),
                                        getString(R.string.transition_name_phone))
                        );
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }
                }));

        //rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());

        return rootView;
    }

    private void LoadClinicLists(ArrayList<Clinic> clinics){
        clinicList = new ArrayList<HashMap<String, String>>();
        if (clinics.size() > 0) {
            for (int idx = 0; idx < clinics.size(); idx++) {
                Clinic clinic = clinics.get(idx);
                HashMap<String, String> clinicMap = new HashMap<String, String>();
                clinicMap.put("name", clinic.getName());
                clinicMap.put("jarak", clinic.getJarak());
                clinicMap.put("lat", clinic.getLat());
                clinicMap.put("lng", clinic.getLng());
                clinicMap.put("address", clinic.getAddress());
                clinicMap.put("photo", clinic.getPhoto());
                clinicMap.put("response", clinic.getResponse());
                clinicMap.put("mylat", clinic.getMylat());
                clinicMap.put("mylng", clinic.getMylng());
                clinicMap.put("jenis", clinic.getJenis());
                clinicList.add(clinicMap);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void refreshItems() {
        // Load items
        final LoadClinicList loadClinicList = new LoadClinicList(getContext());

        // check internet connection
        ConnectionDetector cd = new ConnectionDetector(getContext());
        final Boolean isInternetPresent = cd.isConnectingToInternet(); // true or false

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.d("clinic size",""+loadClinicList.getClinics().size());
                if (isInternetPresent){
                    if(loadClinicList.getClinics().size() > 0){
                        LoadClinicLists(loadClinicList.getClinics());
                        // Load complete
                        onItemsLoadComplete();
                    } else {
                        Toast.makeText(getContext(), "Failed to update. Please refresh again.", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, 2000);
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        DataManager adapter = new DataManager(clinicList);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(true);
        alphaAdapter.setDuration(1000);
//        alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));
        rv.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        Toast.makeText(getContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
