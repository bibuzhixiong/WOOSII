package sochat.so.com.android.llpay_util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import Decoder.BASE64Decoder;

/**
 * Created by Administrator on 2017/5/19.
 */

public class RSAUtil {

    /**
     * 公钥加密的方法
     * @param source
     * @param public_key
     * @return
     * @throws Exception
     */
    public static String encrypt(String source, String public_key) throws Exception {
        byte[] keyByte = android.util.Base64.decode(public_key, android.util.Base64.NO_WRAP);
        X509EncodedKeySpec x509ek = new X509EncodedKeySpec(keyByte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509ek);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");       //"RSA"、"RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] sbt = source.getBytes("UTF-8");
        byte[] epByte = cipher.doFinal(sbt);
        String epStr = android.util.Base64.encodeToString(epByte, android.util.Base64.NO_WRAP);

        return epStr;
    }

    /**
     * 私钥解密的方法
     * @param cryptograph
     * @param private_key
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph, String private_key)
            throws Exception
    {

        BASE64Decoder b64d = new BASE64Decoder();
        byte[] keyByte = b64d.decodeBuffer(private_key);
        PKCS8EncodedKeySpec s8ek = new PKCS8EncodedKeySpec(keyByte);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(s8ek);

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(cryptograph);
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

}
