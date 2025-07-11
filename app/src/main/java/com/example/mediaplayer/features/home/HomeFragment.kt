package com.example.mediaplayer.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentHomeBinding
import com.example.mediaplayer.features.albums.view.AlbumsFragment
import com.example.mediaplayer.features.folders.view.FoldersFragment
import com.example.mediaplayer.features.tracks.view.TracksFragment
import com.google.android.material.tabs.TabLayoutMediator

class SplashFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var tabTitles = listOf("Tracks", "Albums", "Folders","PlayList")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val enterAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(enterAnim)
        binding.viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.apply {
            historyCard.setOnClickListener {
                navigateToHistory()
            }
            searchCard.setOnClickListener {
                navigateToSearch()
            }
            searchBtn.setOnClickListener {
                navigateToSearch()
            }
            favCard.setOnClickListener {
                navigateToFav()
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return TracksFragment()
                1 -> return AlbumsFragment()
                2 -> return FoldersFragment()
                3 -> return FoldersFragment()
                else -> return TracksFragment()
            }
        }

        override fun getItemCount(): Int {
            return 4
        }
    }

    fun navigateToSearch() {
        findNavController().navigate(R.id.action_splashFragment_to_searchFragment)
    }

    fun navigateToFav() {
        findNavController().navigate(R.id.action_splashFragment_to_favoriteFragment)
    }

    fun navigateToHistory() {
        findNavController().navigate(R.id.action_splashFragment_to_historyFragment)
    }

}