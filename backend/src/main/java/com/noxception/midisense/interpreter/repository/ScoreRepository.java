package com.noxception.midisense.interpreter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * An interface for defining interaction with score entities and an external CRUD repository
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */
public interface ScoreRepository extends CrudRepository<ScoreEntity, Long> {

    /** A lookup function for existing scores in the database
     *
     * @param fileDesignator the unique identifier which distinguishes interpreted works in the repository
     * @return a collection of scores whose file designator is matched completely. Returns none if there is no such score
     * in the repository
     */
    @Query("SELECT s FROM ScoreEntity s WHERE s.fileDesignator=?1")
    Optional<ScoreEntity> findByFileDesignator(String fileDesignator);
}
