package br.com.educacaocrescer.appcrescer.escola

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.educacaocrescer.appcrescer.databinding.ActivityRegistroDiarioBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistroDiarioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegistroDiarioBinding.inflate(layoutInflater)
    }
    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }
    private val enviar by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val dataFormatada = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        binding.txtDataAtual.text = "Data: $dataFormatada"
       // dataAtual()
        configurarSpinners()
        val id = intent.getStringExtra("idCrianca")

        binding.txtNomeCrianca.text = intent.getStringExtra("nomeCrianca")
        binding.txtNomePai.text = intent.getStringExtra("nomePai")
        binding.txtNomeMae.text = intent.getStringExtra("nomeMae")
        binding.txtTelefoneEmergencia.text = intent.getStringExtra("telefoneEmergencia")

        binding.btnSalvarRegistro.setOnClickListener {
            val idCrianca = intent.getStringExtra("idCrianca") ?: return@setOnClickListener
            val data = binding.txtDataAtual.text.toString().replace("Data: ", "")
            val presenca = binding.switchPresenca.isChecked
            val soninho = binding.switchSoninho.isChecked
            val evacuacao = binding.switchEvacuacao.isChecked
            val observacoes = binding.edtObservacoes.text.toString().trim()

            val lancheManha = binding.spinnerLancheManha.selectedItem.toString()
            val almoco = binding.spinnerAlmoco.selectedItem.toString()
            val lancheTarde = binding.spinnerLancheTarde.selectedItem.toString()
            val janta = binding.spinnerJanta.selectedItem.toString()

            val registro = hashMapOf(
                "data" to dataFormatada,
                "presenca" to binding.switchPresenca.isChecked,
                "lancheManha" to binding.spinnerLancheManha.selectedItem.toString(),
                "almoco" to binding.spinnerAlmoco.selectedItem.toString(),
                "lancheTarde" to binding.spinnerLancheTarde.selectedItem.toString(),
                "janta" to binding.spinnerJanta.selectedItem.toString(),
                "soninho" to binding.switchSoninho.isChecked,
                "evacuacao" to binding.switchEvacuacao.isChecked,
                "observacoes" to binding.edtObservacoes.text.toString().trim()
            )

            //val idCrianca = intent.getStringExtra("idCrianca") ?: return@setOnClickListener

            val db = FirebaseFirestore.getInstance()
            db.collection("criancas")
                .document(idCrianca)
                .collection("registros")
                .document(dataFormatada) // aqui é o nome do documento, 1 por dia
                .set(registro)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar o registro", Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun configurarSpinners() {
        val opcoesRefeicao = listOf("Selecione", "Comeu tudo", "Comeu pouco", "Não comeu", "Recusou")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, opcoesRefeicao).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerLancheManha.adapter = adapter
        binding.spinnerAlmoco.adapter = adapter
        binding.spinnerLancheTarde.adapter = adapter
        binding.spinnerJanta.adapter = adapter
    }


    /*private fun dataAtual() {
        val dataFormatada = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        binding.txtDataAtual.text = "Data: $dataFormatada"
    }*/

}