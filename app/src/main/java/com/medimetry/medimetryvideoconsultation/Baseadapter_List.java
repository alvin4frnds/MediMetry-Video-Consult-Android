package com.medimetry.medimetryvideoconsultation;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.medimetry.medimetryvideoconsultation.VideoCalling.Service_IncomingCall;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Dronacharya on 12/4/2017.
 */

public class Baseadapter_List extends RecyclerView.Adapter<Baseadapter_List.ViewHolder>
{
    Context context;
    List<Data_UserName> data_userNames;
    SharedPrefrenceClass sharedPrefrenceClass;

    public Baseadapter_List(Context context1,List<Data_UserName> du)
    {
        context=context1;
        data_userNames=du;
        sharedPrefrenceClass=new SharedPrefrenceClass(context);
    }

    @Override
    public Baseadapter_List.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.baseadapter_list,parent,false);
        ViewHolder viewholder=new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Baseadapter_List.ViewHolder holder, final int position) {


        holder.phoneNumber.setText(""+data_userNames.get(position).getPhoneNumber());
 holder.userName.setText(""+data_userNames.get(position).getUserName());


        holder.markOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefrenceClass.setLogedInId(""+data_userNames.get(position).getUserId());
              context.startService(new Intent(context, Service_IncomingCall.class));
                Log.e("UserId",""+data_userNames.get(position).getUserId());
                Toast.makeText(context,"User Set Online",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data_userNames.size();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder
    {


        TextView userName;
        TextView phoneNumber;
        TextView markOnline;
        public ViewHolder(View itemView) {
            super(itemView);
            markOnline=(TextView)itemView.findViewById(R.id.markOnline);

             userName=(TextView)itemView.findViewById(R.id.userName);
             phoneNumber=(TextView)itemView.findViewById(R.id.phoneNumber);
        }
    }
}
