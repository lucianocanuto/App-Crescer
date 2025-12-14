package br.com.educacaocrescer.appcrescer.escola

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        carregarRegistros() // carrega todos do Firestore
        configurarSpinnerMeses() // configura o filtro
    }

    private fun configurarSpinnerMeses() {val meses = listOf(
        "Todos",
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

        val adapterSpinner = ArrayAdapter(this, R.layout.simple_spinner_item, meses)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMeses.adapter = adapterSpinner

        binding.spinnerMeses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val mesSelecionado = position // 0 = Todos, 1 = Janeiro, 2 = Fevereiro...
                filtrarRegistrosPorMes(mesSelecionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filtrarRegistrosPorMes(mes: Int) {
        val registrosFiltrados = if (mes == 0) {
            listaRegistros // todos
        } else {
            listaRegistros.filter { registro ->
                val mesRegistro = registro.data.substring(5, 7).toInt() // pega MM de yyyy-MM-dd
                mesRegistro == mes
            }
        }
        adapter = RegistroAdapter(registrosFiltrados)
        binding.recyclerRegistros.adapter = adapter
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