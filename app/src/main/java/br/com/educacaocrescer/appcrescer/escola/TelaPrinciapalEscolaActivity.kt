package br.com.educacaocrescer.appcrescer.escola

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.educacaocrescer.appcrescer.databinding.ActivityTelaPrinciapalEscolaBinding

class TelaPrinciapalEscolaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityTelaPrinciapalEscolaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        Spinner()
        binding.btnListarCriancas.setOnClickListener {
            val turmaSelecionada = binding.spinnerTurmas.selectedItem.toString()
            val intent = Intent(this, ListarCriancasActivity::class.java)
            intent.putExtra("turmaSelecionada", turmaSelecionada)
            startActivity(intent)
            Log.d("TURMA","$turmaSelecionada")
        }

        binding.btnCadastrarCrianca.setOnClickListener {
            acessarCadCrianca()
        }


        binding.btnCadastrarProfessor.setOnClickListener {
            acessarCadProfessor()
        }


    }

    private fun acessarCadCrianca() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle("Autenticação necessária")
            .setMessage("Digite a senha da diretora para prosseguir:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val senhaDigitada = input.text.toString()
                val senhaDiretora = "admin123"

                if (senhaDigitada == senhaDiretora) {
                    startActivity(Intent(this, CadastroCriancaActivity::class.java))
                } else{
                    Toast.makeText(this, "Senha Incorreta!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar",null)
            .show()
    }

    private fun acessarCadProfessor() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle("Autenticação necessária")
            .setMessage("Digite a senha da diretora para prosseguir:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val senhaDigitada = input.text.toString()
                val senhaDiretora = "admin123"

                if (senhaDigitada == senhaDiretora) {
                    startActivity(Intent(this, CadastroProfessorActivity::class.java))
                } else{
                    Toast.makeText(this, "Senha Incorreta!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar",null)
            .show()
    }

    private fun Spinner() {
        val turmas = listOf("Berçário 1", "Berçário 2", "Maternal 1", "Maternal 2 A", "Maternal 2 B", "Pré-escola")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, turmas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTurmas.adapter = adapter

    }
}