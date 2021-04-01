/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile_uphmis.ui.adapters.dataEntry.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.io.models.Field;

public class NotSupportedRow implements Row {
    private final LayoutInflater inflater;
    private final Field field;
    public boolean readOnly = false;
    
    public NotSupportedRow(LayoutInflater inflater, Field field) {
        this.inflater = inflater;
        this.field = field;
    }

    @Override
    public View getView(View convertView) {
        View view;
        TextHolder holder;

        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_not_supported, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);

            holder = new TextHolder(label);
            rowRoot.setTag(holder);
            view = rowRoot;
        } else {
            view = convertView;
            holder = (TextHolder) view.getTag();
        }

        RowCosmetics.setTextLabel(field, holder.textLabel);
        return view;
    }

    @Override
    public int getViewType() {
        return RowTypes.NO_SUPPORTED_ROW.ordinal();
    }

    @Override
    public void setReadOnly(boolean value) {
        readOnly = value;
    }
}
class TextHolder {
    final TextView textLabel;

    TextHolder(TextView textLabel) {
        this.textLabel = textLabel;
    }
}