/* (C)2025 */
package com.codeplanks.home360.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class DuplicateSessionEvent extends ApplicationEvent {
  private String userEmail;
  private String ipAddress;
  private String operatingSystem;
  private String deviceType;
  private String browserAgent;

  public DuplicateSessionEvent(
      String userEmail,
      String deviceType,
      String ipAddress,
      String browserAgent,
      String operatingSystem) {
    super(userEmail);
    this.userEmail = userEmail;
    this.deviceType = deviceType;
    this.ipAddress = ipAddress;
    this.browserAgent = browserAgent;
    this.operatingSystem = operatingSystem;
  }
}
