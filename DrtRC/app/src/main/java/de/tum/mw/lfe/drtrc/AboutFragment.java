package de.tum.mw.lfe.drtrc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment {
    private static final String TAG = "drtrc.AboutFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "AboutFragment started");
    }



    private String getVersionString(){
        String retString = "";
        String appVersionName = "";
        int appVersionCode = 0;
        try{
            appVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0 ).versionName;
            appVersionCode= getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0 ).versionCode;
        }catch (Exception e) {
            Log.e(TAG, "getVersionString failed: "+e.getMessage());
        }

        retString = "V"+appVersionName+"."+appVersionCode;

        return retString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity)getActivity()).addToReportView(null);//call with (null) just to refresh the reportView

        String tempStr = getVersionString();
        tempStr += "<br/><br/>This is an open source implementation of an application to control an Arduino Detection Response Task (DRT) via USB OTG connection.";
        tempStr += "<br/><br/> (c) Michael Krause <a href=\"mailto:krause@tum.de\">krause@tum.de</a> <br/>2015 Institute of Ergonomics, TUM";
        tempStr += "<br/><br/>More information on <br/><a href=\"http://www.lfe.mw.tum.de/drt-rc\">http://www.lfe.mw.tum.de/drt-rc</a>";
        tempStr += "<br/><br/>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.";



        final SpannableString s = new SpannableString(Html.fromHtml(tempStr));
        Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        TextView tv =  (TextView)rootView.findViewById(R.id.aboutTextView);
        tv.setText(s);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
