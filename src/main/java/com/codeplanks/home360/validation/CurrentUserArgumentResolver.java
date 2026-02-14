/* (C)2025 */
package com.codeplanks.home360.validation;

import com.codeplanks.home360.exception.UnAuthorizedException;
import com.codeplanks.home360.service.UserServiceImpl;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
  private final UserServiceImpl userService;

  public CurrentUserArgumentResolver(UserServiceImpl userService) {
    this.userService = userService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentUser.class);
  }

  @Override
  public Object resolveArgument(@NonNull MethodParameter parameter, @NonNull Message<?> message)
      throws Exception {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    Authentication authentication = (Authentication) accessor.getUser();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnAuthorizedException("User is not authenticated");
    }
    return userService.getUser(authentication.getName());
  }
}
