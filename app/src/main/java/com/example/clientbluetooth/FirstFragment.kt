package com.example.clientbluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clientbluetooth.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {


    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val viewModel = ViewModelProvider(requireActivity())[BluetoothViewModel::class.java]

        if(hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            viewModel.startScan()
        }

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        _binding?.btnPay?.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_payFragment, PayFragment.newBundle(1))
        }
        _binding?.btnCanselPay?.setOnClickListener {
            val messageBt = MessageBt(type = 2, rrn = "")
            viewModel.sendMessage(messageBt)
        }
        _binding?.btnReturn?.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_payFragment, PayFragment.newBundle(3))
        }
        _binding?.btnCanselReturn?.setOnClickListener {
            val messageBt = MessageBt(type = 4, rrn = "")
            viewModel.sendMessage(messageBt)
        }

        return binding.root
    }

    private fun hasPermission(permission: String): Boolean {
        return requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}