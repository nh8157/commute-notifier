package org.commuter_notifier;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<String[], String> {

    @Override
    public String handleRequest(String[] input, Context context) {
        try {
            App.main(input);
            return "Success";
        } catch (Exception e) {
            System.out.println("Failed due to %s".formatted(e));
            return "Failed";
        }
    }
}
