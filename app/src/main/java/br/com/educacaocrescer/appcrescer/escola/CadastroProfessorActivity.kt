package br.com.educacaocrescer.appcrescer.escola

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.databinding.ActivityCadastroProfessorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroProfessorActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroProfessorBinding.inflate(layoutInflater)
    }
    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }
    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {
            cadastrarProfessor()
        }

    }

    private fun cadastrarProfessor() {
        val nome = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || senha.length < 6) {
            Toast.makeText(this, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()

            binding.etNome.text.clear()
            binding.etEmail.text.clear()
            binding.etSenha.text.clear()
            binding.etNome.requestFocus()
            return
        }
        autenticacao.createUserWithEmailAndPassword( email, senha )
            .addOnSuccessListener { sucesso ->
                val uid = sucesso.user?.uid
                val professor = hashMapOf(
                    "nome" to nome,
                    "email" to email,
                    "cargo" to "professor"
                )
                uid?.let {
                    bancoDados.collection("professores")
                        .document(uid)
                        .set(professor)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Professor cadastrado com sucesso!",Toast.LENGTH_SHORT).show()
                            finish()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao salvar os dados!", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao cadastrar o usuário! ${erro.message}", Toast.LENGTH_SHORT).show()
            }

    }
}