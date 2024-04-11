package com.desarrollo.domapp.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.desarrollo.domapp.R

class SecondAppActivity : AppCompatActivity() {

    private lateinit var viewLuces: CardView
    private lateinit var viewAscensor: CardView
    private lateinit var viewAlarma: CardView
    private var isLucesSelected:Boolean = true
    private var isAscensorSelected:Boolean = false
    private var isAlarmaSelected:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_app)
        initComponents()
        initListeners()
        initUI();
    }

    private fun initComponents() {
        viewLuces = findViewById(R.id.viewLuces)
        viewAscensor = findViewById(R.id.viewAscensor)
        viewAlarma = findViewById(R.id.viewAlarma)
    }

    private fun initListeners() {
        viewLuces.setOnClickListener{
            setGenderColor()
            changeColor()
        }
        viewAscensor.setOnClickListener {
            setGenderColor()
            changeColor()
        }

        viewAlarma.setOnClickListener {
            setGenderColor()
            isAlarmaSelected = !isAlarmaSelected
        }
    }

    private fun changeColor(){
        isLucesSelected = !isLucesSelected
        isAscensorSelected = !isAscensorSelected
    }

    private fun setGenderColor(){
        viewLuces.setCardBackgroundColor(getBackgroundColor(isLucesSelected))
        viewAscensor.setCardBackgroundColor(getBackgroundColor(isAscensorSelected))
        viewAlarma.setCardBackgroundColor(getBackgroundColor((isAlarmaSelected)))
    }

    private fun getBackgroundColor(isSelectedComponet:Boolean):Int{
        val colorReference = if (isSelectedComponet){
            R.color.background_component_selected
        }else
            R.color.background_component
        return ContextCompat.getColor(this, colorReference)
    }

    private fun initUI() {
        setGenderColor()
    }


}