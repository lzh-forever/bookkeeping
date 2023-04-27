package com.example.bookkeeping.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookkeeping.R
import com.example.bookkeeping.data.Repository
import com.example.bookkeeping.databinding.FragmentMineBinding

class MineFragment : Fragment() {

    private var _binding: FragmentMineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView()

        return root
    }

    private fun initView() {
        if (Repository.username.isNotEmpty() && Repository.email.isNotEmpty()) {
            binding.layoutBackup.root.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
            binding.userImage.setImageResource(R.drawable.profile_image)
            binding.userTv.text = Repository.username
            binding.emailTv.text = Repository.email
            if (Repository.restoreDate.isNotEmpty()) {
                setUpdateDate()
            }
            if (Repository.backupDate.isNotEmpty()) {
                setBackupDate()
            }

            binding.layoutBackup.backupBtn.setOnClickListener{
                lifecycleScope.launchWhenCreated {
                    if(Repository.backupDatabase()) {
                        setBackupDate()
                    }
                }
            }
            binding.layoutBackup.restoreBtn.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    if (Repository.restoreDatabase()) {
                        setUpdateDate()
                    }
                }
            }
            return
        }
        binding.userImage.setOnClickListener {
            findNavController().navigate(R.id.action_mine_to_login)
        }
        binding.userTv.setOnClickListener {
            findNavController().navigate(R.id.action_mine_to_login)
        }
        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mine_to_login)
        }
    }

    private fun setBackupDate(){
        binding.layoutBackup.backupDateTv.text =
            getString(R.string.local_db_date, Repository.backupDate)
    }

    private fun setUpdateDate(){
        binding.layoutBackup.restoreDateTv.text =
            getString(R.string.backup_db_date, Repository.restoreDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}