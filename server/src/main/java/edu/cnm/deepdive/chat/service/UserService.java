package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dao.UserRepository;
import edu.cnm.deepdive.chat.model.entity.User;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements AbstractUserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getCurrent() {
    return (User) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }

  @Override
  public User get(UUID externalKey) {
    return userRepository
        .findByExternalKey(externalKey)
        .orElseThrow();
  }

  @Override
  public User getOrCreate(String oauthKey, String displayName) {
    return userRepository
        .findByOauthKey(oauthKey)
        .orElseGet(() ->{
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          // TODO: 3/5/25 Set avatar.
          return userRepository.save(user);
        });
  }

  @Override
  public User update(User user) {
    return null;
  }
}
