package com.example.asus.elephant;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;


public class HerdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<CreateUser> membersList;
    Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, CreateUser obj, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    HerdAdapter(ArrayList<CreateUser> membersList, Context mContext) {
        this.membersList = membersList;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return membersList.size();  // How many members in your herd?
    }


    // The ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        RecyclerView.ViewHolder mHerdViewHolder = new HerdViewHolder(v, mContext, membersList);

        return mHerdViewHolder;
    }

    public class HerdViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        ImageView mStatus;
        Context mContext;
        ArrayList<CreateUser> membersList;
        FirebaseAuth auth;
        FirebaseUser user;
        RelativeLayout layoutBox;

        public HerdViewHolder(View itemView, Context mContext, ArrayList<CreateUser> membersList) {
            super(itemView);
            this.mContext = mContext;
            this.membersList = membersList;
//            itemView.setOnClickListener(this);
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            mName = itemView.findViewById(R.id.memberName);
            mStatus = itemView.findViewById(R.id.status);
            layoutBox = (RelativeLayout) itemView.findViewById(R.id.box);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder herdViewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");    // for debugging purposes.

        final CreateUser mUserObj = membersList.get(i);   // i is the position.
        if(herdViewHolder instanceof HerdViewHolder) {
            HerdViewHolder view = (HerdViewHolder) herdViewHolder;
            view.mName.setText(mUserObj.name);
            view.layoutBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, membersList.get(i), i);

                        String memberId = mUserObj.userId;

                        // This part below is for debugging purposes.
                        // It returns jy if click on the user!
//                        String userName = mUserObj.name;
//                        Toast.makeText(mContext, userName, Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }

    // When user clicks on his/her herd member.

//        @Override
//        public void onClick(View v) {
//            Toast.makeText(mContext, R.string.selected_member, Toast.LENGTH_SHORT).show();
//        }

}
