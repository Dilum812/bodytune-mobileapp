package com.example.bodytunemobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(
    private val onNextClick: (Int) -> Unit,
    private val onSkipClick: () -> Unit
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val layouts = listOf(
        R.layout.onboarding_screen_1,
        R.layout.onboarding_screen_2,
        R.layout.onboarding_screen_3,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layouts[viewType], parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = position

    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val nextButton = itemView.findViewById<View>(R.id.next_button)
            val skipButton = itemView.findViewById<View>(R.id.skip_button)

            nextButton.setOnClickListener {
                onNextClick(position)
            }

            skipButton.setOnClickListener {
                onSkipClick()
            }
        }
    }
}
