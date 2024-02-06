package com.example.clientbluetooth

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clientbluetooth.databinding.FragmentSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private val TAG: String = "SecondFragment"


    // This property is only valid between onCreateView and
    // onDestroyView.

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val viewModel: BluetoothViewModel by lazy {
        ViewModelProvider(requireActivity())[BluetoothViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       var _binding= FragmentSecondBinding.inflate(inflater, container, false)

        viewModel.startScan()
        coroutineScope.launch(Dispatchers.Main) {

            viewModel.state.collect() {
                patternDevices(it.pairedDevices, _binding.pairedDevices, true)
                patternDevices(it.scannedDevices, _binding.newDevices, false)
            }
        }

        return _binding.root
    }

    fun patternDevices(listDivice: List<BluetoothDevice>, listView: ListView, isSave: Boolean) {
        if(context == null)
            return

        val pairedDevicesArrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.device_name)
        pairedDevicesArrayAdapter.addAll(listDivice.map { "${it.name} ${it.address}"})
        listView.adapter = pairedDevicesArrayAdapter
        listView.onItemClickListener = mDeviceClickListener
    }

    private val mDeviceClickListener =
        OnItemClickListener { av, v, arg2, arg3 -> // Cancel discovery because it's costly and we're about to connect

            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)

            viewModel.connect(address)
        }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}