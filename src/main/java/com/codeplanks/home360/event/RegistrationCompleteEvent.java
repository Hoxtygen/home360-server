package com.codeplanks.home360.event;

import com.codeplanks.home360.domain.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


/**
 * @author Wasiu Idowu
 */
@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
  private AppUser user;
  private String applicationUrl;

  public RegistrationCompleteEvent(AppUser user, String applicationUrl) {
    super(user);
    this.user = user;
    this.applicationUrl = applicationUrl;
  }
}
