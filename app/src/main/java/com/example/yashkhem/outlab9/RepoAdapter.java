package com.example.yashkhem.outlab9;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RepoAdapter extends ArrayAdapter<Repos> {

    private Context mContext;
    private List<Repos> repoList = new ArrayList<>();

    private class ViewHolder{
        TextView repoName;
        TextView repoAge;
        TextView repoDescription;
    }

    private int lastPosition = -1;

    public RepoAdapter(@NonNull Context context, ArrayList<Repos> list) {
        super(context, 0 , list);
        mContext = context;
        repoList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Repos currentRepo = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.test,parent,false);
            viewHolder.repoName = (TextView) convertView.findViewById(R.id.repo_name);
            viewHolder.repoAge = (TextView) convertView.findViewById(R.id.repo_age);
            viewHolder.repoDescription = (TextView) convertView.findViewById(R.id.repo_description);

            result = convertView;
            convertView.setTag(viewHolder);

        }

        else{
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.repoName.setText(currentRepo.getName());
        viewHolder.repoAge.setText(currentRepo.getAge());
        viewHolder.repoDescription.setText(currentRepo.getDescription());




        return convertView;
    }
}
