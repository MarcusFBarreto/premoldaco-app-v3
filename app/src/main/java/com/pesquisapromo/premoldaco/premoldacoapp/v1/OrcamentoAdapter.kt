package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class OrcamentoAdapter(private var orcamentos: List<Orcamento>) :
    RecyclerView.Adapter<OrcamentoAdapter.OrcamentoViewHolder>() {

    class OrcamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val obraName: TextView = itemView.findViewById(R.id.tvObraName)
        val dataCriacao: TextView = itemView.findViewById(R.id.tvDataCriacao)
        val totalArea: TextView = itemView.findViewById(R.id.tvTotalArea)
        val statusChip: Chip = itemView.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrcamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orcamento, parent, false)
        return OrcamentoViewHolder(view)
    }

    override fun getItemCount() = orcamentos.size

    override fun onBindViewHolder(holder: OrcamentoViewHolder, position: Int) {
        val orcamento = orcamentos[position]

        holder.obraName.text = orcamento.obraName
        // Usando a propriedade formatada que criamos no modelo de dados
        holder.dataCriacao.text = orcamento.dataFormatada
        holder.totalArea.text = "Área Total: ${orcamento.totalArea} m²"
        holder.statusChip.text = orcamento.status

        // Lógica para colorir o status (chip)
        val context = holder.itemView.context
        val statusColor = when (orcamento.status?.uppercase()) {
            "APROVADO" -> R.color.status_aprovado
            "CANCELADO" -> R.color.status_cancelado
            "EM ANÁLISE" -> R.color.status_em_analise
            else -> R.color.status_enviado
        }
        holder.statusChip.setChipBackgroundColorResource(statusColor)
    }

    fun updateData(newOrcamentos: List<Orcamento>) {
        this.orcamentos = newOrcamentos
        notifyDataSetChanged()
    }
}