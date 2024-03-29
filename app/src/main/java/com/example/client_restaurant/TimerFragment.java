package com.example.client_restaurant;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    boolean started = false;
    Tick tick;
    boolean siguiente, wait, para;
    TextView tiempo;
    Lock lock;
    Condition condition;
    ImageView empieza, pausa, stop;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TimerFragment() {
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
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_timer, container, false);


        tiempo = v.findViewById(R.id.txtViewCont);
        lock = new ReentrantLock();
        condition = lock.newCondition();
        empieza = v.findViewById(R.id.btnIniciar);
        pausa = v.findViewById(R.id.btnPausar);
        stop = v.findViewById(R.id.btnReiniciar);


        empieza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                started = true;
                empieza.setEnabled(false);
                pausa.setEnabled(true);
                stop.setEnabled(true);
                if (!siguiente) {
                    siguiente = true;
                    wait = false;
                    para = false;
                    tick = new Tick();
                    tick.execute();
                } else if (wait) {
                    wait = false;
                    lock.lock();
                    condition.signalAll();
                    lock.unlock();
                }
            }
        });

        pausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait = true;
                started = false;
                empieza.setEnabled(true);
                pausa.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empieza.setEnabled(true);
                pausa.setEnabled(false);
                stop.setEnabled(false);
                siguiente = false;
                wait = false;
                para = true;
                lock.lock();
                condition.signalAll();
                lock.unlock();
                tiempo.setText("00:00");
                tiempo.setVisibility(View.VISIBLE);
                started = false;

            }
        });


        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public class Tick extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int cont = 0;
            lock.lock();
            while (!para) {
                if (wait) {
                    try {
                        condition.await();
                    } catch (InterruptedException |IllegalMonitorStateException e) {

                    }

                } else {
                    try {
                        Thread.sleep(1000);
                        cont++;
                        publishProgress(cont);
                    } catch (InterruptedException| IllegalMonitorStateException e) {

                    }
                }
            }
            try {
                lock.unlock();
            }catch (IllegalMonitorStateException e){

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (para && !wait && !siguiente) {
            } else {
                int minutes = (int) (values[0] / 60);
                int seconds = (int) (values[0]) % 60;
                tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                tiempo.setVisibility(View.VISIBLE);
                if (started){
                    empieza.setEnabled(false);
                    pausa.setEnabled(true);
                    stop.setEnabled(true);
                    wait =false;
                    para = false;
                }else if(!started){
                    empieza.setEnabled(true);
                    pausa.setEnabled(false);
                    stop.setEnabled(false);
                    wait = true;
                    para = false;
                }
            }
        }
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
}
