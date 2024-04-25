package com.example.testapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testapp.R
import com.example.testapp.viewmodel.Achievement
import com.example.testapp.viewmodel.StepCounterViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AchievementsScreen(viewModel: StepCounterViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Achievements") })
        }
    ) {
        val achievements = viewModel.achievements.collectAsState().value.achievements
        AchievementList(achievements = achievements)
    }
}

@Composable
fun AchievementList(achievements: List<Achievement>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(achievements) { achievement ->
            AchievementCard(achievement)
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val rank = mapMilestoneToRank(achievement.milestone)
    val textColor = if (achievement.isAchieved) Color.Black else Color.Gray
    val imageRes = getImageResource(rank, achievement.isAchieved)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$rank rank image",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Rank",
                    style = MaterialTheme.typography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
                )
                Text(
                    text = "$rank: ${achievement.milestone} steps",
                    style = MaterialTheme.typography.h6.copy(color = textColor)
                )
                Text(
                    text = if (achievement.isAchieved) "Achieved" else "Not yet achieved",
                    style = MaterialTheme.typography.body1.copy(color = textColor)
                )
            }
        }
    }
}

fun getImageResource(rank: String, isAchieved: Boolean): Int {
    val rankLower = rank.lowercase()
    return if (isAchieved) {
        when (rankLower) {
            "bronze" -> R.drawable.bronze
            "silver" -> R.drawable.silver
            "gold" -> R.drawable.gold
            "platinum" -> R.drawable.platinum
            "diamond" -> R.drawable.diamond
            else -> R.drawable.norank
        }
    } else {
        when (rankLower) {
            "bronze" -> R.drawable.bronze_alt
            "silver" -> R.drawable.silver_alt
            "gold" -> R.drawable.gold_alt
            "platinum" -> R.drawable.platinum_alt
            "diamond" -> R.drawable.diamond_alt
            else -> R.drawable.norank
        }
    }
}

fun mapMilestoneToRank(milestone: Int): String = when(milestone) {
    5000 -> "Bronze"
    10000 -> "Silver"
    25000 -> "Gold"
    50000 -> "Platinum"
    100000 -> "Diamond"
    else -> "Unknown Rank"
}
