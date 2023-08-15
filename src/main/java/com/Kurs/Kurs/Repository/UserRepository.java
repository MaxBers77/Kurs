package com.Kurs.Kurs.Repository;

import com.Kurs.Kurs.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String userName);
    User findUserByLastName(String lastName);

}