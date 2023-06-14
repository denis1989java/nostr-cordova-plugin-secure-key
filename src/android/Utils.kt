package com.nostr.band;

import fr.acinq.secp256k1.Secp256k1
import org.spongycastle.util.encoders.Hex
import java.security.MessageDigest

object Utils {
    private val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
    private val secp256k1 = Secp256k1.get()

    @JvmStatic
    fun pubkeyCreate(privKey: ByteArray) =
            secp256k1.pubKeyCompress(secp256k1.pubkeyCreate(privKey)).copyOfRange(1, 33)

    @JvmStatic
    fun sign(data: ByteArray, privKey: ByteArray): ByteArray =
            secp256k1.signSchnorr(data, privKey, null)

    fun sha256(byteArray: ByteArray): ByteArray = sha256.digest(byteArray)
}

fun ByteArray.toHex() = String(Hex.encode(this))
