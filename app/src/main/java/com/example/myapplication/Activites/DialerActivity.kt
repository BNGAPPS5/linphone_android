package com.example.myapplication.Activites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bng.linphoneupdated.LinphoneApplication
import com.bng.linphoneupdated.LinphoneApplication.Companion.coreContext
import com.bng.linphoneupdated.LinphoneApplication.Companion.createConfig
import com.bng.linphoneupdated.core.CoreCallStateChangeListener
import com.example.myapplication.R
import java.util.regex.Pattern

class DialerActivity : AppCompatActivity(), OnClickListener, CoreCallStateChangeListener {
    var numEditText: AppCompatEditText? = null
    var btnzero: AppCompatTextView? = null
    var btnstar: AppCompatButton? = null
    var zeroplusLayout: LinearLayout? = null
    var btnclearnum: AppCompatImageView? = null
    var btnaddnum: AppCompatImageView? = null
    var btndialerCall: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.dialer_layout)
        createConfig(this)

        numEditText = findViewById(R.id.numedittext)
        btnclearnum = findViewById(R.id.clearnumber)
        btnaddnum = findViewById(R.id.savenumber)
        btndialerCall = findViewById(R.id.dialercallbtn)
        btnzero = findViewById(R.id.btnzero)
        btnstar = findViewById(R.id.btnStar)
        zeroplusLayout = findViewById(R.id.pluszero)
        btndialerCall?.setAlpha(0.4f)
        setOnBtnClickListner()
        registerUserForLinphone(this)
    }

    private fun setOnBtnClickListner() {
        findViewById<View>(R.id.btn1).setOnClickListener(this)
        findViewById<View>(R.id.btn2).setOnClickListener(this)
        findViewById<View>(R.id.btn3).setOnClickListener(this)
        findViewById<View>(R.id.btn4).setOnClickListener(this)
        findViewById<View>(R.id.btn5).setOnClickListener(this)
        findViewById<View>(R.id.btn6).setOnClickListener(this)
        findViewById<View>(R.id.btn7).setOnClickListener(this)
        findViewById<View>(R.id.btn8).setOnClickListener(this)
        findViewById<View>(R.id.btn9).setOnClickListener(this)
        findViewById<View>(R.id.btnStar).setOnClickListener(this)
        findViewById<View>(R.id.pluszero).setOnClickListener(this)
        findViewById<View>(R.id.btnzero).setOnClickListener(this)
        findViewById<View>(R.id.btnhash).setOnClickListener(this)
        findViewById<View>(R.id.dialercalldecline).setOnClickListener(this)
        zeroplusLayout!!.setOnClickListener(this)
        btnaddnum!!.setOnClickListener(this)
        btnclearnum!!.setOnClickListener(this)
        btnclearnum!!.setOnLongClickListener { v: View? ->
            numEditText!!.setText("")
            btnclearnum!!.visibility = View.INVISIBLE
            btnaddnum!!.visibility = View.INVISIBLE
            disableCall()
            true
        }
        btnzero!!.setOnLongClickListener { v: View? ->
            var editNumber = numEditText!!.text.toString()
            editNumber = "$editNumber+"
            numEditText!!.setText(editNumber)
            btnclearnum!!.visibility = View.VISIBLE
            true
        }
        btnstar!!.setOnLongClickListener { v: View? ->
            var editNumber = numEditText!!.text.toString()
            editNumber = "$editNumber*"
            numEditText!!.setText(editNumber)
            btnclearnum!!.visibility = View.VISIBLE
            true
        }
        zeroplusLayout!!.setOnLongClickListener { v: View? ->
            var editNumber = numEditText!!.text.toString()
            editNumber = "$editNumber+"
            numEditText!!.setText(editNumber)
            btnclearnum!!.visibility = View.VISIBLE
            true
        }
        /*  val address = "sip:" + appPrefs.getMagicCallShortCode()
              .toString() + formatted_code.toString() + number.toString() + "@" + appPrefs.getCallingServerIp()
              .toString() +
                  ":" + appPrefs.getCallingServerPort()*/


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn1 -> {
                numEditText!!.append("1")
                checkNumberLength()
            }
            R.id.btn2 -> {
                numEditText!!.append("2")
                checkNumberLength()
            }
            R.id.btn3 -> {
                numEditText!!.append("3")
                checkNumberLength()
            }
            R.id.btn4 -> {
                numEditText!!.append("4")
                checkNumberLength()
            }
            R.id.btn5 -> {
                numEditText!!.append("5")
                checkNumberLength()
            }
            R.id.btn6 -> {
                numEditText!!.append("6")
                checkNumberLength()
            }
            R.id.btn7 -> {
                numEditText!!.append("7")
                checkNumberLength()
            }
            R.id.btn8 -> {
                numEditText!!.append("8")
                checkNumberLength()
            }
            R.id.btn9 -> {
                numEditText!!.append("9")
                checkNumberLength()
            }
            R.id.pluszero -> {
                numEditText!!.append("0")
                checkNumberLength()
            }
            R.id.btnzero -> {
                numEditText!!.append("0")
                checkNumberLength()
            }
            R.id.btnhash -> {
                var editNumber = numEditText!!.text.toString()
                editNumber = "$editNumber#"
                numEditText!!.setText(editNumber)
                checkNumberLength()
            }
            R.id.clearnumber -> if (numEditText!!.text.toString()
                    .equals("", ignoreCase = true) || !numEditText!!.text.toString().isEmpty()
            ) {
                var str = numEditText!!.text.toString()
                if (str.length > 0) str = str.substring(0, str.length - 1)
                // Now set this Text to your edit text
                numEditText!!.setText(str)
                checkNumberLength()
            }
            R.id.savenumber -> addContactIntent()
            R.id.dialercallbtn -> if (!numEditText!!.text.toString().isEmpty()) {

              //  coreContext.terminateAllCalls()
             //   coreContext.notificationsManager.destroy()
                var number = numEditText!!.text.toString()
                if (number.length > 3) {
                    number = getFormattedNumberForCall(number)!!
                }

                var contactName = ""
                //"sip:56003919711667661@52.37.177.101:1183"
                val address = "sip:56503919711667661" + "@52.37.177.101:1183"
                coreContext.setUserAgent("{ccode=+91, user=ANDROID}")
                LinphoneApplication.startCall(address, 2)

                /* address =
                     "sip:" + sharedPrefs.getBgShortCode() + dummy + "@" + sharedPrefs.getCallingServerIp() + ":" + sharedPrefs.getCallingServerPort()
                 val audioURL = selectedItemBackground?.audioUrl
                 val fileName = audioURL?.substring(audioURL.lastIndexOf('/') + 1)
              */
                //coreContext.setUserAgent(userAgent)
                // checkAndInitiateCall(numEditText!!.text.toString(), contactName)
                //  }
            }

            R.id.dialercalldecline -> {
                coreContext.hangUp()
            }
        }
    }

    /*0(MSISDN) - must be assumed as local.
    00880(MSISDN)
    +880(MSISDN)
    (MSISDN)  - must be assumed as local.
    +(COUNTRY-CODE)(MSISDN)
    00(COUNTRY-CODE)(MSISDN)
     */
    fun getFormattedNumberForCall(number: String?): String? {
        var formattedNumber = ""
        if (number != null) {
            formattedNumber = number.replace("\\s+".toRegex(), "")
            if (formattedNumber.startsWith("00")) {
                // strip 00 only
                formattedNumber = formattedNumber.substring(2)
            } else if (formattedNumber.startsWith("0")) {
                // remove 0 and add country code
                formattedNumber = getUserCountryCode() + formattedNumber.substring(1)
            } else if (formattedNumber.startsWith("+")) {
                // trim + only
                formattedNumber = formattedNumber.substring(1)
                if (formattedNumber.contains("-")) {
                    formattedNumber = formattedNumber.replace("[\\s\\-()]".toRegex(), "")
                }
            } else if (formattedNumber.startsWith("-")) {
                formattedNumber.replace("[\\s\\-()]".toRegex(), "")
            } else {
                // add 880
                //TODO Nisha
                formattedNumber = getUserCountryCode() + formattedNumber
            }
        }
        return formattedNumber
    }

    // Remove + and return 880 only
    private fun getUserCountryCode(): String {
        var code: String = "91"
        if (code != null && !code.equals("", ignoreCase = true)) {
        } else {
            code = "91"
        }
        return if (code != null && code.length > 1 && code.contains("+")) {
            code.substring(1)
        } else {
            code
        }
    }


    fun formattedNumber(number: String?): String? {
        var number = number
        number = number!!.replace("\\s+".toRegex(), "")
        if (number != null && !number.trim { it <= ' ' }.isEmpty()) {
            val pt = Pattern.compile("[^+0-9]")
            val match = pt.matcher(number)
            while (match.find()) {
                val unformattedNo = number
                number = number!!.replace("[^+0-9]".toRegex(), "")
            }
        }
        return number
    }


    private fun checkNumberLength() {
        val length = numEditText!!.text?.length
        if (length != null) {
            if (length > 0) {
                btnclearnum!!.visibility = View.VISIBLE
            } else {
                btnclearnum!!.visibility = View.INVISIBLE
            }
        }
        if (length!! >= 5) {
            btnaddnum!!.visibility = View.VISIBLE
            enableCall()
        } else {
            btnaddnum!!.visibility = View.INVISIBLE
            disableCall()
        }
    }

    private fun enableCall() {
        btndialerCall!!.setOnClickListener(this)
        btndialerCall!!.alpha = 1f
    }

    private fun disableCall() {
        btndialerCall!!.setOnClickListener(null)
        btndialerCall!!.alpha = 0.4f
    }

    fun addContactIntent() {
        //   Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        // Creates a new Intent to insert a contact
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        // Sets the MIME type to match the Contacts Provider
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, numEditText!!.text)
        startActivity(intent)
    }

    fun checkAndInitiateCall(mobileNumber: String?, name: String?) {
        var mobileNumber = mobileNumber
        if (mobileNumber != null) {
            val callo_calling_intent =
                Intent(this, MainActivity::class.java)
            if (name != null) {
                callo_calling_intent.putExtra("name", name)
            } else {
                callo_calling_intent.putExtra("name", mobileNumber)
            }
            callo_calling_intent.putExtra("number", mobileNumber)
            callo_calling_intent.putExtra("displaynumber", mobileNumber)

            startActivity(callo_calling_intent)
            finish()
        }
    }

    /*
    public void callStateChanged(String state, final String message) {
        logManager.logsForDebugging(TAG, "Current Call State Changed : " + state.toString() + " message : " + message);
        // refreshCallStateIcons(state);
        CalloApp.getFirebaseAnalytics().logEvent(NewAnalyticsConstants.CALL_STATE_CHANGE + "__" + state.toString(), null);
        Branch.getInstance().userCompletedAction(NewAnalyticsConstants.CALL_STATE_CHANGE + "__" + state.toString());

        switch (state) {
            case CallOCallOutgoingInit:
                startInviteTimer();
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                break;

            case CallOCallOutgoingRinging:
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                break;

            case CallOCallConnected:
                runOnUiThread(() -> {
                    findViewById(R.id.trans_layer).setVisibility(View.GONE);
                    if (pager != null) {
                        logManager.logsForDebugging(TAG, "setselectedItem()");

                        pager.setAdapter(mCallingAdapter);
                        pager.setCurrentItem(HomeDashBoardActivity.getInstance().mBottomAdapterPosition);
                    }
                    stopInviteTimer();
                    enableDisableControls(true);
                    registerCallDurationTimer();
                });
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                break;

            case CalloCallStreamsReunning:

                isPaused = false;
                logManager.logsForDebugging(TAG, "streams running");
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                break;

            case CallOCallPausing:
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                isPaused = true;
                hold_btn.setImageResource(R.drawable.call_hold_sel);
                updateCallStatusMessage(getString(R.string.on_hold));

                break;

            case CallOCallPaused:
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                isPause = false;
                hold_btn.setImageResource(R.drawable.call_hold_sel);
                break;

            case CallOCallResuming:
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
                isPause = true;
                updateCallStatusMessage("");


                //    CalloCallManager.getInstance().enableSpeaker(isSpeakerEnabled);
                hold_btn.setImageResource(R.drawable.call_hold);
                break;

            case CallOCallError:
                logManager.logsForDebugging(TAG, "finishing call : callocallerror");
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                updateCallStatusMessage(message);
                updateCallHistory();
                finishCall(false);
                if (message.contains("I/O error")) {
                    CalloApp.getFirebaseAnalytics().logEvent(NewAnalyticsConstants.CALL_ERROR, null);
                    Branch.getInstance().userCompletedAction(NewAnalyticsConstants.CALL_ERROR);

                    logManager.logsForDebugging(TAG, "call disconnected due to-" + message);
                  */
    /*  CallErrorAsyncTask callErrorAsyncTask = new CallErrorAsyncTask(this, message);
                    callErrorAsyncTask.execute();*/
    /*

                }

                break;

            case CallOCallOutgoingEarlyMedia:
                break;

            case CallOCallEnd:
                logManager.logsForDebugging(TAG, "finishing call : callocallend");
                stopForegroundService();
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                updateCallHistory();
                updateCallStatusMessage(message);
                finishCall(false);
                break;
        }
    }
*/
    fun callIdle(message: String) {
    }

    override fun callIdle(message: String, protocolCode: Int) {
    }

    override fun callOutgoingInit(message: String) {

    }

    override fun callOutgoingRinging(message: String) {

    }

    override fun callOutgoingEarlyMedia(message: String) {
    }


    override fun callConnected(message: String) {

    }


    override fun callPausing(message: String) {

    }

    override fun callPaused(message: String) {

    }

    override fun callResuming(message: String) {

    }

    override fun callStreamsRunning(message: String) {

    }

    override fun callError(message: String, protocolCode: Int) {

    }


    override fun callReleased(message: String, protocolCode: Int) {
        coreContext.hangUp()

    }

    override fun callEnd(message: String, protocolCode: Int) {
        coreContext.hangUp()
    }

    override fun onMessageReceived(message: String) {

    }

    fun registerUserForLinphone(context: Context) {
        val userId: String = "919898989898"
        LinphoneApplication.ensureCoreExists(context, false, null, false)
        val ip = "sip.linphone.org"//"192.168.1.63"
      /*  if (coreContext != null) {
            coreContext.stopCore()
        }*/
        LinphoneApplication.startCore("", userId, ip, 2)
        // DebugLogManager.getInstance().logsForDebugging(TAG, "registerUser() called")
    }

}
