package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tn.Backend.entites.ForgotPasswordToken;
import tn.Backend.entites.User;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {

    // Trouver un token spécifique pour un utilisateur donné
    @Query("select fpt from ForgotPasswordToken fpt where fpt.token = ?1 and fpt.user = ?2")
    Optional<ForgotPasswordToken> findByTokenAndUser(Integer token, User user);

    // Trouver un token uniquement par son code
    Optional<ForgotPasswordToken> findByToken(Integer token);

    // Supprimer les tokens liés à un utilisateur
    @Modifying
    @Transactional
    @Query("delete from ForgotPasswordToken fpt where fpt.user = ?1")
    void deleteByUser(User user);
}
