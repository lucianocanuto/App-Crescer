package br.com.educacaocrescer.appcrescer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.educacaocrescer.appcrescer.dataClass.Registro
import br.com.educacaocrescer.appcrescer.databinding.ItemRegistroBinding

class RegistroAdapter (
    private val listaRegistros: List<Registro>
) : RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>(){
    inner class RegistroViewHolder(val binding: ItemRegistroBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val binding = ItemRegistroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegistroViewHolder(binding)
}

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = listaRegistros[position]

        holder.binding.txtDataRegistro.text = "Data: ${registro.data}"

        val resumo = buildString {
            append("Presença: ${if (registro.presenca) "Sim" else "Não"}")
            append(" | Almoço: ${registro.almoco}")
            if (registro.soninho) append(" | Dormiu")
            if (registro.evacuacao) append(" | Evacuou")
        }
        holder.binding.txtResumoRegistro.text = resumo
        holder.binding.txtObservacoesRegistro.text =
            if (registro.observacoes.isNotEmpty())
                "Observações: ${registro.observacoes}"
            else
                "Observações: Nenhuma observação registrada."
    }

    override fun getItemCount() = listaRegistros.size
}