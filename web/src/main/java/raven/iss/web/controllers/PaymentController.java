package raven.iss.web.controllers;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import raven.iss.web.security.permissions.UserPermission;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostConstruct
    private void setUp() {
        Stripe.apiKey = stripeKey;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class PaymentResponse {
        private String clientSecret;
    }

    @UserPermission
    @PostMapping("/api/create-payment-intent")
    public PaymentResponse intent() throws StripeException {
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("usd")
                    .setAmount(150 * 100L)
                    .build();
        PaymentIntent intent = PaymentIntent.create(createParams);
        return new PaymentResponse(intent.getClientSecret());
    }

    @PostMapping("/webhook")
    public String webhook(@RequestBody String payload,
                          @RequestHeader("Stripe-Signature") String sigHeader) {

        if(sigHeader == null) {
            return "";
        }

        Event event;
        try {
            event = Webhook.constructEvent(
                            payload, sigHeader, endpointSecret
            );
        } catch (SignatureVerificationException e) {
            return "";
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            log.info("Payment succeeded for {}", paymentIntent.getAmount());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }

        return "";
    }

}
