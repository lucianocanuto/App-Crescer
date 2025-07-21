package br.com.educacaocrescer.appcrescer.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.dataClass.Crianca
import br.com.educacaocrescer.appcrescer.escola.RegistroDiarioActivity
import com.bumptech.glide.Glide

class ListarCriancaAdapter (
    private val lista: List<Crianca>,
    private val onItemClick: (Crianca) -> Unit   // callback para clique
) : RecyclerView.Adapter<ListarCriancaAdapter.CriancaViewHolder>() {

    inner class CriancaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto = itemView.findViewById<ImageView>(R.id.imgFoto)
        val txtNome = itemView.findViewById<TextView>(R.id.txtNome)
        val txtTurma = itemView.findViewById<TextView>(R.id.txtTurma)
        val txtTurno = itemView.findViewById<TextView>(R.id.txtTurno)

        init {
            itemView.setOnClickListener {
                onItemClick(lista[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriancaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recycler_lista_criancas, parent, false)
        return CriancaViewHolder(view)
    }

    private var lastPosition = -1

    override fun onBindViewHolder(holder: CriancaViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val crianca = lista[position]
        holder.txtNome.text = crianca.nome
        holder.txtTurma.text = crianca.turma
        holder.txtTurno.text = crianca.turno

        Glide.with(holder.itemView.context)
            .load(crianca.fotoUrl)
            .placeholder(R.drawable.perfil)
            .into(holder.imgFoto)

        // Animação de entrada
        /*if (position > lastPosition) {
            holder.itemView.alpha = 0f
            holder.itemView.translationY = 50f
            holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .start()
            lastPosition = position
        }*/

        // Clique no item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RegistroDiarioActivity::class.java).apply {
                putExtra("idCrianca", crianca.id)
                putExtra("nomeCrianca", crianca.nome)
                putExtra("turma", crianca.turma)
                putExtra("turno", crianca.turno)
                putExtra("telefoneEmergencia", crianca.telefoneEmergencia)
                putExtra("nomePai", crianca.nomePai)
                putExtra("nomeMae", crianca.nomeMae)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = lista.size
}