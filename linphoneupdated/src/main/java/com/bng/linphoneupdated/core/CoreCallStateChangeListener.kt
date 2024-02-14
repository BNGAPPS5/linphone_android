package com.bng.linphoneupdated.core


interface CoreCallStateChangeListener {
    fun callIdle(message: String,protocolCode: Int)
    fun callOutgoingInit(message: String)
    fun callOutgoingRinging(message: String)
    fun callOutgoingEarlyMedia(message: String)
    fun callConnected(message: String)
    fun callPausing(message: String)
    fun callPaused(message: String)
    fun callResuming(message: String)
    fun callStreamsRunning(message: String)
    fun callError(message: String,protocolCode:Int)
    fun callReleased(message: String, protocolCode: Int)
    fun callEnd(message: String,protocolCode: Int)
    fun onMessageReceived(message: String)

}

