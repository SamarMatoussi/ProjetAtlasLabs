package tn.Backend.services;

import tn.Backend.dto.ActivitesDto;
import tn.Backend.dto.PosteDto;
import tn.Backend.exception.ResourceNotFound;

import java.util.List;

public interface PosteService {
    List<PosteDto> getAllPostes();
    PosteDto getPosteById(Long id) throws ResourceNotFound;
    PosteDto createPoste(PosteDto posteDto);
    void deletePoste(Long id);
    PosteDto updatePoste(Long id, PosteDto posteDto) throws ResourceNotFound;

    PosteDto removeActivitesFromPoste(Long posteId, List<ActivitesDto> activitesDto) throws ResourceNotFound;

    PosteDto addActivitesToPoste(Long posteId, List<ActivitesDto> activitesDto) throws ResourceNotFound;
}
