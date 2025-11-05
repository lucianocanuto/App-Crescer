package br.com.educacaocrescer.appcrescer.responsavel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.educacaocrescer.appcrescer.databinding.ActivityPainelPaisBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PainelPaisActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPainelPaisBinding.inflate(layoutInflater)
    }

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Recupera telefone do responsável (usado no login)
        val telefone = intent.getStringExtra("telefoneEmergencia")

        if (telefone.isNullOrEmpty()) {
            Toast.makeText(this, "Erro ao carregar dados. Faça login novamente.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Busca a criança associada ao telefone
        db.collection("criancas")
            .whereEqualTo("telefoneEmergencia", telefone)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Nenhuma criança encontrada com esse telefone.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                val crianca = result.documents[0]
                val idCrianca = crianca.id
                val nome = crianca.getString("nome") ?: "Sem nome"
                val turma = crianca.getString("turma") ?: "Turma não informada"
                val fotoUrl = crianca.getString("fotoUrl") // pode ser nulo

                binding.txtNomeCrianca.text = nome
                binding.txtTurma.text = turma

                // Carregar foto com Glide
                if (!fotoUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(fotoUrl)
                        .centerCrop()
                        .into(binding.imgFotoCrianca)
                }

                // Buscar o último registro diário da criança
                db.collection("criancas")
                    .document(idCrianca)
                    .collection("registros")
                    .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { registros ->
                        if (registros.isEmpty) {
                            binding.txtData.text = "Sem registros ainda"
                            binding.txtPresenca.text = ""
                            binding.txtResumo.text = ""
                            return@addOnSuccessListener
                        }

                        val registro = registros.documents[0]
                        val dataExibicao = registro.getString("dataExibicao") ?: "Data não informada"
                        val presenca = if (registro.getBoolean("presenca") == true) "Sim" else "Não"
                        val observacoes = registro.getString("observacoes") ?: "Sem observações"

                        binding.txtData.text = "Data: $dataExibicao"
                        binding.txtPresenca.text = "Presença: $presenca"
                        binding.txtResumo.text = "Observações: $observacoes"
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao carregar registros.", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar dados da criança.", Toast.LENGTH_SHORT).show()
            }

        // Clique do botão de ver registros
        binding.btnVerRegistros.setOnClickListener {
            Toast.makeText(this, "Abrir tela de registros anteriores", Toast.LENGTH_SHORT).show()
            // Aqui futuramente você abre uma nova tela com os registros completos
        }

        // Clique do botão de mensagens
        binding.btnMensagens.setOnClickListener {
            Toast.makeText(this, "Abrir mensagens com a escola", Toast.LENGTH_SHORT).show()
            // Aqui abrirá a tela de chat com a escola
        }
    }
}
