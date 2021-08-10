package com.noxception.midisense.dataclass;

import com.noxception.midisense.interpreter.repository.DatabaseManager;
import com.noxception.midisense.interpreter.repository.ScoreEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class that mocks the functionality of {@link com.noxception.midisense.interpreter.repository.ScoreRepository} repository class.
 * The operations are accessed in the same manner, due to the {@link DatabaseManager} wrapper from which both classes
 * inherit.
 */
public class MockRepository extends DatabaseManager {

    List<ScoreEntity> database = new ArrayList<>();

    //TODO: Claudio find a way to load and save scores. Maybe try saving before you need to make calls:
    // Hint: generate a temp midi file and use parseJSON to
    // generate a score to save, before you need to find by designator

    @Override
    public Optional<ScoreEntity> findByFileDesignator(String fileDesignator) {
        for(ScoreEntity s: database){
            if(s.getFileDesignator().equals(fileDesignator))
                return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public ScoreEntity save(ScoreEntity s) {
        database.add(s);
        return s;
    }
}
