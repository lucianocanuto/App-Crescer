package br.com.educacaocrescer.appcrescer.escola

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.databinding.ActivityLoginProfessorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginProfessorActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginProfessorBinding.inflate(layoutInflater)
    }
    private val log by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            log.signInWithEmailAndPassword( email.toString(), senha.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Logado com sucesso!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, TelaPrinciapalEscolaActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao logar!", Toast.LENGTH_LONG).show()
                    finish()
                }

        }

    }
}