package br.com.educacaocrescer.appcrescer.escola

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.educacaocrescer.appcrescer.adapters.RegistroAdapter
import br.com.educacaocrescer.appcrescer.dataClass.Registro
import br.com.educacaocrescer.appcrescer.databinding.ActivityListarRegistrosBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListarRegistrosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListarRegistrosBinding
    private val listaRegistros = mutableListOf<Registro>()
    private lateinit var adapter: RegistroAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var idCrianca: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListarRegistrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idCrianca = intent.getStringExtra("idCrianca") ?: return
        db = FirebaseFirestore.getInstance()

        adapter = RegistroAdapter(listaRegistros)
        binding.recyclerRegistros.layoutManager = LinearLayoutManager(this)
        binding.recyclerRegistros.adapter = adapter

        carregarRegistros()
    }

    private fun carregarRegistros() {
        db.collection("criancas")
            .document(idCrianca)
            .collection("registros")
            .orderBy("data")
            .get()
            .addOnSuccessListener { snapshot ->
                listaRegistros.clear()
                for (doc in snapshot) {
                    val registro = doc.toObject(Registro::class.java)
                    listaRegistros.add(registro)
                }
                listaRegistros.reverse() // mostra os mais recentes primeiro
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar registros", Toast.LENGTH_SHORT).show()
            }
    }
}