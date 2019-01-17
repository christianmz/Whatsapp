package io.github.christianmz.whatsapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.github.christianmz.whatsapp.R
import io.github.christianmz.whatsapp.commons.mAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private val phoneNumber by lazy { et_phone_number_login.text.toString() }
    private val verificationCode by lazy { et_code_login.text.toString() }
    private var mVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_next_login.setOnClickListener {
            when {
                phoneNumber.isNotEmpty() -> verifyPhoneNumber()
                phoneNumber.isEmpty() -> longToast(R.string.add_phone_number)
                phoneNumber.isNotEmpty() && verificationCode.isNotEmpty() -> signInWithCredential(
                    PhoneAuthProvider.getCredential(
                        mVerificationId.toString(),
                        verificationCode
                    )
                )
                else -> longToast(R.string.verify_code)
            }
        }
    }

    private fun verifyPhoneNumber() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException?) {
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        longToast(R.string.invalid_credentials)
                    } else if (e is FirebaseTooManyRequestsException) {
                        longToast(R.string.too_many_requests)
                    }
                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String?) {
                    super.onCodeAutoRetrievalTimeOut(verificationId)
                    longToast(R.string.time_out)
                }

                override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                    mVerificationId = verificationId
                }
            }
        )
    }

    private fun signInWithCredential(phoneAuthCredential: PhoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity<MainActivity>()
                }
            }
    }
}
