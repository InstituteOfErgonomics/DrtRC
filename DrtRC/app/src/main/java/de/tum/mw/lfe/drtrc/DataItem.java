package de.tum.mw.lfe.drtrc;


public class DataItem {

    public String mId;
    public String mName;
    public String mDesc;
    public Boolean mIsTimeValue;//in microseconds
    public Boolean mViewEnabled;
    public Boolean mToastEnabled;
    public String mResult;

    public static final Boolean IS_TIME_VALUE = true;
    public static final Boolean NO_TIME_VALUE = false;
    public static final Boolean SHOW_IN_MYVIEW_DEFAULT = true;
    public static final Boolean HIDE_IN_MYVIEW_DEFAULT = false;
    public static final Boolean TOAST_DEFAULT = true;
    public static final Boolean NOT_TOAST_DEFAULT = false;

    public DataItem(String id, String name, String desc, Boolean isTimeValue, Boolean defaultToastValue, Boolean defaultViewValue){
        mId = id;
        mName = name;
        mDesc = desc;
        mIsTimeValue = isTimeValue;
        mToastEnabled = defaultToastValue;
        mViewEnabled = defaultViewValue;
    }
}
