package com.example.clientbluetooth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.clientbluetooth.databinding.FragmentPayBinding
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MODE = "param1"


class PayFragment : Fragment() {

    private var type: Int = -1

    val viewModel: BluetoothViewModel by lazy {
        ViewModelProvider(requireActivity())[BluetoothViewModel::class.java]
    }

    private lateinit var binding: FragmentPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(ARG_MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayBinding.inflate(inflater)

        workMode()

        binding.button.setOnClickListener {
            val btMessage = MessageBt(type, binding.etMoney.text.toString(), binding.etRrn.text.toString())
            viewModel.sendMessage(btMessage)
        }

        return binding.root
    }

    fun workMode() {
        when (type) {
            1 -> {
                binding.etRrn.visibility = View.GONE
                binding.tvRrn.visibility = View.GONE
            }
            3 -> {
                binding.etRrn.visibility = View.VISIBLE
                binding.tvRrn.visibility = View.VISIBLE
                binding.etRrn.setText("")
            }
            else -> {
                throw RuntimeException("not know type $type")
            }

        }
    }

    //1 - оплата, 3 - возврат
    companion object {
        @JvmStatic
        fun newBundle(workMode:Int)=
            Bundle().apply {
                putInt(ARG_MODE, workMode)
            }
    }
}