package br.com.educacaocrescer.appcrescer.dataClass

data class Registro(
    val data: String = "",
    val presenca: Boolean = false,
    val lancheManha: String = "",
    val almoco: String = "",
    val lancheTarde: String = "",
    val janta: String = "",
    val soninho: Boolean = false,
    val evacuacao: Boolean = false,
    val observacoes: String = ""
)
