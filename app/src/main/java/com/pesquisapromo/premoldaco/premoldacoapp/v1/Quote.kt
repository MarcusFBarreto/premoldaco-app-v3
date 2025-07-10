package com.pesquisapromo.premoldaco.premoldacoapp.v1

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class Quote(
    val userId: String = "",
    val clienteNome: String = "",
    val clienteEmail: String = "",
    val clienteTelefone: String = "",
    val clienteObservacoes: String = "",
    val obraName: String = "", // Adicionado para consistência
    val status: String = "",   // Adicionado para consistência
    val tipoLaje: String = "", // Adicionado para consistência
    val totalArea: String = "",// Adicionado para consistência
    val dataCriacao: Timestamp? = null, // <-- MUDANÇA PRINCIPAL AQUI
    val comodos: List<Map<String, Any>> = emptyList()
) {
    val formattedDate: String
        get() {
            return if (dataCriacao != null) { // <-- MUDANÇA AQUI
                val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
                sdf.format(dataCriacao.toDate()) // <-- MUDANÇA AQUI
            } else {
                "Data não disponível"
            }
        }
}