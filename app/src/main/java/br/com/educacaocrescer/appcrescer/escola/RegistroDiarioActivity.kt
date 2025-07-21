package br.com.educacaocrescer.appcrescer.escola

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.databinding.ActivityRegistroDiarioBinding

class RegistroDiarioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegistroDiarioBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val id = intent.getStringExtra("idCrianca")


    }
}