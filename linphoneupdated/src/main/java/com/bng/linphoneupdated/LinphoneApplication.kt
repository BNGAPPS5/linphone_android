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
package com.bng.linphoneupdated

import android.annotation.SuppressLint
import android.content.Context
import com.bng.linphoneupdated.core.*
import com.bng.linphoneupdated.utils.AudioRouteUtils
import com.bng.linphoneupdated.utils.LinphoneUtils
import org.linphone.core.*
import org.linphone.core.tools.Log
import java.io.File
import java.io.IOException
import java.util.*
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class LinphoneApplication {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var corePreferences: CorePreferences

        @SuppressLint("StaticFieldLeak")
        lateinit var coreContext: CoreContext

        @JvmField
        var callState = Call.State.Idle

        @JvmStatic
        fun getCallState(): Call.State {
            return callState

        }

        @JvmStatic
        public fun createConfig(
            context: Context
        ) {
            if (::corePreferences.isInitialized) {
                return
            }

            // Factory.instance().setLogCollectionPath(context.filesDir.absolutePath)
            Factory.instance().enableLogCollection(LogCollectionState.Enabled)
            // For VFS
            // Factory.instance().setCacheDir(context.cacheDir.absolutePath)

            corePreferences = CorePreferences(context)
            corePreferences.copyAssetsFromPackage()

            val config = Factory.instance().createConfigWithFactory(
                corePreferences.configPath, corePreferences.factoryConfigPath
            )
            corePreferences.config = config
            val appName = context.getString(R.string.app_name)
            Factory.instance().setLoggerDomain(appName)
            Factory.instance().enableLogcatLogs(corePreferences.logcatLogsOutput)
            if (corePreferences.debugLogs) {
                Factory.instance().loggingService.setLogLevel(LogLevel.Message)
            }
            ensureCoreExists(context)
            //  startCore(userAgent, userId, localIp, transportType)

        }

        @JvmStatic
        public fun startCall(to: String, transportType: Int) {
            var stringAddress = to.trim()
            Log.w("Log check start call address before  interpretUrl $stringAddress")

            val address: Address? = coreContext.core.interpretUrl(
                stringAddress, LinphoneUtils.applyInternationalPrefix()
            )
            Log.w("Log check start call address after interpretUrl $address")

            if (transportType == 0) {
                address!!.transport = TransportType.Udp
            } else if (transportType == 1) {
                address!!.transport = TransportType.Tcp
            } else if (transportType == 2) {
                address!!.transport = TransportType.Tls
            } else if (transportType == 3) {
                address!!.transport = TransportType.Dtls
            } else {
                address!!.transport = TransportType.Tls
            }
            if (address == null) {
                /*   Log.e("[Context] Failed to parse $stringAddress", "abort outgoing call")*/
                //    callErrorMessageResourceId.value = Event(context.getString(org.linphone.core.R.string.call_error_network_unreachable))
                return
            }
            coreContext.startCall(address)

        }


        public fun isSpeakerOn(isSpeakerOn: Boolean) {
            val currentAudioDevice = coreContext.core.currentCall?.outputAudioDevice
            if (isSpeakerOn) {
                AudioRouteUtils.routeAudioToSpeaker(null, true)
            } else {
                if (currentAudioDevice != null) {
                    if (currentAudioDevice!!.type == AudioDevice.Type.Speaker) {
                        AudioRouteUtils.routeAudioToEarpiece(null, true)
                    } else if (currentAudioDevice!!.type == AudioDevice.Type.Headphones || currentAudioDevice!!.type == AudioDevice.Type.Headset || currentAudioDevice!!.type == AudioDevice.Type.Earpiece || currentAudioDevice!!.type == AudioDevice.Type.HearingAid) {
                        AudioRouteUtils.routeAudioToHeadset(null, true)
                    } else if (currentAudioDevice!!.type == AudioDevice.Type.Bluetooth) {
                        AudioRouteUtils.routeAudioToBluetooth(null, true)
                    } else {
                        AudioRouteUtils.routeAudioToEarpiece(null, true)
                    }
                } else {
                    AudioRouteUtils.routeAudioToEarpiece(null, true)

                }

            }
        }

        fun pauseOrResumeCall(pause: Boolean) {
            try {
                if (coreContext.core.callsNb == 0) return
                val call =
                    if (coreContext.core.currentCall != null) coreContext.core.currentCall else coreContext.core.calls[0]
                call ?: return

                if (call != null) {
                    // val currentCall: Call? = coreContext.core.currentCall

                    /*if (pause) {
                    val currentCall: Call? = coreContext.core.currentCall
                    if (currentCall != null) {
                        coreContext.core.currentCall!!.pause()
                    }
                } else {
                    val pausedCalls: List<Call> =
                        getCallsInState(
                            coreContext.core,
                            Arrays.asList(
                                Call.State.Paused,
                                Call.State.Pausing
                            )
                        )
                    if (pausedCalls != null && pausedCalls.size > 0) {
                        val callToResume: Call = pausedCalls[0]
                        if (callToResume != null) {
                            callToResume.resume()
                            //  coreContext.core.currentCall!!.resume()
                        }
                    }


                }*/


                    if (call?.state != Call.State.Paused && call?.state != Call.State.Pausing) {
                        // If our call isn't paused, let's pause it
                        call?.pause()
                    } else if (call?.state != Call.State.Resuming) {
                        // Otherwise let's resume it
                        call?.resume()
                    } else {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        public fun getCallsInState(
            lc: Core?, states: Collection<Call.State?>
        ): List<Call> {
            val foundCalls: MutableList<Call> = java.util.ArrayList<Call>()
            for (call in lc!!.calls) {
                if (states.contains(call.state)) {
                    foundCalls.add(call)
                }
            }
            return foundCalls
        }


        /* fun getLinphoneCalls(lc: Core): ArrayList<Call> {
             // return a modifiable list
             return ArrayList<Call>(Arrays.asList(lc.calls))
         }*/


        /*    fun pauseOrResumeCall(putCallOnHold: Boolean) {
                if (putCallOnHold) {
                    coreContext.core.currentCall?.pause()
                } else {
                    coreContext.core.currentCall?.resume()
                }
            }
    */
        public fun isCallMicEnabled(isMicOn: Boolean) {
            coreContext.core.isMicEnabled = isMicOn
        }

        @JvmStatic
        public fun ensureCoreExists(
            context: Context,
            pushReceived: Boolean = false,
            service: CoreService? = null,
            useAutoStartDescription: Boolean = false
        ): Boolean {
            if (::coreContext.isInitialized && !coreContext.stopped) {
                return false
            }
            coreContext = CoreContext(
                context, corePreferences.config, service, useAutoStartDescription
            )

            return true
        }

        public fun startCore(
            userAgent: String, userId: String, localIp: String, transportType: Int
        ) {
            var transport: TransportType = TransportType.Tls
            if (transportType == 0) {
                transport = TransportType.Udp
            } else if (transportType == 1) {
                transport = TransportType.Tcp
            } else if (transportType == 2) {
                transport = TransportType.Tls
            } else if (transportType == 3) {
                transport = TransportType.Dtls
            } else {
                transport = TransportType.Tls
            }
            coreContext.start(
                userAgent, userId, localIp, transport
            )
        }

        public fun contextExists(): Boolean {
            return ::coreContext.isInitialized
        }
    }


    /* override fun onCreate() {
         super.onCreate()
         Log.i("Linphone App", "Application is being created")
         //val appName = getString(R.string.app_name)
         // Log.i("[$appName]", "Application is being created")
         createConfig(applicationContext)
     }*/
}
