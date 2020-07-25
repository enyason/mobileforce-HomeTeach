package com.mobileforce.hometeach.ui.withdrawalscreens.mybank
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mobileforce.hometeach.R
import com.mobileforce.hometeach.databinding.FragmentMyBanksBinding


class MyBanks : Fragment() {
    lateinit var navController: NavController
    lateinit var binding:FragmentMyBanksBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentMyBanksBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val toolbar = binding.toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(R.drawable.back_arrow)
        }



        toolbar.setNavigationOnClickListener {

            navController.navigate(R.id.tutorHomePageFragment)
        }

        binding.addBank.setOnClickListener {

            navController.navigate(R.id.addBankFragment)
        }


    }
}