package com.dalbertang.live_chat_backend.repository;

import com.dalbertang.live_chat_backend.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByEmail(String email);

    // For adding a new user to the database since we have IDs (otherwise ReactiveCrudRepository will try to "update" a non-existent row)
    @Query("INSERT INTO users (id, email, name, roles) VALUES (:id, :email, :name, :roles) RETURNING *")
    Mono<User> insertUser(String id, String email, String name, String[] roles);

}
