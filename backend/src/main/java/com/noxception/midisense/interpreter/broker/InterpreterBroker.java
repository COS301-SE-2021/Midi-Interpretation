package com.noxception.midisense.interpreter.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class InterpreterBroker {
    private final StandardConfig configurations;
    private HttpClient client;
    private String serviceURL;

    public InterpreterBroker(StandardConfig configurations) {
        this.configurations = configurations;
        setupConfigurations();
    }

    private void setupConfigurations(){
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        this.serviceURL = configurations.configuration(ConfigurationName.MIDI_INTERPRETATION_URL);
    }

    public void makeRequest(Object body, Consumer<? super java.net.http.HttpResponse<java.lang.String>> onAccept, Consumer<? super java.net.http.HttpResponse<java.lang.String>> onReject) throws ExecutionException, InterruptedException, CancellationException, CompletionException {

        String bodyText = "";
        try {
            bodyText = JSONUtils.ObjectToJSON(body);
        }
        catch (JsonProcessingException ignored) {
        }

        //get the timeout
        int timeout = Integer.parseInt(configurations.configuration(ConfigurationName.MIDI_INTERPRETATION_TIMEOUT));

        //create the request
        HttpRequest request =
                HttpRequest.newBuilder(URI.create(serviceURL))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .timeout(Duration.of(timeout, ChronoUnit.SECONDS))
                        .POST(HttpRequest.BodyPublishers.ofString(bodyText))
                        .build();

        //get the response
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenAccept((res)->{
            if(res.statusCode()==200){
                //successful response from the interpreter
                onAccept.accept(res);
            }
            else{
                //handle errors here
                onReject.accept(res);
            }
        });
        response.join();
    }


}
