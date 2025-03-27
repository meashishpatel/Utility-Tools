package com.example.utilitytools

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.utilitytools.databinding.ActivityThreeBinding

class ActivityThree : BaseActivity() {

    private lateinit var binding: ActivityThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.next3.setOnClickListener{
            Toast.makeText(this, "Reached end", Toast.LENGTH_SHORT).show()
        }
        binding.previous3.setOnClickListener{
            val intent = Intent(this, ActivityTwo::class.java)
            startActivity(intent)
        }
    }
}