package com.dam.chapas.bluetooth;

/**
 * @file BluetoothDevicesAdapter.java
 * @brief Adaptador para la lista de dispositivos Bluetooth
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dam.chapas.R;

import java.util.ArrayList;

/**
 * @class BluetoothDevicesAdapter
 */
public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothDeviceViewHolder> {

    private ArrayList<BluetoothDevice> mDevices;
    private final ListItemClickListener mOnClickListener;

    /**
     * @brief Constructor de la clase BluetoothDevicesAdapter
     * @param devices           Lista de dispositivos
     * @param mOnClickListener  Callback cuando haces click en uno de ellos
     */
    public BluetoothDevicesAdapter(ArrayList devices, ListItemClickListener mOnClickListener) {
        this.mDevices = devices;
        this.mOnClickListener = mOnClickListener;
    }

    /**
     * @brief Establece la lista de dispositivos
     * @param devices   La lista de dispositivos
     */
    public void setDevices(ArrayList devices) {
        mDevices = devices;
        notifyDataSetChanged();
    }

    /**
     * @brief Crea un view holder para la lista
     * @param parent    Contenedor de la lista
     * @param viewType  Tipo de vista
     * @return  El view holder para la lista
     */
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForNumberItem = R.layout.item_device;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForNumberItem, parent, shouldAttachToParentImmediately);
        BluetoothDeviceViewHolder viewHolder = new BluetoothDeviceViewHolder(view);

        return viewHolder;
    }

    /**
     * @brief Llamado cuando se activa el view holder de un elemento de la lista
     * @param holder    Holder de la lista
     * @param position  Elemento de la lista
     */
    @Override
    public void onBindViewHolder(BluetoothDeviceViewHolder holder, int position) {
        BluetoothDevice device = mDevices.get(position);
        String name = device.getName();

        holder.mTextViewDeviceName.setText(name);
    }

    /**
     * @brief Obtén el número de elementos de la lista
     * @return  El número de elementos de la lista
     */
    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTextViewDeviceName;

        public BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            mTextViewDeviceName = itemView.findViewById(R.id.textview_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
