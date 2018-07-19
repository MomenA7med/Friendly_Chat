package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Momen on 7/2/2018.
 */

public class UserAdapter extends ArrayAdapter<UserPro> {

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<UserPro> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.user_item, parent, false);
        }
        UserPro pro = getItem(position);
        TextView name =(TextView) convertView.findViewById(R.id.name);
        TextView mail =(TextView) convertView.findViewById(R.id.mail);
        name.setText(pro.getUserName());
        mail.setText(pro.getUserMail());
        return convertView;
    }
}
