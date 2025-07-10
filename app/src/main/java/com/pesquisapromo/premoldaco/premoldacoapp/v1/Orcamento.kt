package com.pesquisapromo.premoldaco.premoldacoapp.v1

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// Esta é a "planta" de um documento de Orçamento.
// Os nomes das variáveis devem corresponder aos campos no Firestore.
// Os valores padrão (ex: "") são importantes para o Firebase.
data class Orcamento(
    val userId: String = "",
    val obraName: String = "",
    val clienteNome: String = "",
    val clienteEmail: String = "",
    val clienteTelefone: String = "",
    val status: String = "",
    val tipoLaje: String = "",
    val totalArea: String = "",
    val dataCriacao: Timestamp? = null,
    val comodos: List<Map<String, Any>> = emptyList()
) {
    // Esta é uma "propriedade computada" que formata a data para nós.
    // Facilita a exibição na tela sem sujar o nosso Adapter com essa lógica.
    val dataFormatada: String
        get() {
            return if (dataCriacao != null) {
                val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
                sdf.format(dataCriacao.toDate())
            } else {
                "Data indisponível"
            }
        }
}