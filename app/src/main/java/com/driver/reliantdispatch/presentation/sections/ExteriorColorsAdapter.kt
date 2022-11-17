package com.driver.reliantdispatch.presentation.sections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewExteriorColorBinding
import com.driver.reliantdispatch.presentation.secondary.SelectedColorStorage
import kotlin.math.max

class ExteriorColorsAdapter(
    val viewModel: SelectedColorStorage,
    val readOnly: Boolean
): RecyclerView.Adapter<ExteriorColorsAdapter.ViewHolder>(){
    var mColors = IntArray(0)
    var mNames = arrayOf<String>()

    var mSelectedPos = viewModel.getSelectedColorIndex()

    val handler = ClickHandler()

    class ViewHolder(val binding: ViewExteriorColorBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewExteriorColorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = max(mColors.size, mNames.size)

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val wrapDrawable = holder.binding.root.resources.getDrawable(R.drawable.circle_red).let{
            DrawableCompat.wrap(it)
        }
        if (pos < mColors.size) DrawableCompat.setTint(wrapDrawable, mColors[pos])
        holder.binding.drawable = wrapDrawable

        if (pos < mNames.size) holder.binding.name = mNames[pos]
        holder.binding.pos = pos
        holder.binding.selected = mSelectedPos == pos
        holder.binding.handler = handler
    }

    inner class ClickHandler{//: View.OnClickListener{

        fun onClick(pos: Int){
            if (pos in 0..itemCount && pos != mSelectedPos && !readOnly){
                val prevPos = mSelectedPos
                mSelectedPos = pos
                notifyItemChanged(prevPos)
                notifyItemChanged(mSelectedPos)
                viewModel.setSelectedColorIndex(pos)
            }
        }
    }
}