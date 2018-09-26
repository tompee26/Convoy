package com.tompee.convoy.feature.friend.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tompee.convoy.R
import java.util.*

class SectionAdapter(private val baseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val SECTION_TYPE = 0
    }

    private val sections = SparseArray<Section>()
    private var isValid = true

    init {
        baseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                isValid = baseAdapter.itemCount > 0
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                isValid = baseAdapter.itemCount > 0
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                isValid = baseAdapter.itemCount > 0
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                isValid = baseAdapter.itemCount > 0
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SECTION_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_friend_section, parent, false)
            return SectionViewHolder(view)
        }
        return baseAdapter.onCreateViewHolder(parent, viewType - 1)
    }

    override fun getItemCount(): Int {
        return if (isValid) baseAdapter.itemCount + sections.size() else 0
    }

    override fun getItemId(position: Int): Long {
        return if (isSectionHeaderPosition(position))
            (Integer.MAX_VALUE - sections.indexOfKey(position)).toLong()
        else
            baseAdapter.getItemId(sectionedPositionToPosition(position))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isSectionHeaderPosition(position)) {
            (holder as SectionViewHolder).bind(sections[position])
        } else {
            baseAdapter.onBindViewHolder(holder, sectionedPositionToPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeaderPosition(position)) SECTION_TYPE else baseAdapter.getItemViewType(position) + 1
    }

    fun setSections(sectionsInt: Array<Section>) {
        sections.clear()

        Arrays.sort<Section>(sectionsInt, { o, o1 ->
            when {
                o.firstPosition == o1.firstPosition -> 0
                o.firstPosition < o1.firstPosition -> -1
                else -> 1
            }
        })

        for ((offset, section) in sectionsInt.withIndex()) {
            section.sectionedPosition = section.firstPosition + offset
            sections.append(section.sectionedPosition, section)
        }
        notifyDataSetChanged()
    }

    fun positionToSectionedPosition(position: Int): Int {
        var offset = 0
        for (i in 0 until sections.size()) {
            if (sections.valueAt(i).firstPosition > position) {
                break
            }
            ++offset
        }
        return position + offset
    }

    private fun isSectionHeaderPosition(position: Int): Boolean {
        return sections.get(position) != null
    }

    private fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION
        }

        var offset = 0
        for (i in 0 until sections.size()) {
            if (sections.valueAt(i).sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }

        return sectionedPosition + offset
    }

    inner class SectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val sectionTextView = view.findViewById<TextView>(R.id.section)
        private val count = view.findViewById<TextView>(R.id.count)
        fun bind(section: Section) {
            sectionTextView.text = section.title
            count.text = section.count.toString()
        }
    }

    class Section(val firstPosition: Int,
                  val title: String,
                  val count: Int) {
        var sectionedPosition: Int = 0
    }
}