package com.example.googleloginfirebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.googleloginfirebaseapp.databinding.ActivityMainBinding
import com.example.googleloginfirebaseapp.databinding.ListItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ItemDataModelViewHolder(val binding1: ListItemBinding):RecyclerView.ViewHolder(binding1.root)

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var storageRef = Firebase.storage.reference
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val query = db.collection("shops")
        val options = FirestoreRecyclerOptions.Builder<ItemDataModel>().setQuery(query,ItemDataModel::class.java)
            .setLifecycleOwner(this).build()

        val adapter = object : FirestoreRecyclerAdapter<ItemDataModel,ItemDataModelViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ItemDataModelViewHolder {
                return ItemDataModelViewHolder(
                    ListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(
                holder: ItemDataModelViewHolder,
                position: Int,
                model: ItemDataModel
            ) {
                Glide.with(holder.itemView).load(model.shopImageUrl).into(holder.binding1.image)
                holder.binding1.discountRangeText.text = model.discountRange
                holder.binding1.offersCountText.text = model.totalOfferedItem
                holder.binding1.shopName.text = model.shopName
            }

        }

        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)



       // listFiles()
    }

   /* private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val images = storageRef.child("Files/").listAll().await()
            val imageUrls = mutableListOf<String>()
            for(image in images.items){
                val url = image.downloadUrl.await()
                imageUrls.add(url.toString())
            }
            withContext(Dispatchers.Main){
                val itemAdapter = ItemAdapter(imageUrls)
                binding.rvItems.apply {
                    adapter = itemAdapter
                    layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }


        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    } */
}