package com.example.bodytunemobileapp.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bodytunemobileapp.R
import com.example.bodytunemobileapp.databinding.ItemOnboardingBinding
import com.example.bodytunemobileapp.databinding.ItemSignInBinding
import com.example.bodytunemobileapp.databinding.ItemSignUpBinding
import com.example.bodytunemobileapp.databinding.ItemProfileSetupBinding
import com.example.bodytunemobileapp.model.OnboardingItem
import com.example.bodytunemobileapp.model.ScreenType

class OnboardingAdapter(
    private val onboardingItems: List<OnboardingItem>,
    private val onSignUpClick: () -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ONBOARDING = 0
        private const val VIEW_TYPE_SIGN_IN = 1
        private const val VIEW_TYPE_SIGN_UP = 2
        private const val VIEW_TYPE_PROFILE_SETUP = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (onboardingItems[position].screenType) {
            ScreenType.ONBOARDING -> VIEW_TYPE_ONBOARDING
            ScreenType.SIGN_IN -> VIEW_TYPE_SIGN_IN
            ScreenType.SIGN_UP -> VIEW_TYPE_SIGN_UP
            ScreenType.PROFILE_SETUP -> VIEW_TYPE_PROFILE_SETUP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SIGN_IN -> {
                val binding = ItemSignInBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SignInViewHolder(binding, onSignUpClick)
            }
            VIEW_TYPE_SIGN_UP -> {
                val binding = ItemSignUpBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SignUpViewHolder(binding)
            }
            VIEW_TYPE_PROFILE_SETUP -> {
                val binding = ItemProfileSetupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProfileSetupViewHolder(binding)
            }
            else -> {
                val binding = ItemOnboardingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OnboardingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = onboardingItems[position]
        when (holder) {
            is SignInViewHolder -> holder.bind(item)
            is SignUpViewHolder -> holder.bind(item)
            is ProfileSetupViewHolder -> holder.bind(item)
            is OnboardingViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = onboardingItems.size

    class SignInViewHolder(
        private val binding: ItemSignInBinding,
        private val onSignUpClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingItem) {
            // Set up click listener for Sign Up link
            binding.tvSignUpLink.setOnClickListener {
                onSignUpClick()
            }
        }
    }

    class SignUpViewHolder(
        private val binding: ItemSignUpBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingItem) {
            // Sign up screen uses static content from layout
        }
    }

    class ProfileSetupViewHolder(
        private val binding: ItemProfileSetupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingItem) {
            // Profile setup screen uses static content from layout
        }
    }

    class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingItem) {
            binding.ivOnboarding.setImageResource(onboardingItem.image)
            
            // Handle HTML formatting in title
            if (onboardingItem.title.contains("<br/>")) {
                binding.tvTitle.text = Html.fromHtml(onboardingItem.title, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.tvTitle.text = onboardingItem.title
            }
            
            binding.tvDescription.text = onboardingItem.description
            
            // Show logo if provided
            if (onboardingItem.logo != null) {
                binding.ivLogo.setImageResource(onboardingItem.logo)
                binding.ivLogo.visibility = View.VISIBLE
            } else {
                binding.ivLogo.visibility = View.GONE
            }
        }
    }
}
