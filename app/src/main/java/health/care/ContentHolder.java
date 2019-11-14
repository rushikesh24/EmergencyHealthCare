package health.care;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rushi on 17-Jan-18.
 */

public class ContentHolder extends RecyclerView.ViewHolder{
    View mView;
    ImageView call;

    public ContentHolder(View itemView){
        super(itemView);
        mView = itemView;
        call = mView.findViewById(R.id.call);

    }

    public void setName(String name){
        TextView provider_name = mView.findViewById(R.id.namedisplay);
        provider_name.setText(name);
    }

    public void setAddress(String address){
        TextView provider_address = mView.findViewById(R.id.addressdisplay);
        provider_address.setText(address);
    }

    public void setMobileno(String mobileno){
        TextView post_mobileno = mView.findViewById(R.id.mobilenumber);
        post_mobileno.setText(mobileno);
    }

}



