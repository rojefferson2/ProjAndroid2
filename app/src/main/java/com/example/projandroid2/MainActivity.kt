package com.example.projandroid2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projandroid2.adapter.UbsAdapter
import com.example.projandroid2.model.Ubs
import com.example.projandroid2.network.UbsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.projandroid2.data.AppDatabase
import com.example.projandroid2.model.Favorito
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.launch
import android.widget.Button
import androidx.work.*
import com.google.gson.Gson
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var adapter: UbsAdapter
    private var listaCompleta: List<Ubs> = emptyList()
    private val ubsFavoritas = mutableListOf<Ubs>()



    private fun mostrarNotificacaoFavorito(ubs: Ubs) {
        val builder = NotificationCompat.Builder(this, "canal_favorito")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ✅ AQUI!
            .setContentTitle("UBS Favoritada")
            .setContentText("Você favoritou: ${ubs.nome_oficial}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // with(NotificationManagerCompat.from(this)) {
        //   notify(ubs.nome_oficial.hashCode(), builder.build())
        //}
    }
    private fun salvarFavorito(ubsSelecionada: Ubs) {
        val db = AppDatabase.getDatabase(this)
        val favoritoDao = db.favoritoDao()

        lifecycleScope.launch {
            val favorito = Favorito(
                nome_oficial = ubsSelecionada.nome_oficial,
                bairro = ubsSelecionada.bairro,
                especialidade = ubsSelecionada.especialidade
            )

            // Salva no banco
            favoritoDao.inserir(favorito)

            // Adiciona à lista local se ainda não estiver
            if (!ubsFavoritas.any { it.nome_oficial == ubsSelecionada.nome_oficial }) {
                ubsFavoritas.add(ubsSelecionada)
            }

            // Notificação
            mostrarNotificacaoFavorito(ubsSelecionada)
        }
    }

    private fun carregarCache(): List<Ubs> {
        val arquivo = File(cacheDir, "ubs_cache.json")
        return if (arquivo.exists() && arquivo.readText().isNotBlank()) {
            val json = arquivo.readText()
            Gson().fromJson(json, Array<Ubs>::class.java).toList()
        } else {
            emptyList()
        }
    }

//    private fun agendarAtualizacaoPeriodica() {
//        val workRequest = PeriodicWorkRequestBuilder<com.example.projmobile.worker.UbsWorker>(15, TimeUnit.MINUTES).build()
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "UBS_WORK",
//            ExistingPeriodicWorkPolicy.UPDATE,
//            workRequest
//        )
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "canal_favorito",
                "Favoritos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificações de unidades favoritedas"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        //agendarAtualizacaoPeriodica()


        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("UBS_WORK")
            .observe(this) { workInfos ->
                val finished = workInfos.firstOrNull { it.state.isFinished }
                if (finished != null) {
                    listaCompleta = carregarCache()
                    adapter = UbsAdapter(listaCompleta) { salvarFavorito(it) }
                    recyclerView.adapter = adapter
                }
            }


        val db = AppDatabase.getDatabase(this)
        val favoritoDao = db.favoritoDao()

        spinner = findViewById(R.id.spinnerEspecialidades)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val favoritosSalvos = favoritoDao.listar()
            val favoritosConvertidos = favoritosSalvos.map {
                Ubs(
                    nome_oficial = it.nome_oficial,
                    bairro = it.bairro,
                    especialidade = it.especialidade
                )
            }
            ubsFavoritas.clear()
            ubsFavoritas.addAll(favoritosConvertidos)
        }


        val btnTodas = findViewById<Button>(R.id.btnTodas)
        val btnFavoritas = findViewById<Button>(R.id.btnFavoritas)

        btnTodas.setOnClickListener {
            adapter = UbsAdapter(listaCompleta) { ubsSelecionada ->
                salvarFavorito(ubsSelecionada)
            }
            recyclerView.adapter = adapter
        }

        btnFavoritas.setOnClickListener {
            adapter = UbsAdapter(ubsFavoritas) { ubsSelecionada ->
                salvarFavorito(ubsSelecionada)
            }
            recyclerView.adapter = adapter
        }

        adapter = UbsAdapter(listaCompleta){ ubsSelecionada ->
            mostrarNotificacaoFavorito(ubsSelecionada)
        }
        recyclerView.adapter = adapter
        // Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.22.77.216:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(UbsApi::class.java)

        api.getUbs().enqueue(object : Callback<List<Ubs>> {
            override fun onResponse(call: Call<List<Ubs>>, response: Response<List<Ubs>>) {
                if (response.isSuccessful) {
                    listaCompleta = response.body() ?: emptyList()

                    // Exibir lista completa inicialmente
                    adapter = UbsAdapter(listaCompleta) { ubsSelecionada ->
                        salvarFavorito(ubsSelecionada)
                    }
                    recyclerView.adapter = adapter

                    // Pegar especialidades únicas
                    val especialidades = listaCompleta
                        .flatMap { it.especialidade.split(",").map { esp -> esp.trim() } }
                        .distinct()
                        .sorted()

                    val spinnerAdapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_item,
                        listOf("Todas") + especialidades
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = spinnerAdapter

                    // Ao selecionar no Spinner, filtra a lista
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selecionada = parent.getItemAtPosition(position).toString()

                            val filtrada = if (selecionada == "Todas") {
                                listaCompleta
                            } else {
                                listaCompleta.filter {
                                    it.especialidade.contains(selecionada, ignoreCase = true)
                                }
                            }

                            adapter = UbsAdapter(filtrada) { ubsSelecionada ->
                                salvarFavorito(ubsSelecionada)
                            }
                            recyclerView.adapter = adapter
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }
            }

            override fun onFailure(call: Call<List<Ubs>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })



    }
}