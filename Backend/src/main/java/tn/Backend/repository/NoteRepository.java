package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.Backend.dto.NoteGlobaleDto;
import tn.Backend.entites.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Recherche des notes en fonction du CIN de l'employé

    @Query("SELECT n FROM Note n WHERE n.employe.cin = :cin")
    List<Note> findByEmployeCin(Long cin);

    Optional<Note> findByEmployeIdAndAgentIdAndKpiId(Long employeId, Long agentId, Long kpiId);

    Optional<Note> findByEmployeIdAndKpiId(Long employeId, Long kpiId);

    List<Note> findByEmployeId(Long id);

    @Query("SELECT new tn.Backend.dto.NoteGlobaleDto(n.employe.cin, " +
            "ROUND(AVG(CASE " +
            "WHEN n.appreciation = 'Mauvais' THEN 1 " +
            "WHEN n.appreciation = 'Passable' THEN 2 " +
            "WHEN n.appreciation = 'Bien' THEN 3 " +
            "WHEN n.appreciation = 'Excellent' THEN 4 " +
            "ELSE 0 END), 2)) " +
            "FROM Note n WHERE n.agentId = :agentId GROUP BY n.employe.cin")
    List<NoteGlobaleDto> getNoteGlobaleByAgent(Long agentId);



}