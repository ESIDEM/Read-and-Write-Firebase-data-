package ng.com.techdepo.firebaserw;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ESIDEM jnr on 2/3/2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView nameTextView;
    public TextView ageTextView;
    public TextView levelTextView;

    public MessageViewHolder(View v) {
        super(v);
        nameTextView = (TextView) itemView.findViewById(R.id.staff_name);
        ageTextView = (TextView) itemView.findViewById(R.id.staff_age);
        levelTextView = (TextView) itemView.findViewById(R.id.staff_level);
    }
}
