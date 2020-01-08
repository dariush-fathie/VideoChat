package ir.jin724.videochat.signup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import ir.jin724.videochat.R
import ir.jin724.videochat.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    val binding: ActivitySignupBinding by lazy {
        DataBindingUtil.setContentView<ActivitySignupBinding>(this, R.layout.activity_signup)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)

        binding.btnLogin.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                // todo login
                viewModel.signUp(firstName, lastName).observe(this) {
                    // todo save user
                }
            } else {
                Toast.makeText(this, "لطفا تمام گزینه ها را تکمیل کنید", Toast.LENGTH_LONG).show()
            }
        }
    }

}
