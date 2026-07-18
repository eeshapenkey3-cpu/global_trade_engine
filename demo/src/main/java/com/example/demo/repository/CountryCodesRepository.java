// THE FILING CLERK: Safely runs the background SQL queries to grab data from the database.
package com.example.demo.repository;

import com.example.demo.model.CountryCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CountryCodesRepository extends JpaRepository<CountryCodes, String> {

    // Forces Hibernate to explicitly match against your Java object field directly
    @Query("SELECT c FROM CountryCodes c WHERE LOWER(c.countryName) = LOWER(:countryName)")
    Optional<CountryCodes> findByCountryName(@Param("countryName") String countryName);
}