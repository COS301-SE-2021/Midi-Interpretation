package com.noxception.midisense.interpreter.repository;

import java.util.Optional;

/**
 * A wrapper for the {@link ScoreRepository} class that enables subclasses to alter or mock repository behaviour
 */
public class DatabaseManager {
    private ScoreRepository repository;

    public Optional<ScoreEntity> findByFileDesignator(String fileDesignator){
       if(repository != null)
           return repository.findByFileDesignator(fileDesignator);
       return Optional.empty();
    }

    public ScoreEntity save(ScoreEntity s){
        if(repository != null)
            return repository.save(s);
        return null;
    }

    public void attachRepository(ScoreRepository repository){
        this.repository = repository;
    }
}
