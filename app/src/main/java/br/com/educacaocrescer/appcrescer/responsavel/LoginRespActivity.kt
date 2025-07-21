package br.com.educacaocrescer.appcrescer.responsavel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.databinding.ActivityLoginRespBinding

class LoginRespActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginRespBinding.inflate(layoutInflater)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}