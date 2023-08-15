package com.Kurs.Kurs.Repository;

import com.Kurs.Kurs.Models.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB,Long> {
}
