package com.smart.q.smartq.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PayHereWebhookVerifier {
    
    @Value("${payhere.merchant-secret}")
    private String merchantSecret;
    
    public boolean verifyWebhookSignature(Map<String, String> payload, String receivedSignature) {
        try {
            // 1. Sort all parameters alphabetically
            Map<String, String> sortedParams = new TreeMap<>(payload);
            
            // 2. Create the message to sign
            StringBuilder message = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                message.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            // Remove the last '&'
            if (message.length() > 0) {
                message.setLength(message.length() - 1);
            }
            
            // 3. Calculate HMAC SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(merchantSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            byte[] hash = sha256_HMAC.doFinal(message.toString().getBytes(StandardCharsets.UTF_8));
            String computedSignature = bytesToHex(hash);
            
            // 4. Compare signatures
            return computedSignature.equalsIgnoreCase(receivedSignature);
            
        } catch (Exception e) {
            //log.error("Error verifying webhook signature", e);
            return false;
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}