package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuoteAdapter(private val quotes: List<Quote>) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    // Cria a ViewHolder, inflando o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(view)
    }

    // Vincula os dados de um orçamento específico à view
    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quotes[position]
        holder.bind(quote)
    }

    // Retorna o número total de itens na lista
    override fun getItemCount(): Int = quotes.size

    // A classe que representa a view de cada item da lista
    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvObraName: TextView = itemView.findViewById(R.id.tvItemObraName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvItemDate)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvItemStatus)

        fun bind(quote: Quote) {
            tvObraName.text = quote.obraName
            tvDate.text = quote.formattedDate
            // Você pode adicionar lógica para o status aqui, se necessário
            tvStatus.text = "Status: ENVIADO"
        }
    }
}