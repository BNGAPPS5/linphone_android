/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bng.linphoneupdated.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bng.linphoneupdated.LinphoneApplication.Companion.corePreferences
import com.bng.linphoneupdated.R
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.*
import org.linphone.core.tools.Log

/**
 * This file contains all the data binding necessary for the app
 */

fun View.hideKeyboard() {
    try {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    } catch (e: Exception) {}
}

@BindingAdapter("android:src")
fun ImageView.setSourceImageResource(resource: Int) {
    this.setImageResource(resource)
}

@BindingAdapter("android:contentDescription")
fun ImageView.setContentDescription(resource: Int) {
    if (resource == 0) {
        Log.w("Can't set content description with resource id 0")
        return
    }
    this.contentDescription = context.getString(resource)
}

@BindingAdapter("android:textStyle")
fun TextView.setTypeface(typeface: Int) {
    this.setTypeface(null, typeface)
}

@BindingAdapter("android:layout_size")
fun View.setLayoutSize(dimension: Float) {
    if (dimension == 0f) return
    this.layoutParams.height = dimension.toInt()
    this.layoutParams.width = dimension.toInt()
}

@BindingAdapter("backgroundImage")
fun LinearLayout.setBackgroundImage(resource: Int) {
    this.setBackgroundResource(resource)
}

@Suppress("DEPRECATION")
@BindingAdapter("style")
fun TextView.setStyle(resource: Int) {
    this.setTextAppearance(context, resource)
}

@BindingAdapter("android:layout_marginLeft")
fun setLeftMargin(view: View, margin: Float) {
    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
    layoutParams.leftMargin = margin.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_marginRight")
fun setRightMargin(view: View, margin: Float) {
    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
    layoutParams.rightMargin = margin.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_alignLeft")
fun setLayoutLeftAlign(view: View, oldTargetId: Int, newTargetId: Int) {
    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
    if (oldTargetId != 0) layoutParams.removeRule(RelativeLayout.ALIGN_LEFT)
    if (newTargetId != 0) layoutParams.addRule(RelativeLayout.ALIGN_LEFT, newTargetId)
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_alignRight")
fun setLayoutRightAlign(view: View, oldTargetId: Int, newTargetId: Int) {
    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
    if (oldTargetId != 0) layoutParams.removeRule(RelativeLayout.ALIGN_RIGHT)
    if (newTargetId != 0) layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, newTargetId)
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_toLeftOf")
fun setLayoutToLeftOf(view: View, oldTargetId: Int, newTargetId: Int) {
    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
    if (oldTargetId != 0) layoutParams.removeRule(RelativeLayout.LEFT_OF)
    if (newTargetId != 0) layoutParams.addRule(RelativeLayout.LEFT_OF, newTargetId)
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_gravity")
fun setLayoutGravity(view: View, gravity: Int) {
    val layoutParams = view.layoutParams as LinearLayout.LayoutParams
    layoutParams.gravity = gravity
    view.layoutParams = layoutParams
}

@BindingAdapter("layout_constraintGuide_percent")
fun setLayoutConstraintGuidePercent(guideline: Guideline, percent: Float) {
    val params = guideline.layoutParams as ConstraintLayout.LayoutParams
    params.guidePercent = percent
    guideline.layoutParams = params
}

@BindingAdapter("onClickToggleSwitch")
fun switchSetting(view: View, switchId: Int) {
    val switch: SwitchMaterial = view.findViewById(switchId)
    view.setOnClickListener { switch.isChecked = !switch.isChecked }
}

@BindingAdapter("onValueChanged")
fun editTextSetting(view: EditText, lambda: () -> Unit) {
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            lambda()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

@BindingAdapter("onSettingImeDone")
fun editTextImeDone(view: EditText, lambda: () -> Unit) {
    view.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lambda()

            view.clearFocus()

            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            return@setOnEditorActionListener true
        }
        false
    }
}

@BindingAdapter("onFocusChangeVisibilityOf")
fun setEditTextOnFocusChangeVisibilityOf(editText: EditText, view: View) {
    editText.setOnFocusChangeListener { _, hasFocus ->
        view.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
    }
}

@BindingAdapter("onProgressChanged")
fun setListener(view: SeekBar, lambda: (Any) -> Unit) {
    view.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) lambda(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}


@BindingAdapter("android:scaleType")
fun setImageViewScaleType(imageView: ImageView, scaleType: ImageView.ScaleType) {
    imageView.scaleType = scaleType
}

@BindingAdapter("assistantPhoneNumberValidation")
fun addPhoneNumberEditTextValidation(editText: EditText, enabled: Boolean) {
    if (!enabled) return
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            when {
                s?.matches(Regex("\\d+")) == false ->
                    editText.error =
                        editText.context.getString(R.string.assistant_error_phone_number_invalid_characters)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

@BindingAdapter("assistantPhoneNumberPrefixValidation")
fun addPrefixEditTextValidation(editText: EditText, enabled: Boolean) {
    if (!enabled) return
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null || s.isEmpty() || !s.startsWith("+")) {
                editText.setText("+$s")
            }
        }
    })
}

@BindingAdapter("assistantUsernameValidation")
fun addUsernameEditTextValidation(editText: EditText, enabled: Boolean) {
    if (!enabled) return
    val usernameRegexp = corePreferences.config.getString(
        "assistant",
        "username_regex",
        "^[a-z0-9+_.\\-]*\$"
    )!!
    val usernameMaxLength = corePreferences.config.getInt("assistant", "username_max_length", 64)
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            when {
                s?.matches(Regex(usernameRegexp)) == false ->
                    editText.error =
                        editText.context.getString(R.string.assistant_error_username_invalid_characters)
                (s?.length ?: 0) > usernameMaxLength -> {
                    editText.error =
                        editText.context.getString(R.string.assistant_error_username_too_long)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

@BindingAdapter("emailConfirmationValidation")
fun addEmailEditTextValidation(editText: EditText, enabled: Boolean) {
    if (!enabled) return
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                editText.error =
                    editText.context.getString(R.string.assistant_error_invalid_email_address)
            }
        }
    })
}

@BindingAdapter("urlConfirmationValidation")
fun addUrlEditTextValidation(editText: EditText, enabled: Boolean) {
    if (!enabled) return
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!Patterns.WEB_URL.matcher(s).matches()) {
                editText.error =
                    editText.context.getString(R.string.assistant_remote_provisioning_wrong_format)
            }
        }
    })
}

@BindingAdapter("passwordConfirmationValidation")
fun addPasswordConfirmationEditTextValidation(password: EditText, passwordConfirmation: EditText) {
    password.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (passwordConfirmation.text == null || s == null || passwordConfirmation.text.toString() != s.toString()) {
                passwordConfirmation.error =
                    passwordConfirmation.context.getString(R.string.assistant_error_passwords_dont_match)
            } else {
                passwordConfirmation.error = null // To clear other edit text field error
            }
        }
    })

    passwordConfirmation.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (password.text == null || s == null || password.text.toString() != s.toString()) {
                passwordConfirmation.error =
                    passwordConfirmation.context.getString(R.string.assistant_error_passwords_dont_match)
            }
        }
    })
}

@BindingAdapter("errorMessage")
fun setEditTextError(editText: EditText, error: String?) {
    if (error != editText.error) {
        editText.error = error
    }
}

@InverseBindingAdapter(attribute = "errorMessage")
fun getEditTextError(editText: EditText): String? {
    return editText.error?.toString()
}

@BindingAdapter("errorMessageAttrChanged")
fun setEditTextErrorListener(editText: EditText, attrChange: InverseBindingListener) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            attrChange.onChange()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            editText.error = null
            attrChange.onChange()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

@BindingAdapter("android:layout_margin")
fun setConstraintLayoutMargins(view: View, margins: Float) {
    val params = view.layoutParams as ConstraintLayout.LayoutParams
    val m = margins.toInt()
    params.setMargins(m, m, m, m)
    view.layoutParams = params
}

@BindingAdapter("android:layout_marginTop")
fun setConstraintLayoutTopMargin(view: View, margins: Float) {
    val params = view.layoutParams as ConstraintLayout.LayoutParams
    val m = margins.toInt()
    params.setMargins(params.leftMargin, m, params.rightMargin, params.bottomMargin)
    view.layoutParams = params
}

@BindingAdapter("android:layout_marginBottom")
fun setConstraintLayoutBottomMargin(view: View, margins: Float) {
    val params = view.layoutParams as ConstraintLayout.LayoutParams
    val m = margins.toInt()
    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, m)
    view.layoutParams = params
}

@BindingAdapter("android:layout_marginEnd")
fun setConstraintLayoutEndMargin(view: View, margins: Float) {
    val params = view.layoutParams as ConstraintLayout.LayoutParams
    val m = margins.toInt()
    params.marginEnd = m
    view.layoutParams = params
}

@BindingAdapter("android:onTouch")
fun View.setTouchListener(listener: View.OnTouchListener) {
    setOnTouchListener(listener)
}

@BindingAdapter("entries")
fun Spinner.setEntries(entries: List<Any>?) {
    if (entries != null) {
        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entries)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter = arrayAdapter
    }
}

@BindingAdapter("selectedValueAttrChanged")
fun Spinner.setInverseBindingListener(listener: InverseBindingListener) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (tag != position) {
                listener.onChange()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(value: Any?) {
    if (adapter != null) {
        val position = (adapter as ArrayAdapter<Any>).getPosition(value)
        setSelection(position, false)
        tag = position
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun Spinner.getSelectedValue(): Any? {
    return selectedItem
}


