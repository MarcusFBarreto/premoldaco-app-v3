package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private val TAG = "PROFILE_ACTIVITY_DEBUG"

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoQuotesMessage: TextView
    private lateinit var tvWelcomeUser: TextView
    private lateinit var fabNewQuote: FloatingActionButton
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var adapter: OrcamentoAdapter

    private var quotesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PremoldaçoApp) // Mesclagem: Adicionado para evitar crashes de tema
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Log.d(TAG, "onCreate: Atividade sendo criada.")

        // 1º - Inicializa os serviços do Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        // 2º - Inicializa todas as views do layout
        initializeViews()

        // 3º - Configura os componentes
        setupRecyclerView()
        setupToolbar()
        setupFab()

        // 4º - Verifica se o usuário pode estar aqui
        if (auth.currentUser == null) {
            Log.e(TAG, "ERRO: Usuário nulo no onCreate. Navegando para Welcome.")
            navigateToWelcome()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Atividade ficou visível.")
        auth.currentUser?.let { user ->
            // Agora é seguro usar as views, pois elas foram inicializadas no onCreate
            tvWelcomeUser.text = "Bem-vindo, ${user.email}"
            attachQuotesListener(user.uid)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Atividade ficou invisível. Removendo listener.")
        quotesListener?.remove()
    }

    private fun initializeViews() {
        Log.d(TAG, "initializeViews: Encontrando as views no layout.")
        recyclerView = findViewById(R.id.recyclerViewQuotes)
        progressBar = findViewById(R.id.progressBarProfile)
        tvNoQuotesMessage = findViewById(R.id.tvNoQuotesMessage)
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        fabNewQuote = findViewById(R.id.fabNewQuote)
        topAppBar = findViewById(R.id.topAppBar)
    }

    private fun setupRecyclerView() {
        adapter = OrcamentoAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fabNewQuote.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_EMAIL", auth.currentUser?.email)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    auth.signOut()
                    Toast.makeText(this, "Você saiu da sua conta.", Toast.LENGTH_SHORT).show()
                    navigateToWelcome()
                    true
                }
                else -> false
            }
        }
    }

    private fun attachQuotesListener(userId: String) {
        setLoading(true)
        Log.d(TAG, "attachQuotesListener: Anexando ouvinte para o UID: '$userId'")

        quotesListener?.remove()

        val query = db.collection("orcamentos")
            .whereEqualTo("userId", userId)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)

        quotesListener = query.addSnapshotListener { snapshots, e ->
            setLoading(false)
            if (e != null) {
                Log.e(TAG, "ERRO no listener do Firestore:", e)
                tvNoQuotesMessage.text = "Erro ao carregar orçamentos."
                tvNoQuotesMessage.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                Log.d(TAG, "SUCESSO: Listener recebeu dados! Número de documentos: ${snapshots.size()}")
                val orcamentos = snapshots.toObjects(Orcamento::class.java)
                adapter.updateData(orcamentos)
                tvNoQuotesMessage.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                Log.w(TAG, "AVISO: Listener executou com sucesso, mas a lista de orçamentos está vazia.")
                tvNoQuotesMessage.text = "Você ainda não tem orçamentos. Crie um agora!"
                tvNoQuotesMessage.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}