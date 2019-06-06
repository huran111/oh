package com.tykj.utils;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @auther huran
 * @date
 **/
public class AESUtils {
    public static String decrypt(String encryptedData, String sessionKey, String iv, String encodingFormat) throws Exception {

        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            BASE64Decoder base64Decoder = new BASE64Decoder();

            byte[] _encryptedData = base64Decoder.decodeBuffer(encryptedData);

            byte[] _sessionKey = base64Decoder.decodeBuffer(sessionKey);

            byte[] _iv = base64Decoder.decodeBuffer(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(_sessionKey, "AES");

            IvParameterSpec ivParameterSpec = new IvParameterSpec(_iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] original = cipher.doFinal(_encryptedData);

            byte[] bytes = WxPKCS7Encoder.decode(original);

            String originalString = new String(bytes, encodingFormat);  return originalString;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

    }


}
