/* (C)2024 */
package com.codeplanks.home360.domain.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class AppUserDTO {
  private Integer id;
  private String firstName;
  private String lastName;
  private String email;
  private String address;
  private String phoneNumber;
  private Date createdAt;
  private Date updatedAt;
  private Role role;
  @Builder.Default private boolean isEnabled = false;
}
