package com.pesquisapromo.premoldaco.premoldacoapp.v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PremoldaçoApp) // Mesclagem: Adicionado para evitar crashes de tema
        super.onCreate(savedInstanceState)

        // --- LÓGICA DE DIRECIONAMENTO ---
        // Antes de mostrar a tela, verificamos se o usuário já está logado
        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.isAnonymous) {
            // Se já existe um usuário permanente, vai direto para o perfil dele
            navigateToProfile()
            return // 'return' impede que o resto do onCreate seja executado
        }

        // Se não há usuário logado, mostra a tela de boas-vindas
        setContentView(R.layout.activity_welcome)

        // --- LISTENERS DOS BOTÕES ---
        val btnGoToAuth = findViewById<Button>(R.id.btnGoToAuth)
        val btnGoToCalculator = findViewById<Button>(R.id.btnGoToCalculator)

        // Botão para o fluxo de login/registro
        btnGoToAuth.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // Botão para o fluxo anônimo
        btnGoToCalculator.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        // Flags para garantir que o usuário não possa "voltar" para a tela de boas-vindas
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Fecha a WelcomeActivity
    }
}