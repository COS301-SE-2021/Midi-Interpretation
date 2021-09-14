package com.noxception.midisense.interpreter.broker;

import java.util.concurrent.atomic.AtomicReference;

public class RequestMonitor<T> {
    private AtomicReference<RequestStatus> requestStatus;
    private T resource;

    public RequestMonitor() {
        resource = null;
        requestStatus = new AtomicReference<>();
        requestStatus.set(RequestStatus.EMPTY);
    }

    public void monitor(){
        resource = null;
        requestStatus.set(RequestStatus.EMPTY);
    }

    public void await(){
        while(requestStatus.get().equals(RequestStatus.EMPTY));
    }

    public T getResource(){
        T response = resource;
        this.resource = null;
        this.requestStatus.set(RequestStatus.EMPTY);
        return response;
    }

    public void acquire(T resource){
        this.resource = resource;
        this.requestStatus.set(RequestStatus.LOADED);
    }

    public void abort(){
        this.resource = null;
        this.requestStatus.set(RequestStatus.FAILED);
    }

    enum RequestStatus{
        EMPTY,
        LOADED,
        FAILED
    }
}
