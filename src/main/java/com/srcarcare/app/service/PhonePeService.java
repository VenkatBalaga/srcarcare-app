package com.srcarcare.app.service;

import com.srcarcare.app.entity.PaymentStatus;
import com.srcarcare.app.entity.PaymentTransaction;
import com.srcarcare.app.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Structural PhonePe Standard Checkout integration.
 * Credentials are supplied only via environment variables / application.properties
 * placeholders (srcarcare.phonepe.*) - never hardcoded, never exposed to the client.
 * Real merchant onboarding credentials must be configured before going live;
 * until then this service operates against the PhonePe sandbox base URL.
 */
@Service
public class PhonePeService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${srcarcare.phonepe.merchant-id}")
    private String merchantId;

    @Value("${srcarcare.phonepe.salt-key}")
    private String saltKey;

    @Value("${srcarcare.phonepe.salt-index}")
    private String saltIndex;

    @Value("${srcarcare.phonepe.base-url}")
    private String phonePeBaseUrl;

    @Value("${srcarcare.base-url}")
    private String appBaseUrl;

    public PhonePeService(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public boolean isConfigured() {
        return merchantId != null && !merchantId.startsWith("PLACEHOLDER")
                && saltKey != null && !saltKey.startsWith("PLACEHOLDER");
    }

    /**
     * Creates a local payment transaction record and returns the redirect URL the
     * customer should be sent to in order to complete payment on PhonePe.
     * When credentials are still placeholders, this returns a local "not configured" page
     * instead of calling the live PhonePe API, so no requests are ever made with fake secrets.
     */
    public PaymentInitiationResult initiatePayment(String bookingType, Long bookingId, BigDecimal amount) {
        String merchantTransactionId = "SRCC" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();

        PaymentTransaction txn = new PaymentTransaction();
        txn.setBookingType(bookingType);
        txn.setBookingId(bookingId);
        txn.setMerchantTransactionId(merchantTransactionId);
        txn.setAmount(amount);
        txn.setStatus(PaymentStatus.INITIATED);
        paymentTransactionRepository.save(txn);

        if (!isConfigured()) {
            return new PaymentInitiationResult(merchantTransactionId, "/payment/not-configured?txn=" + merchantTransactionId, false);
        }

        // Real integration point: build the PhonePe Pay API request here using
        // merchantId / saltKey / saltIndex and phonePeBaseUrl, per PhonePe's
        // Standard Checkout documentation. The checksum below follows PhonePe's
        // X-VERIFY scheme (SHA256(base64Payload + apiEndpoint + saltKey) + "###" + saltIndex).
        String redirectUrl = appBaseUrl + "/payment/redirect?txn=" + merchantTransactionId;
        return new PaymentInitiationResult(merchantTransactionId, redirectUrl, true);
    }

    public String buildChecksum(String base64Payload, String apiEndpoint) {
        try {
            String toHash = base64Payload + apiEndpoint + saltKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash) + "###" + saltIndex;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to compute PhonePe checksum", e);
        }
    }

    public Optional<PaymentTransaction> getTransaction(String merchantTransactionId) {
        return paymentTransactionRepository.findByMerchantTransactionId(merchantTransactionId);
    }

    public void markStatus(String merchantTransactionId, PaymentStatus status, String providerReference) {
        paymentTransactionRepository.findByMerchantTransactionId(merchantTransactionId).ifPresent(txn -> {
            txn.setStatus(status);
            txn.setProviderReference(providerReference);
            paymentTransactionRepository.save(txn);
        });
    }

    public record PaymentInitiationResult(String merchantTransactionId, String redirectUrl, boolean liveGateway) {}
}
