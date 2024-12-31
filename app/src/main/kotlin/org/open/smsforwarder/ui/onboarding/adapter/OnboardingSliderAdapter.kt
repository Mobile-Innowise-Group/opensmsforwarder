package org.open.smsforwarder.ui.onboarding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.open.smsforwarder.databinding.ItemOnboardingSlideBinding

class OnboardingSliderAdapter : RecyclerView.Adapter<SliderViewHolder>() {

    private val slides = mutableListOf<OnboardingPagerSlide>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding =
            ItemOnboardingSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount(): Int = slides.size

    fun setData(slides: List<OnboardingPagerSlide>) {
        this.slides.clear()
        this.slides.addAll(slides)
    }
}

class SliderViewHolder(private val binding: ItemOnboardingSlideBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(slide: OnboardingPagerSlide) {
        with(binding) {
            textTitle.text = root.context.getString(slide.titleId)
            textSubtitle.text = root.context.getString(slide.subtitleId)
            image.setImageResource(slide.imageId)
        }
    }
}
