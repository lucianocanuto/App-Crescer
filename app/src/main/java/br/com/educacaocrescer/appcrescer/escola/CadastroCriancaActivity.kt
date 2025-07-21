package br.com.educacaocrescer.appcrescer.escola

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.educacaocrescer.appcrescer.R
import br.com.educacaocrescer.appcrescer.databinding.ActivityCadastroCriancaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class CadastroCriancaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroCriancaBinding.inflate(layoutInflater)
    }
    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }
    private val enviar by lazy {
        FirebaseStorage.getInstance()
    }

    private var imageUri: Uri? = null

    private lateinit var abrirCamera: ActivityResultLauncher<Void?>
    private lateinit var abrirGaleria: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        spinnerTurma()
        spinnerTurno()
        capturarImagem()

    binding.btnSalvarCrianca.setOnClickListener {
        cadastrarCrianca()
    }





    }

    private fun cadastrarCrianca() {
        val nome = binding.edtNomeCrianca.text.toString().trim()
        val dataNascimento = binding.edtDataNascimento.text.toString().trim()
        val turma = binding.spinnerTurma.selectedItem.toString()
        val turno = binding.spinnerTurno.selectedItem.toString()
        val nomePai = binding.edtNomePai.text.toString().trim()
        val nomeMae = binding.edtNomeMae.text.toString().trim()
        val telefoneDigitado = binding.edtTelefoneEmergencia.text.toString().trim()
        val telefoneLimpo = telefoneDigitado.replace(Regex("[^\\d]"), "")
        val telefoneEmergencia = "+55$telefoneLimpo"

        Log.d("CadastroCrianca", "Telefone formatado: $telefoneEmergencia")

        if (nome.isEmpty() || dataNascimento.isEmpty() || turma == "Selecione a turma" ||
            turno == "Selecione o turno" || nomePai.isEmpty() || nomeMae.isEmpty() || telefoneEmergencia.isEmpty()
        ) {
            Toast.makeText(this, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Escolha ou tire uma foto da criança", Toast.LENGTH_SHORT).show()
            return
        }
        // Consulta para verificar duplicidade
        bancoDados.collection("criancas")
            .whereEqualTo("nome", nome)
            .whereEqualTo("dataNascimento", dataNascimento)
            .whereEqualTo("turma", turma)
            .whereEqualTo("telefoneEmergencia", telefoneEmergencia)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    Toast.makeText(
                        this,
                        "Essa criança já está cadastrada com esse telefone e data de nascimento.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
                    val nomeFoto = "${nome}_${System.currentTimeMillis()}.jpg"
                    val storageRef = enviar.reference.child("fotos_criancas/$nomeFoto")

                    storageRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { fotoUrl ->

                                val dadosCrianca = hashMapOf(
                                    "nome" to nome,
                                    "dataNascimento" to dataNascimento,
                                    "turma" to turma,
                                    "turno" to turno,
                                    "nomePai" to nomePai,
                                    "nomeMae" to nomeMae,
                                    "telefoneEmergencia" to telefoneEmergencia,
                                    "fotoUrl" to fotoUrl.toString(),
                                    "ano" to anoAtual
                                )
                                bancoDados.collection("criancas")
                                    .add(dadosCrianca)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Criança cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Erro ao salvar no banco", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show()
                        }

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar duplicidade", Toast.LENGTH_SHORT).show()
            }

    }






    private fun capturarImagem() {
        // Câmera
        abrirCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                binding.imgFotoCrianca.setImageBitmap(it)
            }
        }

        // Galeria
        abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imgFotoCrianca.setImageURI(it)
                imageUri = it
            }
        }
        binding.btnAbrirCamera.setOnClickListener {
            abrirCamera.launch(null)
        }

        binding.btnAbrirGaleria.setOnClickListener {
            abrirGaleria.launch("image/*")
        }



    }

    private fun spinnerTurno() {
        val turnos = listOf("Selecione o turno", "Manhã", "Tarde", "Integral")

        val adapterTurnos = ArrayAdapter(this, android.R.layout.simple_spinner_item, turnos)
        adapterTurnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTurno.adapter = adapterTurnos
    }

    private fun spinnerTurma() {
        val turmas = listOf(
            "Selecione a turma",
            "Berçário 1", "Berçário 2", "Maternal 1",
            "Maternal 2 A", "Maternal 2 B", "Pré-escola"
        )

        val adapterTurmas = ArrayAdapter(this, android.R.layout.simple_spinner_item, turmas)
        adapterTurmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTurma.adapter = adapterTurmas
    }
}