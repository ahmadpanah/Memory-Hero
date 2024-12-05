package ir.shariaty.memoryhero

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import ir.shariaty.memoryhero.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.media.MediaPlayer
import android.os.Handler
import android.provider.Settings.Panel
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlin.random.Random




class MainActivity : AppCompatActivity(),OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private var score = 0
    private var result : String = ""
    private var userAnswer : String = ""

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var panel1: AppCompatButton
    private lateinit var panel2: AppCompatButton
    private lateinit var panel3: AppCompatButton
    private lateinit var panel4: AppCompatButton

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        panel1 = findViewById(R.id.panel1)
        panel2 = findViewById(R.id.panel2)
        panel3 = findViewById(R.id.panel3)
        panel4= findViewById(R.id.panel4)
        mediaPlayer = MediaPlayer.create(this, R.raw.mediading)


        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // init Views
        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
        }
    }


    private fun disableButtons(){
        binding.root.forEach { view ->
            if (view is AppCompatButton)
            {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons() {
        binding.root.forEach { view ->
            if (view is AppCompatButton) {
                view.isEnabled = true
                mediaPlayer?.start()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun submitScore(score: Int, timeInMillis: Long) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)

        builder.setTitle("New Score!")
            .setMessage("Enter your name:")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val playerName = input.text.toString()
                if (playerName.isNotEmpty()) {

                    val score = Score(
                        playerName = playerName,
                        score = score,
                        timeInMillis = timeInMillis,
                        userId = Firebase.getInstance().currentUser?.uid ?: ""
                    )

                    LeaderboardManager().submitScore(score) { success ->
                        if (success) {
                            startActivity(Intent(this, OnlineLeaderboardActivity::class.java))
                        } else {
                            Toast.makeText(this, "Failed to submit score", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun startGame(){
        result = ""
        userAnswer = ""
        disableButtons()
        lifecycleScope.launch {
            val round = (3 .. 5).random()
            repeat(round)
            {
                delay(400)
                val randomPanel = (1 .. 4).random()
                result += randomPanel
                val panel = when(randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }
                val drawbleYellow = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_yellow)
                val drawableDefault = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_state)
                panel.background = drawbleYellow
                delay(1000)
                panel.background = drawableDefault
            }
            enableButtons()
        }
    }
    private fun loseAnimation(){
        binding.apply {
            score = 0
            tvScore.text = "0"
            disableButtons()
            val drawbleLose = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_lose)
            val drawableDefault = ActivityCompat.getDrawable(this@MainActivity,R.drawable.btn_state)
            lifecycleScope.launch {
                binding.root.forEach { view ->
                    if (view is AppCompatButton) {
                        view.background = drawbleLose
                        delay(300)
                        view.background = drawableDefault
                    }
                }
                delay(1000)

                startGame()
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            userAnswer += when(it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""
            }
            if (userAnswer == result){
                Toast.makeText(this@MainActivity, "W I N! 👌🍕",Toast.LENGTH_SHORT).show()
                score++
                binding.tvScore.text = score.toString()
                startGame()
            }
            else if  (userAnswer.length >= result.length)  {
                val currentTimeMillis = System.currentTimeMillis()
                submitScore(score, currentTimeMillis)
                loseAnimation()

            }
        }
    }

}

