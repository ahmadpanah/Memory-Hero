package ir.shariaty.memoryhero

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LeaderboardManager {
    private val database = FirebaseDatabase.getInstance()
    private val scoresRef = database.getReference("scores")

    fun submitScore(score: Score, callback: (Boolean) -> Unit) {
        val scoreId = scoresRef.push().key ?: return
        scoresRef.child(scoreId).setValue(score).addOnSuccessListener {
            callback(true)
        }
            .addOnFailureListener{
               callback(false)
            }
    fun getTopScore(limit: Int = 100,callback: (LeaderboardResult) -> Unit){
        scoresRef.orderByChild("score")
            .limitToLast(limit)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scores = snapshot.children.mapNotNull {
                        it.getValue(Score::class.java)
                    }.sortedByDescending { it.score }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    }
}