package com.mobileforce.hometeach.ui.signin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mobileforce.hometeach.R
import com.mobileforce.hometeach.data.sources.remote.Params
import com.mobileforce.hometeach.databinding.ActivityLoginBinding
import com.mobileforce.hometeach.ui.BottomNavigationActivity
import com.mobileforce.hometeach.ui.ExploreActivity
import com.mobileforce.hometeach.utils.ApiError
import com.mobileforce.hometeach.utils.Result
import com.mobileforce.hometeach.utils.snack
import kotlinx.android.synthetic.main.recover_email_layout.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SignInViewModel by viewModel()

    private var emailValid = false
    private var passwordValid = false

    private lateinit var emailWatcher: TextWatcher
    private lateinit var passwordWatcher: TextWatcher

    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Login in progress...")
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signIn.setOnClickListener {
            triggerSignInProcess()
        }

        binding.textRegisterNow.setOnClickListener {
            navigateToSignUp()
        }
        binding.forgotPassword.setOnClickListener {
            showEmailDialog()
        }

        emailWatcher = object : TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                emailValid = Patterns.EMAIL_ADDRESS.matcher(input)
                        .matches() && input!!.indexOf("@") < input!!.length
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                val passwordPattern = "^(?=.*?[#?!@\$%^&*-]).{6,}\$"
                passwordValid = Pattern.matches(passwordPattern, input)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        binding.textEditEmail.addTextChangedListener(emailWatcher)
        binding.textEditPassword.addTextChangedListener(passwordWatcher)

        observeSignIn()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

    private fun triggerSignInProcess() {
        if (emailValid && passwordValid) {
            val user = Params.SignIn(
                email = binding.textEditEmail.text.toString(),
                password = binding.textEditPassword.text.toString()
            )
            viewModel.signIn(user)
        } else if (!emailValid) {
            binding.textInputEmailSignin.isHelperTextEnabled = true
            binding.textInputEmailSignin.error = "Input a valid email address"
        } else if (!passwordValid) {
            binding.textInputPasswordField.isHelperTextEnabled = true
            binding.textInputPasswordField.error = "Input a valid password"
        }
    }


    private fun observeSignIn() {

        viewModel.signIn.observe(this, Observer { result ->

            when (result) {
                is Result.Loading -> {
                    progressDialog.show()
                }

                is Result.Success -> {
                    progressDialog.hide()
                    binding.signInLayout.snack(message = "Login Successful",
                        actionCallBack = {
                            navigateToDashBoard()
                        })

                    navigateToDashBoard()

                }

                is Result.Error -> {
                    progressDialog.hide()
                    val message = ApiError(result.exception).message
                    binding.signInLayout.snack(message = message, length = Snackbar.LENGTH_LONG)
                }
            }
        })
    }


    private fun navigateToDashBoard() {
        startActivity(Intent(this, BottomNavigationActivity::class.java))
        finish()
    }


    private fun navigateToSignUp() {
        startActivity(Intent(this, ExploreActivity::class.java))
    }


    private fun showEmailDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.recover_email_layout, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.show()
        val submit = mDialogView.findViewById<AppCompatButton>(R.id.submit)

        submit.setOnClickListener {

            val email = mDialogView.email.text.toString()
            val data = Params.PasswordReset(email)
            viewModel.resetPassword(data)

            if (viewModel.success) {
                mAlertDialog.hide()
                binding.signInLayout.snack(message = "A PASSSWORD RESET LINK HAS BEEN SENT TO YOUR MAIL")

            } else {
                mAlertDialog.hide()
                binding.signInLayout.snack(message = "SORRY, THIS EMAIL IS NOT REGISTERED OR YOU HAVE A POOR INTERNET CONNECTION")

            }
        }
    }

}