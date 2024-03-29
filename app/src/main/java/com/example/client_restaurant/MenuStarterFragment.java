package com.example.client_restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuStarterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuStarterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuStarterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<Dish> dishList = new ArrayList<>();
    TextView name, price, stock, dniKitchen, description;
    EditText editTextName, editTextPrice, editTextStock, editTextDniKitchen, editTextDescription;
    SwipeRefreshLayout swipeRefreshLayout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MenuStarterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuStarterFragment newInstance(String param1, String param2) {
        MenuStarterFragment fragment = new MenuStarterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    MenuStarterAdapter recyclerViewAdapterDish;
    RecyclerView recyclerViewDish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu_starter, container, false);
        recyclerViewDish = v.findViewById(R.id.recyclerview_menu_starter_id);

        swipeRefreshLayout = v.findViewById(R.id.refreshDish);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                LayoutInflater mInflater2 = LayoutInflater.from(getContext());
                View customLayout = mInflater2.inflate(R.layout.dish_add_layout, null);

                alertDialog.setView(customLayout);
                final AlertDialog alert = alertDialog.create();

                name = customLayout.findViewById(R.id.textViewLabelNameDish);
                editTextName = customLayout.findViewById(R.id.editTextDishName);

                price = customLayout.findViewById(R.id.textViewLabelDishPrice);
                editTextPrice = customLayout.findViewById(R.id.editTextDishPrice);

                stock = customLayout.findViewById(R.id.textViewLabelDishQuantityStock);
                editTextStock = customLayout.findViewById(R.id.editTextDishStock);

                dniKitchen = customLayout.findViewById(R.id.textViewLabelDishDniKitchen);
                editTextDniKitchen = customLayout.findViewById(R.id.editTextDishDniKitchen);

                description = customLayout.findViewById(R.id.textViewLabelDishDescription);
                editTextDescription = customLayout.findViewById(R.id.editTextDishDescription);

                Button btnInsert = customLayout.findViewById(R.id.btnInsert);
                btnInsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GetDishAsyncTask getDishAsyncTask = new GetDishAsyncTask(2);
                        getDishAsyncTask.execute();

                        alert.cancel();

                        GetDishAsyncTask getDishAsyncTask2 = new GetDishAsyncTask(1);
                        getDishAsyncTask2.execute();
                    }
                });

                alert.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetDishAsyncTask getDishAsyncTask = new GetDishAsyncTask(1);
                getDishAsyncTask.execute();
            }
        });

        GetDishAsyncTask getDishAsyncTask = new GetDishAsyncTask(1);
        getDishAsyncTask.execute();

        return v;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetDishAsyncTask extends AsyncTask<String, Void, String> {

        Socket sk;
        DataInputStream dis;
        DataOutputStream dos;
        ObjectInputStream ois;
        PublicKey publicKey;
        List<Dish> dishList2 = new ArrayList<Dish>();
        int option;

        private GetDishAsyncTask(int option) {
            this.option = option;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dishList = dishList2;
            recyclerViewAdapterDish = new MenuStarterAdapter(getContext(), dishList);
            recyclerViewDish.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerViewDish.setAdapter(recyclerViewAdapterDish);
            swipeRefreshLayout.setRefreshing(false);

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

                if (option == 1) {
                    dos.writeInt(2);

                    int size = dis.readInt();
                    System.out.println("TAMAÑO LISTA : " + size);

                    for (int i = 0; i < size; i++) {

                        String dishName = dis.readUTF();
                        int idItemDish = dis.readInt();
                        float price = dis.readFloat();
                        int quantityStock = dis.readInt();
                        int statusDish = dis.readInt();
                        String descriptionDish = dis.readUTF();
                        String dniKitchen = dis.readUTF();

                        System.out.println("NOMBRE DEL PLATO: " + dishName);

                        dishList2.add(new Dish(dishName, idItemDish, price, quantityStock, statusDish, descriptionDish, dniKitchen));
                        System.out.println("TAMAÑO LISTA BUCLE: " + dishList2.size());
                    }
                }
                if (option == 2) {

                    try {

                        dos.writeInt(6);
                        dos.writeUTF(editTextName.getText().toString());
                        dos.writeFloat(Float.parseFloat(editTextPrice.getText().toString()));
                        dos.writeInt(Integer.parseInt(editTextStock.getText().toString()));
                        dos.writeUTF(editTextDescription.getText().toString());
                        dos.writeUTF(editTextDniKitchen.getText().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;

        }

    }
}
