package br.com.educacaocrescer.appcrescer.escola

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.educacaocrescer.appcrescer.databinding.ActivityLoginProfessorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginProfessorActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginProfessorBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // ✅ Verifica se o professor já está logado
        val usuarioAtual = auth.currentUser
        if (usuarioAtual != null && !usuarioAtual.isAnonymous) {
            // Já está logado → vai direto para a tela principal da escola
            startActivity(Intent(this, TelaPrinciapalEscolaActivity::class.java))
            finish()
            return
        }

        // 🧩 Se não estiver logado, continua com a tela normal
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    Toast.makeText(this, "Logado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, TelaPrinciapalEscolaActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao logar: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
