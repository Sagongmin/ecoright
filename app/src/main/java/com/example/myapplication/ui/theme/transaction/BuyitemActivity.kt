package com.example.myapplication.ui.theme.transaction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.example.myapplication.databinding.ActivityBuyBinding
import com.example.myapplication.ui.theme.transaction.CarbonCreditItem

class BuyitemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("탄소배출권").child("users")
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val query = databaseReference.orderByChild("판매물품가격")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val carbonCreditList = mutableListOf<CarbonCreditItem>()

                    for (userSnapshot in dataSnapshot.children) {
                        for (itemSnapshot in userSnapshot.children) {
                            val item = itemSnapshot.getValue(CarbonCreditItem::class.java)
                            if (item != null) {
                                carbonCreditList.add(item)
                            }
                        }
                    }

                    // carbonCreditList를 가격이 높은 순으로 정렬
                    carbonCreditList.sortByDescending { it.판매물품가격 }

                    val adapter = CarbonCreditAdapter(carbonCreditList)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object : CarbonCreditAdapter.OnItemClickListener {
                        override fun onBuyClick(position: Int) {
                            // 구매 작업 수행
                            val itemToDelete = carbonCreditList[position]
                            val userReference = databaseReference.child(itemToDelete.판매자ID).child(itemToDelete.key.orEmpty()) // 해당 아이템이 속한 사용자 참조
                            //val itemReference = userReference.child(itemToDelete.key.orEmpty()) // 아이템 참조
                            userReference.removeValue()

                            // 아래 코드를 추가하여 리스트에서 아이템 제거
                            carbonCreditList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터베이스에서 데이터를 불러오지 못한 경우 처리
            }
        })

        binding.goToSellButton.setOnClickListener {
            val intent = Intent(this, SellitemActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}