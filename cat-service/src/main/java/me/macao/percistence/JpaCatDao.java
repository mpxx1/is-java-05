package me.macao.percistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCatDao
    extends JpaRepository<Cat, Long> { }
