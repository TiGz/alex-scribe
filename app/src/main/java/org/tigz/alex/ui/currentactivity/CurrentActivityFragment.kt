package org.tigz.alex.ui.currentactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.tigz.alex.databinding.FragmentCurrentactivityBinding

class CurrentActivityFragment : Fragment() {

    private var _binding: FragmentCurrentactivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentActivityViewModel =
            ViewModelProvider(this).get(CurrentActivityViewModel::class.java)

        _binding = FragmentCurrentactivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCurrentActivity
        currentActivityViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}