package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.Backend.entites.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
}
