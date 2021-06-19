package com.noxception.midisense.interpreter.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ScoreRepository extends CrudRepository<ScoreEntity, UUID> {

}
