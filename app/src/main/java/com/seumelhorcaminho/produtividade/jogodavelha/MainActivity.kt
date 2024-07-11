package com.seumelhorcaminho.produtividade.jogodavelha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seumelhorcaminho.produtividade.jogodavelha.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.random.nextInt


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOffline.setOnClickListener(this)
        binding.btnOnline.setOnClickListener(this)
        binding.btnOnlineJoin.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_offline -> createOfflineGame()
            R.id.btn_online -> createOnlineGame()
            R.id.btn_online_join -> joinOnlineGame()
        }
    }

    private fun createOfflineGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    private fun createOnlineGame() {
        GameData.myId = "X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = Random.nextInt(1000..9999).toString()
            )
        )
        startGame()
    }

    private fun joinOnlineGame() {
        var gameId = binding.txtInputOnlineJoin.text.toString()
        if (gameId.isEmpty()) {
            binding.txtInputOnlineJoin.error = "Verifique o código e tente novamente"
            return
        }
        GameData.myId = "O"
        Firebase.firestore.collection("games").document(gameId).get().addOnSuccessListener {
            val model = it?.toObject(GameModel::class.java)
            if (model == null) {
                binding.txtInputOnlineJoin.error = "Entre com um código válido"
            } else {
                model.gameStatus = GameStatus.JOINED
                GameData.saveGameModel(model)
                startGame()
            }
        }
    }

    fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}