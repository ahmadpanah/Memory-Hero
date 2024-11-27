package ir.shariaty.memoryhero

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.Distribution.BucketOptions.Linear
import ir.shariaty.memoryhero.databinding.ActivityOnlineLeaderboardBinding

class OnlineLeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnlineLeaderboardBinding
    private lateinit var leaderboardManager: LeaderboardManager
    private lateinit var adapter: OnlineLeaderboardAdapter
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupRefreshLayout()
        loadLeaderboard()

        // Subscribe to real-time updates
        leaderboardManager.subscribeToScoreUpdates { result ->
            handleLeaderboardResult(result)
        }
    }

    private fun setupRecyclerView() {
        adapter = OnlineLeaderboardAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OnlineLeaderboardActivity)
            adapter = this@OnlineLeaderboardActivity.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadLeaderboard()
        }
    }

    private fun loadLeaderboard() {
        if (isLoading) return
        isLoading = true
        binding.swipeRefreshLayout.isRefreshing = true

        leaderboardManager.getTopScore { result ->
            handleLeaderboardResult(result)
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun handleLeaderboardResult(result: LeaderboardResult) {
        when (result) {
            is LeaderboardResult.Success -> {
                adapter.submitList(result.score)
                binding.emptyView.isVisible = result.score.isEmpty()
            }
            is LeaderboardResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}