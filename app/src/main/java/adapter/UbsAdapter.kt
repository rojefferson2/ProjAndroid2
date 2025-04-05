package com.example.projandroid2.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projandroid2.R
import com.example.projandroid2.model.Ubs
import android.widget.Button


class UbsAdapter(
    private val lista: List<Ubs>,
    private val onFavoritarClick: (Ubs) -> Unit
) : RecyclerView.Adapter<UbsAdapter.UbsViewHolder>() {


    class UbsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeText: TextView = itemView.findViewById(R.id.nomeText)
        val bairroText: TextView = itemView.findViewById(R.id.bairroText)
        val especialidadeText: TextView = itemView.findViewById(R.id.especialidadeText)
        val btnFavoritar: Button = itemView.findViewById(R.id.btnFavoritar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ubs, parent, false)
        return UbsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UbsViewHolder, position: Int) {
        val ubs = lista[position]
        holder.nomeText.text = ubs.nome_oficial
        holder.bairroText.text = ubs.bairro
        holder.especialidadeText.text = ubs.especialidade

        holder.btnFavoritar.setOnClickListener {
            onFavoritarClick(ubs)
        }
    }

    override fun getItemCount(): Int = lista.size
}
