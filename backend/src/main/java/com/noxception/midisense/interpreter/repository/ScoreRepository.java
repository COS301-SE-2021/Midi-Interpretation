package com.noxception.midisense.interpreter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends CrudRepository<ScoreEntity, Long> {
    @Query("SELECT s FROM ScoreEntity s WHERE s.fileDesignator=?1")
    Optional<ScoreEntity> findByFileDesignator(String fileDesignator);
}
