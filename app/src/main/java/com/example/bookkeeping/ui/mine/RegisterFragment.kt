package com.example.bookkeeping.ui.mine

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.databinding.FragmentRegisterBinding
import com.example.bookkeeping.util.checkEmail


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val countDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = (millisUntilFinished / 1000).toString()
            binding.layoutCaptcha.sendTv.text = "${seconds}s"
        }

        override fun onFinish() {
            binding.layoutCaptcha.sendTv.text = "发送验证码"
            binding.layoutCaptcha.sendTv.isClickable = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.registerBtn.isEnabled = false
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val enabled = !binding.emailText.text.isNullOrEmpty()
                        && !binding.passwordText.text.isNullOrEmpty()
                        && !binding.usernameText.text.isNullOrEmpty()
                        && !binding.layoutCaptcha.captchaTv.text.isNullOrEmpty()
                binding.registerBtn.isEnabled = enabled
            }

            override fun afterTextChanged(s: Editable) {}
        }

        binding.emailText.addTextChangedListener(textWatcher)
        binding.usernameText.addTextChangedListener(textWatcher)
        binding.passwordText.addTextChangedListener(textWatcher)
        binding.layoutCaptcha.captchaTv.addTextChangedListener(textWatcher)
        binding.layoutCaptcha.sendTv.setOnClickListener {
            checkEmail(binding.emailText) {
                lifecycleScope.launchWhenCreated {
                    binding.layoutCaptcha.sendTv.isClickable = false
                    countDownTimer.start()
                    Repository.sendCaptcha(binding.emailText.text.toString())
                }
            }
        }
        binding.registerBtn.setOnClickListener {
            checkEmail(binding.emailText) {
                lifecycleScope.launchWhenCreated {
                    val result = Repository.register(
                        binding.emailText.text.toString(),
                        binding.usernameText.text.toString(),
                        binding.passwordText.text.toString(),
                        binding.layoutCaptcha.captchaTv.text.toString()
                    )
                    if (result) {
                        findNavController().navigateUp()
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _binding = null
    }
}