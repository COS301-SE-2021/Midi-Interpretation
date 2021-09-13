package com.noxception.midisense.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

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
    @Query("SELECT s FROM ScoreEntity s LEFT JOIN FETCH s.fileContents WHERE s.fileDesignator=:fileDesignator")
    Optional<ScoreEntity> findByFileDesignator(@Param("fileDesignator") String fileDesignator);

    @Transactional
    @Modifying
    @Query("DELETE FROM ScoreEntity s WHERE s.lastModified <= :date")
    void purge(@Param("date") Date purgeDate);



}
