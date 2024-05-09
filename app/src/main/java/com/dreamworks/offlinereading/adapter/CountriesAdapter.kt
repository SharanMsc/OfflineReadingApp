package com.dreamworks.offlinereading.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamworks.offlinereading.databinding.ItemHeaderTitleBinding
import com.dreamworks.offlinereading.databinding.ItemNameBinding
import com.dreamworks.offlinereading.model.CountriesModel

class CountriesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sectionView = 0
    private val contentView = 1
    private var dataList:List<CountriesModel> ?= null
    fun setData(dataList:List<CountriesModel>){
        this.dataList=dataList
    }

    class SectionViewHolder(val binding: ItemHeaderTitleBinding): RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(val binding: ItemNameBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==sectionView){
            val view =ItemHeaderTitleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            SectionViewHolder(view)
        } else{
            val view = ItemNameBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (sectionView==getItemViewType(position)){
            val sectionViewHolder = holder as SectionViewHolder
            val sectionItem = dataList!![position]
            sectionViewHolder.binding.headerTitleTextview.text = sectionItem.name
        }else{
            val itemViewHolder = holder as ItemViewHolder
            val item = dataList!![position]
            itemViewHolder.binding.nameTextview.text = item.name
        }
    }


    override fun getItemCount(): Int =dataList!!.size

    override fun getItemViewType(position: Int): Int {
        return if (dataList!![position].isSection){
            sectionView
        }else{
            contentView
        }

    }
}