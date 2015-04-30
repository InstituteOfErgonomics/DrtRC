package de.tum.mw.lfe.drtrc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class MyViewFragment extends Fragment {
    private static final String TAG = "drtrc.MyViewFragment";
    private static MainActivity mParent;
    private static ArrayList<DataItem> mDataItems;
    private static ListAdapter mAdapter;
    private static View mRootView;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyViewFragment newInstance(MainActivity parent, ArrayList<DataItem> dataItems, int sectionNumber) {
        mParent = parent;
        mDataItems = dataItems;

        MyViewFragment fragment = new MyViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "MyViewFragment started");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_myview, container, false);

        mAdapter = new MyViewAdapter(mParent,mDataItems);
        ListView listview = (ListView)mRootView.findViewById(R.id.myViewListView);
        listview.setAdapter(mAdapter);

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public static ListAdapter getAdapter() {
        return mAdapter;
    }
}
