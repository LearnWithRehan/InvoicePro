package com.example.kuhuinvoicepro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private List<InvoiceItem> itemList;

    public InvoiceAdapter(List<InvoiceItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceItem item = itemList.get(position);

        holder.tvSr.setText(String.valueOf(item.getSrNo()));
        holder.tvProduct.setText(item.getProductName());
        holder.tvQty.setText(item.getQty() +"kg");
        holder.tvRate.setText(String.valueOf(item.getRate()));
        holder.tvAmount.setText(String.format("%.2f", item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSr, tvProduct, tvQty, tvRate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSr = itemView.findViewById(R.id.tvSr);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}