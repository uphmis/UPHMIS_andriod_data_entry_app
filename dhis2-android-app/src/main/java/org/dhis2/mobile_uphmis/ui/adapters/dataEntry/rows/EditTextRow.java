package org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows;

import android.widget.TextView;

public class EditTextRow  {

    protected TextView.OnEditorActionListener mOnEditorActionListener;


    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener){
        mOnEditorActionListener = onEditorActionListener;
    }


}


