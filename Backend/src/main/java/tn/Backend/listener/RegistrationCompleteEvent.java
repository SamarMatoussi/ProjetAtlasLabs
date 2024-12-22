package tn.Backend.listener;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import tn.Backend.entites.Employe;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private Employe user;
    private String applicationUrl;

    public RegistrationCompleteEvent(Employe user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
