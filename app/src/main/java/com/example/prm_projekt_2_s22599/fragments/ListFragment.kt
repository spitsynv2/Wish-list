package com.example.prm_projekt_2_s22599.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm_projekt_2_s22599.Navigable
import com.example.prm_projekt_2_s22599.adapters.ProductAdapter
import com.example.prm_projekt_2_s22599.adapters.SwipeToRemove
import com.example.prm_projekt_2_s22599.data.ProductDatabase
import com.example.prm_projekt_2_s22599.databinding.FragmentListBinding
import com.example.prm_projekt_2_s22599.model.Product
import kotlin.concurrent.thread

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private var adapter: ProductAdapter? = null
    private lateinit var db:ProductDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ProductDatabase.open(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentListBinding.inflate(inflater,container,false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductAdapter().apply {
            onItemClick = {
                (activity as? Navigable)?.navigate(Navigable.Destination.Edit, it)
            }
        }

        loadData()

        binding.list.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(
                SwipeToRemove{
                    adapter?.removeItem(it)?.let {
                        thread {
                            db.products.remove(it.id)
                        }
                    }
                }
            ).attachToRecyclerView(it)
        }

        binding.BtAdd.setOnClickListener {
            (activity as? Navigable)?.navigate(Navigable.Destination.Add)
        }

        binding.BtSort.setOnClickListener {
            loadDataSortedByName()
        }

    }

    fun  loadData() = thread {
        val products = db.products.getAllSortedByTime().map { entity ->
            Product(
            entity.id,
            entity.name,
            entity.address,
            entity.path,
            entity.longitude,
            entity.latitude
            )
        }
            adapter?.replace(products)
    }

    fun  loadDataSortedByName() = thread {
        val products = db.products.getAllSortedByName().map { entity ->
            Product(
                entity.id,
                entity.name,
                entity.address,
                entity.path,
                entity.longitude,
                entity.latitude
            )
        }
        adapter?.replace(products)
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }


}