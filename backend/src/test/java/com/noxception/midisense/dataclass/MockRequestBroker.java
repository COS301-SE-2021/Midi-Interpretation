package com.noxception.midisense.dataclass;

import com.noxception.midisense.config.ConfigurationName;
import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.interpreter.broker.InterpreterBroker;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.SSLSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MockRequestBroker extends InterpreterBroker {

    private boolean isRejecting;

    public MockRequestBroker(StandardConfig configurations) {
        super(configurations);
        isRejecting = false;
    }

    public void isRejecting(){
        isRejecting = true;
    }

    @Override
    public void makeRequest(Object body, Consumer<? super HttpResponse<String>> onAccept, Consumer<? super HttpResponse<String>> onReject) throws ExecutionException, InterruptedException, CancellationException, CompletionException {
        HttpResponse<String> mockResponse;

        if(isRejecting){
            mockResponse = new MockResponse(this.configurations);
            onReject.accept(mockResponse);
        }
        else{
            mockResponse = new MockResponse(this.configurations);
            onAccept.accept(mockResponse);
        }


    }

    public static class MockResponse implements HttpResponse<String>{

        private String mockBodyText;
        private final boolean rejected;

        public MockResponse(StandardConfig con){
            this(con,false);
        }

        public MockResponse(StandardConfig con, boolean fail) {
            rejected = fail;
            try {
                FileInputStream source = new FileInputStream(con.configuration(ConfigurationName.MIDI_TESTING_SAMPLE_RESPONSE));
                mockBodyText = IOUtils.toString(source, StandardCharsets.UTF_8);
            }
            catch(IOException e){
                mockBodyText = "";
            }
        }

        @Override
        public int statusCode() {
            return (rejected)?400:200;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return null;
        }

        @Override
        public String body() {
            return this.mockBodyText;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return null;
        }

        @Override
        public HttpClient.Version version() {
            return null;
        }
    }
}
