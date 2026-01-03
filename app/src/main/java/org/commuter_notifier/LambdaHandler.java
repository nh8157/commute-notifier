package org.commuter_notifier;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Void> {
    @Override
    public Void handleRequest(Map<String, Object> input, Context context) {
        try {
            App.run(input);
            return null;
        } catch (Exception e) {
            System.out.println("Failed due to %s".formatted(e));
            return null;
        }
    }
}
