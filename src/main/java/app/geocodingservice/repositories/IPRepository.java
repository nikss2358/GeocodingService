package app.geocodingservice.repositories;

import app.geocodingservice.entities.IP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPRepository extends JpaRepository<IP, Long> {

    Optional<IP> findByName(String name);
}
