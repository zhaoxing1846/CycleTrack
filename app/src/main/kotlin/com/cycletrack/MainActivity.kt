package com.cycletrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cycletrack.data.CycleRepository
import com.cycletrack.ui.HomeScreen
import com.cycletrack.ui.theme.CycleTrackTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = CycleRepository(applicationContext)
        
        setContent {
            CycleTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(repository = repository, context = applicationContext)
                }
            }
        }
    }
}
