package br.com.educacaocrescer.appcrescer.responsavel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.educacaocrescer.appcrescer.databinding.ActivityLoginRespBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginRespActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginRespBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLoginResp.setOnClickListener {
            val telefone = binding.edtTelefone.text.toString().trim()

            if (telefone.isEmpty()) {
                Toast.makeText(this, "Digite o telefone de emergência!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Busca a criança pelo telefone de emergência
            db.collection("criancas")
                .whereEqualTo("telefoneEmergencia", telefone)
                .get()
                .addOnSuccessListener { resultado ->
                    if (!resultado.isEmpty) {
                        val crianca = resultado.documents[0]
                        val nomeCrianca = crianca.getString("nome") ?: "Desconhecido"
                        val turma = crianca.getString("turma") ?: "Sem turma"

                        // Login anônimo no Firebase
                        auth.signInAnonymously()
                            .addOnSuccessListener {
                                // Envia o telefone e dados básicos para o Painel
                                val intent = Intent(this, PainelPaisActivity::class.java)
                                intent.putExtra("telefoneEmergencia", telefone)
                                intent.putExtra("nomeCrianca", nomeCrianca)
                                intent.putExtra("turma", turma)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao autenticar!", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this, "Telefone não encontrado.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao buscar dados.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
