package de.tum.mw.lfe.drtrc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;


public class ControlFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "drtrc.ControlFragment";
    private static MainActivity mParent;
    private static NumberPicker mNumberPicker;
    private static EditText mCommandText;
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
    public static ControlFragment newInstance(MainActivity parent, int sectionNumber) {
        mParent = parent;

        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "ControlFragment started");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_control, container, false);
        ((MainActivity)getActivity()).addToReportView(null);

        ((Button)mRootView.findViewById(R.id.controlStartButton)).setOnClickListener(this);
        ((Button)mRootView.findViewById(R.id.controlStopButton)).setOnClickListener(this);
        ((Button)mRootView.findViewById(R.id.controlMarkerButton)).setOnClickListener(this);
        ((Button)mRootView.findViewById(R.id.controlSendCommandButton)).setOnClickListener(this);
        ((Button)mRootView.findViewById(R.id.controlMeasurementButton)).setOnClickListener(this);
        ((Button)mRootView.findViewById(R.id.controlTestButton)).setOnClickListener(this);


        mNumberPicker = (NumberPicker)mRootView.findViewById(R.id.controlNumberPicker);
        mNumberPicker.setValue(0);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(9);

        mCommandText = (EditText)mRootView.findViewById(R.id.controlCommandEditText);

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.controlStartButton:
                mParent.sendCommand((byte) '#');
                break;
            case R.id.controlStopButton:
                mParent.sendCommand((byte) '$');
                break;
            case R.id.controlMarkerButton:
                byte m = (byte)mNumberPicker.getValue();//marker
                mParent.sendCommand((byte) (48 + m));//add 48 > send ascii code e.g. 1 = ascii 49
                break;
            case R.id.controlMeasurementButton:
                mParent.sendCommand((byte) 'm');
                mParent.changeToFragment(0);//switch to report fragment. so, the measurement results can be seen.
                break;
            case R.id.controlTestButton:
                mParent.sendCommand((byte) 't');
                break;
            case R.id.controlSendCommandButton:
                String temp = mCommandText.getText().toString();
                for(int i=0;i<temp.length();i++){
                    char b = temp.charAt(i);
                    mParent.sendCommand((byte) b);
                }
                break;
            default:

        }

    }
}
