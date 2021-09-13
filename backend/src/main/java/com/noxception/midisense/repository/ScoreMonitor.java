package com.noxception.midisense.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ScoreMonitor extends Thread{

    @Autowired
    private ScoreRepository scoreRepository;

    private final Thread thread;

    private final int retentionPolicy = 60 * 1000;
    private Date retention;
    private boolean active;

    public ScoreMonitor() {
        this.active = false;
        thread = new Thread(this,ScoreMonitor.class.getSimpleName());
        retention = new Date();
    }

    public ScoreMonitor(ScoreRepository scoreRepository) {
        this();
        this.scoreRepository = scoreRepository;
    }

    public void start(){
        this.active = true;
        retention = new Date();
        log.info("Starting monitoring");
        thread.start();
    }

    public void halt(){
        this.active = false;
        log.info("Finished monitoring");
    }

    @Override
    public void run(){
        while(this.active){
            try {
                Thread.sleep(this.retentionPolicy);
                scoreRepository.purge(this.retention);
                log.info("Purging files from before: "+ this.retention);
                retention = new Date();
            } catch (InterruptedException e) {
                this.halt();
            }
        }
    }


}
