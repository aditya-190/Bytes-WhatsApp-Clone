package com.bytes.messenger.welcome

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bytes.messenger.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


//TODO: (Phone) - Check for Internet, Design Phone Number, Login More, Time to Go Back to Enter New Number (Change Number)
//TODO: (OTP) - Check for Internet, OTP Code Max 6 Digits and Design It, Wrong Number, OTP More, Resend SMS, Timer.

class PhoneLoginActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var nextButton: Button
    private lateinit var phoneNumber: EditText
    private lateinit var otpEntered: EditText
    private lateinit var heading: TextView
    private lateinit var phoneSubHeading: TextView
    private lateinit var phoneCountryCode: TextView
    private lateinit var otpSubHeading: TextView
    private lateinit var otpNumber: TextView
    private lateinit var otpWrongNumber: TextView
    private lateinit var otpSubSubHeading: TextView
    private lateinit var otpResendText: TextView
    private lateinit var otpResendTimer: TextView
    private lateinit var phoneCarrierCharges: TextView
    private lateinit var verificationID: String
    private lateinit var phoneSpinner: Spinner
    private lateinit var otpResendImage: ImageView
    private lateinit var progressBar: pl.droidsonroids.gif.GifImageView
    private val countryNames = arrayOf(
        "Afghanistan", "Albania",
        "Algeria", "Andorra", "Angola", "Antarctica", "Argentina",
        "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
        "Bahrain", "Bangladesh", "Belarus", "Belgium", "Belize", "Benin",
        "Bhutan", "Bolivia", "Bosnia And Herzegovina", "Botswana",
        "Brazil", "Brunei Darussalam", "Bulgaria", "Burkina Faso",
        "Myanmar", "Burundi", "Cambodia", "Cameroon", "Canada",
        "Cape Verde", "Central African Republic", "Chad", "Chile", "China",
        "Christmas Island", "Cocos (keeling) Islands", "Colombia",
        "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia",
        "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti",
        "Timor-leste", "Ecuador", "Egypt", "El Salvador",
        "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
        "Falkland Islands (malvinas)", "Faroe Islands", "Fiji", "Finland",
        "France", "French Polynesia", "Gabon", "Gambia", "Georgia",
        "Germany", "Ghana", "Gibraltar", "Greece", "Greenland",
        "Guatemala", "Guinea", "Guinea-bissau", "Guyana", "Haiti",
        "Honduras", "Hong Kong", "Hungary", "India", "Indonesia", "Iran",
        "Iraq", "Ireland", "Isle Of Man", "Israel", "Italy", "Ivory Coast",
        "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati",
        "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho",
        "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
        "Macao", "Macedonia", "Madagascar", "Malawi", "Malaysia",
        "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania",
        "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
        "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique",
        "Namibia", "Nauru", "Nepal", "Netherlands", "New Caledonia",
        "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Korea",
        "Norway", "Oman", "Pakistan", "Palau", "Panama",
        "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn",
        "Poland", "Portugal", "Puerto Rico", "Qatar", "Romania",
        "Russian Federation", "Rwanda", "Saint Barthélemy", "Samoa",
        "San Marino", "Sao Tome And Principe", "Saudi Arabia", "Senegal",
        "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia",
        "Slovenia", "Solomon Islands", "Somalia", "South Africa",
        "Korea, Republic Of", "Spain", "Sri Lanka", "Saint Helena",
        "Saint Pierre And Miquelon", "Sudan", "Suriname", "Swaziland",
        "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan",
        "Tajikistan", "Tanzania", "Thailand", "Togo", "Tokelau", "Tonga",
        "Tunisia", "Turkey", "Turkmenistan", "Tuvalu",
        "United Arab Emirates", "Uganda", "United Kingdom", "Ukraine",
        "Uruguay", "United States", "Uzbekistan", "Vanuatu",
        "Holy See (vatican City State)", "Venezuela", "Viet Nam",
        "Wallis And Futuna", "Yemen", "Zambia", "Zimbabwe"
    )
    private val countryCodes = arrayOf(
        "93", "355", "213",
        "376", "244", "672", "54", "374", "297", "61", "43", "994", "973",
        "880", "375", "32", "501", "229", "975", "591", "387", "267", "55",
        "673", "359", "226", "95", "257", "855", "237", "1", "238", "236",
        "235", "56", "86", "61", "61", "57", "269", "242", "682", "506",
        "385", "53", "357", "420", "45", "253", "670", "593", "20", "503",
        "240", "291", "372", "251", "500", "298", "679", "358", "33",
        "689", "241", "220", "995", "49", "233", "350", "30", "299", "502",
        "224", "245", "592", "509", "504", "852", "36", "91", "62", "98",
        "964", "353", "44", "972", "39", "225", "1876", "81", "962", "7",
        "254", "686", "965", "996", "856", "371", "961", "266", "231",
        "218", "423", "370", "352", "853", "389", "261", "265", "60",
        "960", "223", "356", "692", "222", "230", "262", "52", "691",
        "373", "377", "976", "382", "212", "258", "264", "674", "977",
        "31", "687", "64", "505", "227", "234", "683", "850", "47", "968",
        "92", "680", "507", "675", "595", "51", "63", "870", "48", "351",
        "1", "974", "40", "7", "250", "590", "685", "378", "239", "966",
        "221", "381", "248", "232", "65", "421", "386", "677", "252", "27",
        "82", "34", "94", "290", "508", "249", "597", "268", "46", "41",
        "963", "886", "992", "255", "66", "228", "690", "676", "216", "90",
        "993", "688", "971", "256", "44", "380", "598", "1", "998", "678",
        "39", "58", "84", "681", "967", "260", "263"
    )
    private lateinit var code: String
    private lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        progressBar = findViewById(R.id.progressBar)
        nextButton = findViewById(R.id.next_button)
        heading = findViewById(R.id.login_heading)

        phoneSubHeading = findViewById(R.id.login_sub_heading)
        phoneSpinner = findViewById(R.id.country_selector)
        phoneCountryCode = findViewById(R.id.country_code)
        phoneNumber = findViewById(R.id.phone_number)
        phoneCarrierCharges = findViewById(R.id.carrier_charges)

        otpSubHeading = findViewById(R.id.otp_login_sub_heading)
        otpNumber = findViewById(R.id.otp_number)
        otpSubSubHeading = findViewById(R.id.enter_otp_text)
        otpWrongNumber = findViewById(R.id.wrong_number)
        otpEntered = findViewById(R.id.otp_code)
        otpResendImage = findViewById(R.id.resend_code_image)
        otpResendText = findViewById(R.id.resend_code_text)
        otpResendTimer = findViewById(R.id.resend_otp_timer)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countryNames).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        phoneSpinner.also {
            it.textAlignment = View.TEXT_ALIGNMENT_CENTER
            it.isScrollbarFadingEnabled = true
            it.scrollBarFadeDuration = 10
            it.onItemSelectedListener = this
            it.adapter = adapter
            it.setSelection(79)
        }
    }

    private fun clickListeners() {
        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (android.util.Patterns.PHONE.matcher(s).matches())
                    nextButton.isEnabled = true
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        nextButton.setOnClickListener {
            if (nextButton.text == getString(R.string.button_confirm)) {
                if (otpEntered.text.toString() != "") {
                    progressBar.visibility = View.VISIBLE
                    signIn()
                } else
                    Snackbar.make(findViewById(android.R.id.content),
                        "Please enter the OTP.",
                        Snackbar.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                sendOTP()
            }
        }
    }

    private fun changeLayout(to: String) {
        if (to == getString(R.string.button_confirm)) {
            nextButton.text = getString(R.string.button_confirm)
            heading.text = String.format("Verify (%s) %s", code, number)
            otpNumber.text = String.format("Verify (%s) %s. ", code, number)

            phoneSubHeading.visibility = View.GONE
            phoneSpinner.visibility = View.GONE
            phoneCountryCode.visibility = View.GONE
            phoneNumber.visibility = View.GONE
            phoneCarrierCharges.visibility = View.GONE

            otpSubHeading.visibility = View.VISIBLE
            otpNumber.visibility = View.VISIBLE
            otpSubSubHeading.visibility = View.VISIBLE
            otpWrongNumber.visibility = View.VISIBLE
            otpEntered.visibility = View.VISIBLE
            otpResendImage.visibility = View.VISIBLE
            otpResendText.visibility = View.VISIBLE
            otpResendTimer.visibility = View.VISIBLE

        } else {
            nextButton.text = getString(R.string.button_next)
            heading.text = getString(R.string.heading_phone_number)

            phoneSubHeading.visibility = View.VISIBLE
            phoneSpinner.visibility = View.VISIBLE
            phoneCountryCode.visibility = View.VISIBLE
            phoneNumber.visibility = View.VISIBLE
            phoneCarrierCharges.visibility = View.VISIBLE

            otpSubHeading.visibility = View.GONE
            otpNumber.visibility = View.GONE
            otpSubSubHeading.visibility = View.GONE
            otpWrongNumber.visibility = View.GONE
            otpEntered.visibility = View.GONE
            otpResendImage.visibility = View.GONE
            otpResendText.visibility = View.GONE
            otpResendTimer.visibility = View.GONE
        }
    }

    private fun sendOTP() {
        code = phoneCountryCode.text.toString()
        number = phoneNumber.text.toString()

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(String.format("%s%s", code, number))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                        changeLayout(getString(R.string.button_confirm))
                        progressBar.visibility = View.INVISIBLE
                    }

                    override fun onVerificationFailed(error: FirebaseException) {
                        progressBar.visibility = View.INVISIBLE

                        when (error) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Snackbar.make(findViewById(android.R.id.content),
                                    "Invalid phone number.",
                                    Snackbar.LENGTH_SHORT).show()
                            }
                            is FirebaseTooManyRequestsException -> {
                                Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                                    Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCodeSent(
                        code: String,
                        token: PhoneAuthProvider.ForceResendingToken,
                    ) {
                        super.onCodeSent(code, token)
                        changeLayout(getString(R.string.button_confirm))
                        progressBar.visibility = View.INVISIBLE
                        verificationID = code
                    }
                }).build()
        )
    }

    private fun signIn() {
        val credentials: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationID, otpEntered.text.toString())
        auth.signInWithCredential(credentials).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.INVISIBLE
                val user: FirebaseUser? = task.result?.user
                if (user != null) {
                    startActivity(Intent(this@PhoneLoginActivity,
                        UserInfoActivity::class.java))
                    finish()
                }
            } else {
                progressBar.visibility = View.INVISIBLE
                Snackbar.make(findViewById(android.R.id.content), "Incorrect OTP entered.",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        parent.getItemAtPosition(pos)
        phoneCountryCode.text = String.format("+%s", countryCodes[pos])
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    override fun onBackPressed() {
        if (nextButton.text == getString(R.string.button_next))
            super.onBackPressed()
        else
            changeLayout(getString(R.string.button_next))
    }
}