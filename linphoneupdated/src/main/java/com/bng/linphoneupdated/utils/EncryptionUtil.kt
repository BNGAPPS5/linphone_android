package com.bng.linphoneupdated.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import java.security.KeyStore

object EncryptionUtil {
    private const val ALGORITHM = "AES/GCM/NoPadding"

    fun decryptFile(context: Context, assetName: String, key: SecretKey): String {
        val assetManager = context.assets
        val encryptedFile = File(context.cacheDir, assetName)
        assetManager.open(assetName).use { input ->
            FileOutputStream(encryptedFile).use { output ->
                input.copyTo(output)
            }
        }

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)

        val decryptedFile = File(context.cacheDir, "rootcaa.pem")
        decryptedFile.outputStream().use { fileOutput ->
            CipherInputStream(encryptedFile.inputStream(), cipher).use { cipherInput ->
                cipherInput.copyTo(fileOutput)
            }
        }

        return decryptedFile.absolutePath
    }
}

fun loadSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    return (keyStore.getEntry("MySecretKey", null) as KeyStore.SecretKeyEntry).secretKey
}

fun getDecryptedRootCA(context: Context): String {
    val key = loadSecretKey()
    return EncryptionUtil.decryptFile(context, "rootcaa.pem.enc", key)
}
