package ir.shariaty.memoryhero

data class Score (
    val playerName: String ="",
    val score: Int = 0,
    val timeInMillis: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)

sealed class LeaderboardResult{
    data class Success(val score: List<Score>) : LeaderboardResult()
    data class Error(val message: String) : LeaderboardResult()
}
