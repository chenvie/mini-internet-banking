package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private List<Transaction> listTrans;
    private Context mContext;

    public HistoryAdapter(List<Transaction> listTrans, Context mContext){
        this.listTrans = listTrans;
        this.mContext = mContext;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        NumberFormat formatter = new DecimalFormat("#,###");

        Transaction trans = listTrans.get(position);
        holder.history_date.setText(trans.getDate());
        holder.history_goal.setText(trans.getGoal());
        holder.history_nominal.setText("Rp " + String.valueOf(formatter.format(trans.getNominal())) + ",-");
        holder.history_info.setText(trans.getInfo());
        holder.history_status.setText(trans.getStatus());
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder{
        private TextView history_date, history_goal, history_nominal, history_info, history_status;

        public HistoryHolder(View itemView) {
            super(itemView);

            history_date = itemView.findViewById(R.id.history_date);
            history_goal = itemView.findViewById(R.id.history_goal);
            history_nominal = itemView.findViewById(R.id.history_nominal);
            history_info = itemView.findViewById(R.id.history_info);
            history_status = itemView.findViewById(R.id.history_status);
        }
    }
}
