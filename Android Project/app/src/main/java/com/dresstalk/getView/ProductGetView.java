package com.dresstalk.getView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dresstalk.Interface.ItemClickListner;
import com.dresstalk.R;

public class ProductGetView extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProducPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductGetView(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription= (TextView) itemView.findViewById(R.id.product_description);
        txtProducPrice= (TextView) itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view ,getAdapterPosition(),false);
    }
}
