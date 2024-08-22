package com.github.opensmsforwarder.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.opensmsforwarder.databinding.ItemOnboardingSlideBinding
import com.github.opensmsforwarder.model.OnboardingPagerSlide

class OnboardingSliderAdapter : RecyclerView.Adapter<SliderViewHolder>() {

    private val slides = mutableListOf<OnboardingPagerSlide>()
    private var isCheckboxClickable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding =
            ItemOnboardingSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(slides[position], isCheckboxClickable)
    }

    override fun getItemCount(): Int = slides.size

    fun setData(slides: List<OnboardingPagerSlide>) {
        this.slides.clear()
        this.slides.addAll(slides)
    }

    fun getItem(position: Int): OnboardingPagerSlide {
        return slides[position]
    }

    fun setCheckboxClickable(isClickable: Boolean) {
        if (isCheckboxClickable != isClickable) {
            isCheckboxClickable = isClickable
            notifyItemChanged(slides.size - 1)
        }
    }
}

class SliderViewHolder(private val binding: ItemOnboardingSlideBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(slide: OnboardingPagerSlide, isCheckboxClickable: Boolean) {
        with(binding) {
            textTitle.text = root.context.getString(slide.titleId)
            textSubtitle.text = root.context.getString(slide.subtitleId)
            image.setImageResource(slide.imageId)
            checkboxAgree.isChecked = slide.isChecked
            checkboxAgree.isVisible = slide.isLastSlide
            if (slide.isLastSlide) {
                checkboxAgree.isClickable = isCheckboxClickable
            } else {
                checkboxAgree.isClickable = false
            }
            checkboxAgree.setOnCheckedChangeListener { _, isChecked ->
                slide.isChecked = isChecked
            }
        }
    }
}
