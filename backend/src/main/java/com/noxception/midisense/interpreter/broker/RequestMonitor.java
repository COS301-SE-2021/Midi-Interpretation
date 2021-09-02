package com.noxception.midisense.interpreter.broker;

public class RequestMonitor<T> {
    private RequestStatus requestStatus;
    private T resource;

    public RequestMonitor() {
        resource = null;
        requestStatus = RequestStatus.EMPTY;
    }

    public void monitor(){
        resource = null;
        requestStatus = RequestStatus.EMPTY;
    }

    public void await(){
        while(requestStatus.equals(RequestStatus.EMPTY));
    }

    public T getResource(){
        T response = resource;
        this.resource = null;
        this.requestStatus = RequestStatus.EMPTY;
        return response;
    }

    public void acquire(T resource){
        this.resource = resource;
        this.requestStatus = RequestStatus.LOADED;
    }

    public void abort(){
        this.resource = null;
        this.requestStatus = RequestStatus.EMPTY;
    }

    enum RequestStatus{
        EMPTY,
        LOADED,
    }
}
