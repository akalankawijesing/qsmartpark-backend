package com.smart.q.smartq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payhere")
public class PayHereConfig {
    
    // Production credentials
    private String merchantId;
    private String merchantSecret;
    
    // Sandbox credentials
    private String sandboxMerchantId;
    private String sandboxMerchantSecret;
    
    // Environment flag
    private boolean sandbox = true; // Default to sandbox for development
    
    // URLs
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    
    /**
     * Get the appropriate merchant ID based on environment
     */
    public String getMerchantId() {
        return sandbox ? sandboxMerchantId : merchantId;
    }
    
    /**
     * Get the appropriate merchant secret based on environment
     */
    public String getMerchantSecret() {
        return sandbox ? sandboxMerchantSecret : merchantSecret;
    }
    
    /**
     * Get PayHere payment URL based on environment
     */
    public String getPaymentUrl() {
        return sandbox ? "https://sandbox.payhere.lk/pay/checkout" : "https://www.payhere.lk/pay/checkout";
    }
}