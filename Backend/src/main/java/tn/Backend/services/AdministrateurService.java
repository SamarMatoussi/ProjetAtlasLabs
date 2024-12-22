package tn.Backend.services;



import tn.Backend.dto.AgentDto;
import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.User;

import java.util.List;
import java.util.Optional;

public interface AdministrateurService {
    Response addAgent(AgentDto agentDto);

    Response revokeAccount(Long cin, boolean activate);

    //Response revokeAccount(String email, boolean activate);
    List<UserDto> getAllUsersExceptAdmin();
    Response updateUser(User user);
    Optional<User> findUserById(Long id);

    Response deleteUser(Long id);

    List<UserDto> getAllAdmin();
}