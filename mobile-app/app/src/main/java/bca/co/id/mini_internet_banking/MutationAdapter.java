package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MutationAdapter extends RecyclerView.Adapter<MutationAdapter.MutationHolder> {
    private List<Transaction> listTrans;
    private Context mContext;

    public MutationAdapter(List<Transaction> listTrans, Context mContext){
        this.listTrans = listTrans;
        this.mContext = mContext;
    }

    @Override
    public MutationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mutation, parent, false);
        return new MutationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MutationHolder holder, int position) {
        Transaction trans = listTrans.get(position);
        holder.mutation_date.setText(trans.getDate());
        holder.mutation_type.setText(trans.getType());
        holder.mutation_nominal.setText(String.valueOf(trans.getNominal()));
        holder.mutation_info.setText(trans.getInfo());
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    public class MutationHolder extends RecyclerView.ViewHolder{
        private TextView mutation_date, mutation_type, mutation_nominal, mutation_info;

        public MutationHolder(View itemView) {
            super(itemView);

            mutation_date = itemView.findViewById(R.id.mutation_date);
            mutation_nominal = itemView.findViewById(R.id.mutation_nominal);
            mutation_type = itemView.findViewById(R.id.mutation_type);
            mutation_info = itemView.findViewById(R.id.mutation_info);
        }
    }
}
