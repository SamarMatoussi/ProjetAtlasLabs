package tn.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private String responseMessage;
    private String email;
    //private Long agenceId;



}
