package de.tum.mw.lfe.drtrc;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//------------------------------------------------------
//Revision History 'Remote Control Android App to control Arduino DRT'
//------------------------------------------------------
//Version	Date			Author				Mod
//1		    April, 2015		Michael Krause		initial
//
//------------------------------------------------------

/*
        LGPL

        Copyright (c) 2015 Michael Krause (krause@tum.de), Institute of Ergonomics, Technische Universität München

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    private static final String TAG = "drtrc.ItemListActivity";
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final int FRAGMENT_MYVIEW = 3;

    private Boolean mInformUserFlag = false;//communication to runnable
    private Toast mToast = null;

    public ArrayList<DataItem> mDataItems = new ArrayList<DataItem>(){{
        add(new DataItem("cnt", "Stimulus Count", "within experiment", DataItem.NO_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("stmT", "Stimulus Time", "Microseconds from experiment start of this stimulus ", DataItem.IS_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("onstDly", "Stimulus onset delay", "Microseconds deviations to planed stimulus onset", DataItem.IS_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("soa", "Stimulus Onset Asychrony", "from this stimulus to the stimulus before", DataItem.IS_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("soaNxt", "Stimulus Onset Asychrony Next", "when is the nex stimulus planed", DataItem.IS_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("rt", "Reaction Time ", "", DataItem.IS_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("rslt", "Result", "'H' hit, 'M' miss or 'C' cheat. status messages: 'R' ready to start, '#' experiment started, '$' experiment stopped, 'N' no sd card, 'E' error while logging", DataItem.NO_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("meanRt", "Mean Reaction Time", "Average RT in this experiment", DataItem.IS_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("hCnt", "Hit Count", "How many hits in this experiment", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("mCnt", "Miss Count", "How many misses in this experiment", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("cCnt", "Cheat Count", "How many cheats in this experiment", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("hitRate", "Hit Rate", "Percentage of hits in this experiment", DataItem.NO_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("marker", "Marker", "Which marker is active", DataItem.NO_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("edgs", "Edges", "How many electrical edges where detected on the button", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("edgsDbncd", "Edges Debounced", "The edges where reduced/debounced to x edges", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("hold", "Hold time", "The time the button was hold down for the stimulus before", DataItem.IS_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("btnDwnC", "Button Down Count", "How often the button was pressed in this experiment", DataItem.NO_TIME_VALUE, DataItem.TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
        add(new DataItem("fileN", "File Number", "The file number for the logging file on SD card", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.HIDE_IN_MYVIEW_DEFAULT));
        add(new DataItem("pwm", "PWM value", "Stimulus strength", DataItem.NO_TIME_VALUE, DataItem.NOT_TOAST_DEFAULT, DataItem.SHOW_IN_MYVIEW_DEFAULT));
    }};




    private static UsbSerialPort sPort = null;
    private static UsbSerialDriver mDriver = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private MyListenerAdapter mListener = null;
    private Handler mHandler = new Handler();
    private Context mContext;
    protected static final String GET_USB_PERMISSION = "GetUsbPermission";


    private LinkedList mReportHistory = new LinkedList();//last REPORT_LINES lines for report/log fragment
    public static final int REPORT_LINES = 100;

    public void loadPrefs(){
        SharedPreferences settings = this.getSharedPreferences(TAG, MODE_PRIVATE);
        for (DataItem d : mDataItems){
            d.mViewEnabled = settings.getBoolean(d.mId+"View", d.mViewEnabled);
            d.mToastEnabled = settings.getBoolean(d.mId+"Toast", d.mToastEnabled);
        }
    }
    public void savePrefs(){
        SharedPreferences settings = getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for (DataItem d : mDataItems){
            editor.putBoolean(d.mId+"View", d.mViewEnabled);
            editor.putBoolean(d.mId+"Toast", d.mToastEnabled);
        }
        // Commit the edits!
        editor.commit();
    }
    public String getReportHistory(){

        synchronized(mReportHistory) {
            String ret = null;
            StringBuilder temp = new StringBuilder();
                temp.append("");
                for (int i =  0; i < mReportHistory.size(); i++) {
                    temp.append(mReportHistory.get(i));
                    temp.append('\n');
                }
            ret = temp.toString();
        return ret;
        }//sync
    }

    private void pushToReport(String s){
        synchronized(mReportHistory) {
            if (s != null) mReportHistory.addLast(s);
            if (mReportHistory.size() > REPORT_LINES) {
                mReportHistory.remove(0);
            }
        }//sync

        parsePacket(s);
    }

    private void parsePacket(String s){
        if (s == null) return;
        if (!s.startsWith("cnt")) return; //this is not a packet

        try {
            String[] parts = s.split(";");
            for (String p : parts) {
                String[] temp = p.split(":");
                String key = temp[0];
                String value = temp[1];
                for (DataItem d : mDataItems) {
                    if (d.mId.equals(key)){
                        if (d.mIsTimeValue){
                            Long l = Long.parseLong(value) / 1000;//convert time values from microseconds to milliseconds
                            d.mResult = Long.toString(l);
                        }else{
                            d.mResult = value;
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "parsePacket(): " + e.getMessage());
        }
        mInformUserFlag = true;//set flag so thread/runnable can inform user
    }

    public void	addToReportView(String s){//refresh and add string 's'
        pushToReport(s);
        if (s != null) pushToReport("-------------");
        mHandler.post(new refreshReportViewR());

    }

    private void refreshMyResultView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.container);
        int sec = f.getArguments().getInt(ARG_SECTION_NUMBER);
        MyViewAdapter a = null;
        if (sec == FRAGMENT_MYVIEW){
            a = (MyViewAdapter)((MyViewFragment)f).getAdapter();
            a.notifyDataSetChanged();
            //a.clear();
            //a.addAll(mDataItems);
        }
    }

    private void toastNews(){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (DataItem d : mDataItems) {
            if (d.mToastEnabled){
                if (count > 0) sb.append("\n");
                sb.append(d.mName);
                sb.append(": ");
                sb.append(d.mResult);
                count++;
            }
        }
        String temp = sb.toString();
        if (count > 0) toasting(temp, 2000);
    }

    class refreshReportViewR implements Runnable {
    @Override
        public void run() {

            if(mInformUserFlag){
                refreshMyResultView();
                toastNews();
                mInformUserFlag = false;
            }

            TextView tv = (TextView) findViewById(R.id.reportTextView);
            synchronized(mReportHistory) {
                String temp = getReportHistory();
                if (tv != null) tv.setText(temp);//refresh mReportView
            }//sync

            //scroll to last position
            ScrollView sv = (ScrollView)findViewById(R.id.reportScrollView);
            if (sv != null){
                sv.smoothScrollTo(0, tv.getBottom());
                //sv.fullScroll(ScrollView.FOCUS_DOWN);
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        mContext = this;


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void changeToFragment(int position){

        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = ReportFragment.newInstance(position + 1);
                break;
            case 1:
                fragment = ControlFragment.newInstance(this, position + 1);
                break;
            case (FRAGMENT_MYVIEW -1):
                fragment = MyViewFragment.newInstance(this, mDataItems,position + 1);
                break;
            case 3:
                fragment = MyViewConfigFragment.newInstance(this, mDataItems, position + 1);
                break;
            case 4:
                fragment = AboutFragment.newInstance(position + 1);
                break;
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        changeToFragment(position);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadPrefs();

        initUsb();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);

        savePrefs();

        stopIoManager();

        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }

    }


    private void initUsb(){
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // Find all available drivers from attached devices.
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            l("No device/driver");
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver mDriver = availableDrivers.get(0);
        //are we allowed to access?
        UsbDevice device = mDriver.getDevice();

        if (!manager.hasPermission(device)){
            //ask for permission
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(GET_USB_PERMISSION), 0);
            mContext.registerReceiver(mPermissionReceiver,new IntentFilter(GET_USB_PERMISSION));
            manager.requestPermission(device, pi);
            l("USB ask for permission");
            return;
        }



        UsbDeviceConnection connection = manager.openDevice(mDriver.getDevice());
        if (connection == null) {
            l("USB connection == null");
            return;
        }

        try {
            sPort = (UsbSerialPort)mDriver.getPorts().get(0);//Most have just one port (port 0)
            sPort.open(connection);
            sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            l("Error setting up device: " + e.getMessage());
            try {
                sPort.close();
            } catch (IOException e2) {/*ignore*/}
            sPort = null;
            return;
        }

        onDeviceStateChange();

    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            l("Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
            mListener = null;
        }
    }
    private void startIoManager() {
        if (sPort != null) {
            l("Starting io manager ..");
            mListener = new MyListenerAdapter(this);
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }
    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }



    public void toasting(final String msg, final int duration){
        if (mToast != null) mToast.cancel();
        Context context = getApplicationContext();
        CharSequence text = msg;
        mToast = Toast.makeText(context, text, duration);
        mToast.setDuration(duration);
        mToast.show();
    }

    public void l(String s){
        Log.v(TAG,s);
        addToReportView(s);
    }

    public void sendCommand(byte b){
        byte[] temp = new byte[1];
        temp[0] = b;
        try {
            if (sPort != null) sPort.write(temp,200);
        } catch (IOException e) {
            l("sendCommand err:" +e.getMessage());
        }
    }


    private PermissionReceiver mPermissionReceiver = new PermissionReceiver();
    private class PermissionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mContext.unregisterReceiver(this);
            if (intent.getAction().equals(GET_USB_PERMISSION)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    l("USB we got permission");
                    if (device != null){
                        initUsb();
                    }else{
                        l("USB perm receive device==null");
                    }

                } else {
                    l("USB no permission");
                }
            }
        }

    }


}
