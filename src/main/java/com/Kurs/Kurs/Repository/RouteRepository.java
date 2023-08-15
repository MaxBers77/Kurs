package com.Kurs.Kurs.Repository;

import com.Kurs.Kurs.Models.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Route findByName(String name);
}
