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

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceHolder> {
    private List<Rekening> listRekening;
    private Context mContext;

    public BalanceAdapter(List<Rekening> listRekening, Context mContext){
        this.listRekening = listRekening;
        this.mContext = mContext;
    }

    @Override
    public BalanceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rekening, parent, false);
        return new BalanceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BalanceHolder holder, int position) {
        NumberFormat formatter = new DecimalFormat("#,###");

        Rekening rek = listRekening.get(position);
        holder.info_rekNum.setText(rek.getRekeningNum());
        holder.info_saldo.setText("Rp " + String.valueOf(formatter.format(rek.getSaldo())) + ",-");
        holder.info_cabang.setText(rek.getKode_cabang());
    }

    @Override
    public int getItemCount() {
        return listRekening.size();
    }

    public class BalanceHolder extends RecyclerView.ViewHolder{
        private TextView info_rekNum, info_saldo, info_cabang;

        public BalanceHolder(View itemView) {
            super(itemView);

            info_rekNum = itemView.findViewById(R.id.info_rekNum);
            info_saldo = itemView.findViewById(R.id.info_saldo);
            info_cabang = itemView.findViewById(R.id.info_cabang);
        }
    }
}
