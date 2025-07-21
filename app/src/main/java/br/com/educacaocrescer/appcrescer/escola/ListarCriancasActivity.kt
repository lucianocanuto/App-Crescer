package br.com.educacaocrescer.appcrescer.escola

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.adapters.ListarCriancaAdapter
import br.com.educacaocrescer.appcrescer.dataClass.Crianca
import br.com.educacaocrescer.appcrescer.databinding.ActivityListarCriancasBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarCriancasActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListarCriancasBinding.inflate(layoutInflater)
    }
    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }
    private val enviar by lazy {
        FirebaseStorage.getInstance()
    }

    private val listaCriancas = mutableListOf<Crianca>()
    private lateinit var adapter: ListarCriancaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        adapter = ListarCriancaAdapter(listaCriancas){  crianca ->
            val intent = Intent(this, RegistroDiarioActivity::class.java)
            intent.putExtra("idCrianca", crianca.id)
            intent.putExtra("nomeCrianca", crianca.nome)
            intent.putExtra("telefoneEmergencia", crianca.telefoneEmergencia)
            intent.putExtra("nomePai", crianca.nomePai)
            intent.putExtra("nomeMae", crianca.nomeMae)
            startActivity(intent)

        }

        binding.rvListaCrianca.layoutManager = LinearLayoutManager(this)
        binding.rvListaCrianca.adapter = adapter


        carregarCriancas()


    }

    private fun carregarCriancas() {
        val turmaSelecionada = intent.getStringExtra("turmaSelecionada") ?: return

        FirebaseFirestore.getInstance().collection("criancas")
            .whereEqualTo("turma", turmaSelecionada)
            .get()
            .addOnSuccessListener { docs ->
                listaCriancas.clear()
                for (doc in docs) {
                    val crianca = doc.toObject(Crianca::class.java)
                    crianca.id = doc.id  // pega o ID do documento
                    listaCriancas.add(crianca)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar crianças", Toast.LENGTH_SHORT).show()
            }
    }


}