package tn.Backend.services;

import tn.Backend.dto.EmployeDto;
import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.Role;
import tn.Backend.entites.User;


import java.util.List;
import java.util.Optional;

public interface AgentService {


    List<UserDto> getEmployesByAgent(Long agentId);

    Optional<User> findUserByIdAndRole(Long id, Role role);

    Response revokeAccount(Long cin, boolean activate);

    //Response addEmploye(EmployeDto employeDto);

    Response addEmployeByAuthenticatedAgent(EmployeDto employeDto);

    Response updateEmploye(Long cin, EmployeDto employeDto);

    List<UserDto> getAllEmployes();

    Response deleteEmploye(Long cin);

    Response deleteUser(Long id);

    List<EmployeDto> getEmployesByAuthenticatedAgent();

    Response updateEmployeByAuthenticatedAgent(Long cin, EmployeDto employeDto);
}
