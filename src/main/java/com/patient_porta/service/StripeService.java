// src/main/java/com/patient_porta/service/StripeService.java
package com.patient_porta.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public StripeService(@Value("${stripe.api-key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public String createPaymentIntent(long amountVnd) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountVnd)         // VND: số nguyên
                .setCurrency("vnd")
                .addPaymentMethodType("card")
                .build();
        PaymentIntent pi = PaymentIntent.create(params);
        return pi.getClientSecret();
    }

    public PaymentIntent retrieve(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
