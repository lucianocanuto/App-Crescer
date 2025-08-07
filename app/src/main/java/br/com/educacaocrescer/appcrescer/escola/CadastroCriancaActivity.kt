package br.com.educacaocrescer.appcrescer.escola

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.educacaocrescer.appcrescer.databinding.ActivityCadastroCriancaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
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
    private var bitmapDaCamera: Bitmap? = null

    private lateinit var abrirCamera: ActivityResultLauncher<Void?>
    private lateinit var abrirGaleria: ActivityResultLauncher<String>

    private val REQUEST_CAMERA_PERMISSION = 100

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

        if (imageUri == null && bitmapDaCamera == null) {
            Toast.makeText(this, "Escolha ou tire uma foto da criança", Toast.LENGTH_SHORT).show()
            return
        }

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

                    if (imageUri != null) {
                        storageRef.putFile(imageUri!!)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { fotoUrl ->
                                    salvarDadosCrianca(fotoUrl.toString(), nome, dataNascimento, turma, turno, nomePai, nomeMae, telefoneEmergencia, anoAtual)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show()
                            }
                    } else if (bitmapDaCamera != null) {
                        val baos = ByteArrayOutputStream()
                        bitmapDaCamera!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val uploadTask = storageRef.putBytes(data)
                        uploadTask.addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { fotoUrl ->
                                salvarDadosCrianca(fotoUrl.toString(), nome, dataNascimento, turma, turno, nomePai, nomeMae, telefoneEmergencia, anoAtual)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Erro ao enviar imagem da câmera", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao verificar duplicidade", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarDadosCrianca(
        fotoUrl: String,
        nome: String,
        dataNascimento: String,
        turma: String,
        turno: String,
        nomePai: String,
        nomeMae: String,
        telefoneEmergencia: String,
        ano: Int
    ) {
        val dadosCrianca = hashMapOf(
            "nome" to nome,
            "dataNascimento" to dataNascimento,
            "turma" to turma,
            "turno" to turno,
            "nomePai" to nomePai,
            "nomeMae" to nomeMae,
            "telefoneEmergencia" to telefoneEmergencia,
            "fotoUrl" to fotoUrl,
            "ano" to ano
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

    private fun capturarImagem() {
        abrirCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                binding.imgFotoCrianca.setImageBitmap(it)
                bitmapDaCamera = it
                imageUri = null
            }
        }

        abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imgFotoCrianca.setImageURI(it)
                imageUri = it
                bitmapDaCamera = null
            }
        }

        binding.btnAbrirCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                abrirCamera.launch(null)
            }
        }

        binding.btnAbrirGaleria.setOnClickListener {
            abrirGaleria.launch("image/*")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera.launch(null)
            } else {
                Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun spinnerTurno() {
        val turnos = listOf("Selecione o turno", "Manhã", "Tarde", "Integral")
        val adapterTurnos = ArrayAdapter(this, android.R.layout.simple_spinner_item, turnos)
        adapterTurnos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTurno.adapter = adapterTurnos
    }

    private fun spinnerTurma() {
        val turmas = listOf("Selecione a turma", "Berçário 1", "Berçário 2", "Maternal 1", "Maternal 2 A", "Maternal 2 B", "Pré-escola")
        val adapterTurmas = ArrayAdapter(this, android.R.layout.simple_spinner_item, turmas)
        adapterTurmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTurma.adapter = adapterTurmas
    }
}