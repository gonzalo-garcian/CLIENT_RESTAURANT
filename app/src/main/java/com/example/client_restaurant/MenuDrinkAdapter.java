package com.example.client_restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

public class MenuDrinkAdapter extends RecyclerView.Adapter<MenuDrinkAdapter.DrinkViewHolder> {


    private Context mContext;
    private List<Drink> mData;

     MenuDrinkAdapter(Context mContext, List<Drink> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_dish, viewGroup ,false);

        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder starterViewHolder, int i) {

        starterViewHolder.textViewDishName.setText(mData.get(i).getName());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

     class DrinkViewHolder extends RecyclerView.ViewHolder {

         TextView textViewDishName;
         ImageView imageViewDishImage;

         DrinkViewHolder(@NonNull final View itemView) {
             super(itemView);

             textViewDishName = itemView.findViewById(R.id.dish_name_id);
             imageViewDishImage = itemView.findViewById(R.id.dish_img_id);

             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                     LayoutInflater mInflater2 = LayoutInflater.from(mContext);
                     @SuppressLint("InflateParams") final View customLayout = mInflater2.inflate(R.layout.dish_info_layout, null);

                     //MOVER SI NO FUNCIONA
                     alertDialog.setView(customLayout);
                     final AlertDialog alert= alertDialog.create();

                     TextView dishName = customLayout.findViewById(R.id.textViewAlertDialogDishName);
                     dishName.setText(mData.get(getAdapterPosition()).getName());

                     ImageView dishImage = customLayout.findViewById(R.id.imageViewAlertDialogDishImage);

                     TextView dishDescription = customLayout.findViewById(R.id.textViewAlertDialogDishDescription);
                     dishDescription.setText(mData.get(getAdapterPosition()).getDescriptionDish());

                     final TextView dishStock = customLayout.findViewById(R.id.textViewAlertDialogDishStock);
                     dishStock.setText(Integer.toString(mData.get(getAdapterPosition()).getQuantityStock()));

                     Button btnMinus = customLayout.findViewById(R.id.btnMinusStockDish);
                     btnMinus.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             mData.get(getAdapterPosition()).minusDishStock();

                             dishStock.setText(Integer.toString(mData.get(getAdapterPosition()).getQuantityStock()));
                             UpdateDrinkAsyncTask updateDishAsyncTask = new UpdateDrinkAsyncTask(mData.get(getAdapterPosition()),1);
                             updateDishAsyncTask.execute();

                         }
                     });

                     Button btnAdd = customLayout.findViewById(R.id.btnAddStockDish);
                     btnAdd.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             mData.get(getAdapterPosition()).addDishStock();

                             dishStock.setText(Integer.toString(mData.get(getAdapterPosition()).getQuantityStock()));
                             UpdateDrinkAsyncTask updateDishAsyncTask = new UpdateDrinkAsyncTask(mData.get(getAdapterPosition()),1);
                             updateDishAsyncTask.execute();

                         }
                     });

                     Button btnDelete = customLayout.findViewById(R.id.btnDeleteDish);
                     btnDelete.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             UpdateDrinkAsyncTask updateDrinkAsyncTask = new UpdateDrinkAsyncTask(mData.get(getAdapterPosition()),2);
                             updateDrinkAsyncTask.execute();
                             alert.cancel();


                         }
                     });

                     alert.show();
                 }
             });
         }

         @SuppressLint("StaticFieldLeak")
         class UpdateDrinkAsyncTask extends AsyncTask<String, Void, String> {

             Socket sk;
             PublicKey publicKey;
             DataInputStream dis;
             DataOutputStream dos;
             ObjectInputStream ois;

             Drink drink;
             int option;

             public UpdateDrinkAsyncTask(Drink drink, int option) {

                 this.drink = drink;
                 this.option = option;
             }

             @Override
             protected void onPreExecute() {
                 super.onPreExecute();
             }

             @Override
             protected void onPostExecute(String s) {
                 super.onPostExecute(s);
             }

             @Override
             protected String doInBackground(String... strings) {

                 try {
                     Connection connection = new Connection();
                     String ip = connection.getIp();
                     sk = new Socket(ip, 20002);
                     System.out.println("Establecida la conexión con " + ip);
                     dis = new DataInputStream(sk.getInputStream());
                     dos = new DataOutputStream(sk.getOutputStream());
                     ois = new ObjectInputStream(sk.getInputStream());

                     publicKey = (PublicKey) ois.readObject();

                     if(option == 1) {
                         dos.writeInt(10);
                         dos.writeInt(mData.get(getAdapterPosition()).getIdItemDish());
                         dos.writeInt(mData.get(getAdapterPosition()).getQuantityStock());
                     }
                     if(option == 2){
                         dos.writeInt(11);
                         dos.writeInt(mData.get(getAdapterPosition()).getIdItemDish());
                         System.out.println("Elimino cosas");
                     }

                     sk.close();
                     dis.close();
                     dos.close();
                     ois.close();

                 } catch (IOException | ClassNotFoundException ex) {
                     ex.printStackTrace();
                 }

                 return null;


             }
         }
     }
}
