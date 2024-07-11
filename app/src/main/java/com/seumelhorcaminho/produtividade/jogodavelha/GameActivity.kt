package com.seumelhorcaminho.produtividade.jogodavelha

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.seumelhorcaminho.produtividade.jogodavelha.databinding.ActivityGameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameBinding
    private var gameModel: GameModel? = null
    private var contX = 0;
    private var contO = 0;
    private var contEmpate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel()

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)


        binding.buttonPlay.setOnClickListener {
            startGame()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }

        binding.buttonClearCont.setOnClickListener {
            binding.tvCont0.text = "0"
            binding.tvContX.text = "0"
            binding.tvContEmpate.text = "0"
            Toast.makeText(applicationContext, "Contadores zerados!", Toast.LENGTH_SHORT).show()
        }
//        buttonManager = ButtonManager(binding)
    }

    fun setUI() {
        gameModel?.apply {
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            binding.buttonPlay.visibility = View.VISIBLE
            binding.tvContEmpate.text = contEmpate.toString()

            binding.tvStatus.text =
                when (gameStatus) {
                    GameStatus.CREATED -> {
                        binding.buttonPlay.visibility = View.INVISIBLE
                        "ID :" + gameId
                    }

                    GameStatus.JOINED -> {
                        "Inicie o jogo"
                    }

                    GameStatus.INPROGRESS -> {
                        binding.buttonPlay.visibility = View.INVISIBLE
                        when (GameData.myId) {
                            currentPlayer -> "Sua vez"
                            else -> "Agora é o ${currentPlayer}"
                        }

                    }

                    GameStatus.FINISHED -> {
                        if (winner.isNotEmpty()) {
                            when (GameData.myId) {
                                winner -> "Você ganhou!"
                                else -> "Ganhador: ${winner}"
                            }

                        } else "Empate ${contEmpate++}"
                    }
                }
        }
    }


    fun startGame() {
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )
        }
    }

    fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }


    fun checkForWinner() {
        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),
        )

        gameModel?.apply {
            for (i in winningPos) {
                //012
                if (
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ) {
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]


                    if (winner == "X") {
                        contX++
                        binding.tvContX.text = contX.toString()
                    }
                    if (winner == "O") {
                        contO++
                        binding.tvCont0.text = contO.toString()
                    }
                    break
                }
            }

            if (filledPos.none() { it.isEmpty() }) {
                gameStatus = GameStatus.FINISHED
            }
            updateGameData(this)
        }


    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "Inicie o jogo", Toast.LENGTH_SHORT).show()
                return
            }

            if (gameId != "-1" && currentPlayer != GameData.myId) {
                Toast.makeText(applicationContext, "Não é sua vez", Toast.LENGTH_SHORT).show()
                return
            }
            if (gameId != "-1" && GameData.myId != currentPlayer) {
                Toast.makeText(applicationContext, "Não é sua vez", Toast.LENGTH_SHORT).show()
                return
            }

            val clickedPos = (v?.tag as String).toInt()
            if (filledPos[clickedPos].isEmpty()) {
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }

        }
    }
}

