package ir.shariaty.memoryhero

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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import ir.shariaty.memoryhero.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),OnClickListener {
    private lateinit var binding : ActivityMainBinding
    private var score = 0
    private var result : String = ""
    private var userAnswer : String = ""

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onClick(view: View?) {
        TODO("Not yet implemented")
    }


    private fun disableButtons(){
        binding.root.forEach { view ->
            if (view is AppCompatButton)
            {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons(){
        binding.root.forEach { view ->
            if (view is AppCompatButton)
            {
                view.isEnabled = true
            }
        }
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
                var panel = when(randomPanel) {
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
}