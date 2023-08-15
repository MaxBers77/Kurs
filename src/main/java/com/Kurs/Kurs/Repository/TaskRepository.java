package com.Kurs.Kurs.Repository;

import com.Kurs.Kurs.Models.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<UserTask, Long> {
}
