package br.com.educacaocrescer.appcrescer.dataClass

data class Crianca(
    var id : String = "",
    val nome: String = "",
    val dataNascimento: String = "",
    val turma: String = "",
    val turno: String = "",
    val nomePai: String = "",
    val nomeMae: String = "",
    val telefoneEmergencia: String = "",
    val fotoUrl: String = "",
    val ano: Int = 0
)
