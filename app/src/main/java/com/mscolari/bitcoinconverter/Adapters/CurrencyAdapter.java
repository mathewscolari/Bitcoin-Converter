package com.mscolari.bitcoinconverter.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mscolari.bitcoinconverter.Models.Currency;
import com.mscolari.bitcoinconverter.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CurrencyAdapter extends RecyclerView.Adapter{
    private ArrayList<Currency> currencyList;
    private OnItemClickListener clickListener;

    public CurrencyAdapter(ArrayList<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.currency_item, viewGroup, false);
        return new CurrencyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Currency currency = currencyList.get(i);

        // Obtain reference to current list item and set views accordingly
        CurrencyViewHolder classViewHolder = (CurrencyViewHolder) viewHolder;
        classViewHolder.tvCurrencyName.setText(currency.getName());
        classViewHolder.tvCode.setText(currency.getCode());
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Currency currency);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { clickListener = listener; }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCode;
        public TextView tvCurrencyName;

        public CurrencyViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.currency_item_tv_code);
            tvCurrencyName = itemView.findViewById(R.id.currency_item_tv_name);

            // pass selected class information to click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int currPosition = getAdapterPosition();
                        if (currPosition != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(currencyList.get(currPosition));
                        }
                    }
                }
            });
        }
    }

    // filters recyclerview by search term
    public void filterList(ArrayList<Currency> updatedList) {
        currencyList = new ArrayList<Currency>();
        currencyList.addAll(updatedList);
        notifyDataSetChanged();
    }
}
