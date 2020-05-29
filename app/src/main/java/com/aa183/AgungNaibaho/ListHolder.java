package com.aa183.AgungNaibaho;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ListHolder extends RecyclerView.ViewHolder{

    public TextView tvJudul, tvPenyayi, tvTahun;
    public CardView cvList;
    public ImageView mListUpload;

    public ListHolder(View itemView){
        super(itemView);
        cvList = itemView.findViewById(R.id.cvList);
        tvJudul = itemView.findViewById(R.id.tvJudul);
        tvPenyayi = itemView.findViewById(R.id.tvPenyayi);
        tvTahun = itemView.findViewById(R.id.tvTahun);
        tvPenyayi = itemView.findViewById(R.id.tvPenyayi);
        mListUpload = itemView.findViewById(R.id.mListUpload);
    }

}
