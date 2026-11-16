package com.ohli.floatingai;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/** Stores provider tokens encrypted with an Android Keystore AES-GCM key. */
public final class SecretStore {
    private static final String PREFS = "private_ai_credentials";
    private static final String ALIAS = "floating_ai_aes_key_v1";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private final SharedPreferences prefs;

    public SecretStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(String name, String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            prefs.edit().remove(name + "_iv").remove(name + "_data").apply();
            return;
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey());
        byte[] encrypted = cipher.doFinal(value.trim().getBytes(StandardCharsets.UTF_8));
        prefs.edit()
                .putString(name + "_iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP))
                .putString(name + "_data", Base64.encodeToString(encrypted, Base64.NO_WRAP))
                .apply();
    }

    public String read(String name) throws Exception {
        String ivText = prefs.getString(name + "_iv", null);
        String dataText = prefs.getString(name + "_data", null);
        if (ivText == null || dataText == null) return "";
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(),
                new GCMParameterSpec(128, Base64.decode(ivText, Base64.NO_WRAP)));
        byte[] plain = cipher.doFinal(Base64.decode(dataText, Base64.NO_WRAP));
        return new String(plain, StandardCharsets.UTF_8);
    }

    public boolean has(String name) {
        return prefs.contains(name + "_data");
    }

    private SecretKey getOrCreateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        java.security.Key existing = keyStore.getKey(ALIAS, null);
        if (existing instanceof SecretKey) return (SecretKey) existing;

        KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build();
        generator.init(spec);
        return generator.generateKey();
    }
}
