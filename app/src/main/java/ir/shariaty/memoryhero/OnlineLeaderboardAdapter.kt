package ir.shariaty.memoryhero

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.shariaty.memoryhero.databinding.ItemLeaderboardBinding

class OnlineLeaderboardAdapter :
    ListAdapter<Score, OnlineLeaderboardAdapter.ViewHolder>(ScoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class ViewHolder(
        private val binding: ItemLeaderboardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(score: Score, position: Int) {
            binding.apply {
                rankTextView.text = "#$position"
                nameTextView.text = score.playerName
                scoreTextView.text = "${score.score}"
                timeTextView.text = formatTime(score.timeInMillis)

                // Highlight top 3 positions
                val backgroundColor = when (position) {
                    1 -> R.color.gold
                    2 -> R.color.silver
                    3 -> R.color.bronze
                    else -> R.color.transparent
                }
                root.setBackgroundResource(backgroundColor)
            }
        }

        private fun formatTime(timeInMillis: Long): String {
            val seconds = (timeInMillis / 1000) % 60
            val minutes = (timeInMillis / (1000 * 60)) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    class ScoreDiffCallback : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem.timestamp == newItem.timestamp &&
                    oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem == newItem
        }
    }
}