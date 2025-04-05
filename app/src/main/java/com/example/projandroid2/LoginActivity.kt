package com.example.projandroid2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvCadastrar: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar componentes
        etEmail = findViewById(R.id.etEmail)
        etSenha = findViewById(R.id.etSenha)
        btnLogin = findViewById(R.id.btnLogin)
        tvCadastrar = findViewById(R.id.tvCadastrar)
        progressBar = findViewById(R.id.progressBar)

        // Configurar listeners
        btnLogin.setOnClickListener {
            realizarLogin()
        }

        tvCadastrar.setOnClickListener {
            // Intent para tela de cadastro
            //val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin() {
        val email = etEmail.text?.toString()?.trim() ?: ""
        val senha = etSenha.text?.toString()?.trim() ?: ""

        if (validarCampos(email, senha)) {
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            try {
                if (email == "admin" && senha == "admin") {
                    // Login bem-sucedido
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email ou senha incorretos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }

    private fun validarCampos(email: String, senha: String): Boolean {
//        if (email.isEmpty()) {
//            etEmail.error = "Email é obrigatório"
//            return false
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            etEmail.error = "Email inválido"
//            return false
//        }

//        if (senha.isEmpty()) {
//            etSenha.error = "Senha é obrigatória"
//            return false
//        }
//
//        if (senha.length < 6) {
//            etSenha.error = "Senha deve ter pelo menos 6 caracteres"
//            return false
//        }

        return true
    }
}