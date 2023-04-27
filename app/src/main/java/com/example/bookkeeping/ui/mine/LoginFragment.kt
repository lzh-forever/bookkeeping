package com.example.bookkeeping.ui.mine

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.databinding.FragmentLoginBinding
import com.example.bookkeeping.util.checkEmail


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.loginBtn.isEnabled = false
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val enabled =
                    !binding.emailText.text.isNullOrEmpty() && !binding.passwordText.text.isNullOrEmpty()
                binding.loginBtn.isEnabled = enabled
            }

            override fun afterTextChanged(s: Editable) {}
        }
        binding.emailText.addTextChangedListener(textWatcher)
        binding.passwordText.addTextChangedListener(textWatcher)
        binding.loginBtn.setOnClickListener {
            checkEmail(binding.emailText){
                lifecycleScope.launchWhenCreated {
                    if (Repository.login(binding.emailText.text.toString(), binding.passwordText.text.toString())){
                        findNavController().navigateUp()
                    }
                }
            }
        }
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}