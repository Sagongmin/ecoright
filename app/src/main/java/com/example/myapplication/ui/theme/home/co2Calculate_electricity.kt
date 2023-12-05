package com.example.myapplication.ui.theme.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.theme.main.ocrCamera

class co2Calculate_electricity : AppCompatActivity(){

    //전기 사용량 -> 탄소배출량,필요소나무는 1kWh당 0.4781kg , 0.1157그루

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carboncalculate_electricity)
        supportActionBar?.hide()

        val editText = findViewById<EditText>(R.id.electricity_cost)

        // SharedPreferences에서 이전에 저장된 전기 비용 로드
        val savedElectricityCost = loadElectricityCost()
        val savedWaterCost = loadWaterCost()
        val savedGasCost = loadGasCost()

        editText.setText(savedElectricityCost.toString())

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val currentElectricityCost = s.toString().toIntOrNull() ?: 0
                saveElectricityCost(currentElectricityCost)
                updateCalculations(currentElectricityCost, savedWaterCost, savedGasCost)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        updateCalculations(savedElectricityCost, savedWaterCost, savedGasCost)

        val goToGasButton = findViewById<TextView>(R.id.electricity_G_btn)
        val goToWaterButton = findViewById<TextView>(R.id.electricity_W_btn)

        goToGasButton.setOnClickListener {//홈버튼
            navigateTo(co2Calculate_gas::class.java, savedElectricityCost, savedWaterCost, savedGasCost)
        }

        goToWaterButton.setOnClickListener {
            navigateTo(co2Calculate_water::class.java, savedElectricityCost, savedWaterCost, savedGasCost)
        }

        val return_Button = findViewById<TextView>(R.id.electricity_return_btn)

        return_Button.setOnClickListener {
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        val complete_Button = findViewById<TextView>(R.id.electricity_complete_btn)

        complete_Button.setOnClickListener {
            //파이어베이스에 추가
        }

        val goTohomeButton = findViewById<ImageView>(R.id.homebutton)
        val goToco2Button = findViewById<ImageView>(R.id.co2button)
        val goTomarketButton = findViewById<ImageView>(R.id.marketbutton)
        val goToOptionButton = findViewById<ImageView>(R.id.optionbutton)

        val camerabtn = findViewById<ImageView>(R.id.electricity_cameravector)
        camerabtn.setOnClickListener{
            Toast.makeText(this, "카메라 클릭됨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ocrCamera::class.java))
        }

        goTohomeButton.setOnClickListener {//홈버튼
            val intent = Intent(this, mainMenu::class.java)
            startActivity(intent)
            finish()
        }

        goToco2Button.setOnClickListener {//co2 관련 버튼
            val intent = Intent(this, co2Calculate::class.java)
            startActivity(intent)
            finish()
        }

        goTomarketButton.setOnClickListener{//마켓 거래 관련 버튼

        }

        goToOptionButton.setOnClickListener{//기타 설정 버튼

        }
    }

    private fun navigateTo(destination: Class<*>, electricityCost: Int, waterCost: Int, gasCost: Int) {
        val intent = Intent(this, destination).apply {
            putExtra("electricity_cost", electricityCost)
            putExtra("water_cost", waterCost)
            putExtra("gas_cost", gasCost)
        }
        startActivity(intent)
        finish()
    }

    private fun updateCalculations(electricityCost: Int, waterCost: Int, gasCost: Int) {
        val co2ElectricityEmission = electricityCost * 0.4781
        val treesElectricityNeeded = electricityCost * 0.1157
        val co2GasEmission = gasCost * 2.176
        val treesGasNeeded = gasCost * 0.5268
        val co2WaterEmission = waterCost * 0.237
        val treesWaterNeeded = waterCost * 0.0573

        val carbonTextView = findViewById<TextView>(R.id.electricity_carbon)
        val treeTextView = findViewById<TextView>(R.id.electricity_tree)
        val totalCarbonTextView = findViewById<TextView>(R.id.electricity_total_carbon)
        val totalTreeTextView = findViewById<TextView>(R.id.electricity_total_tree)

        carbonTextView.text = "${String.format("%.1f", co2ElectricityEmission)} KG"
        treeTextView.text = "${String.format("%.1f", treesElectricityNeeded)} 개"
        totalCarbonTextView.text = "${String.format("%.1f", co2ElectricityEmission + co2GasEmission + co2WaterEmission)} KG"
        totalTreeTextView.text = "${String.format("%.1f", treesElectricityNeeded + treesGasNeeded + treesWaterNeeded)} 개"
    }

    private fun saveElectricityCost(cost: Int) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putInt("electricity_cost", cost)
            apply()
        }
    }

    private fun loadElectricityCost(): Int {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("electricity_cost", 0)
    }

    // Water Cost 로드 함수
    private fun loadWaterCost(): Int {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("water_cost", 0)
    }

    // Gas Cost 로드 함수
    private fun loadGasCost(): Int {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("gas_cost", 0)
    }
}