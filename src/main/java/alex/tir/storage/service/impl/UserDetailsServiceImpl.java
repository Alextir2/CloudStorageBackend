package alex.tir.storage.service.impl;

import alex.tir.storage.entity.User;
import alex.tir.storage.entity.UserPrincipal;
import alex.tir.storage.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl{
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserPrincipal loadUserByUsername(String email) {
                User user = userRepository
                        .findUserByEmailIgnoreCase(email)
                        .orElseThrow(() -> new UsernameNotFoundException("The email " + email + " has not been found"));
                List<SimpleGrantedAuthority> grantedAuthorities = user
                        .getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList());
                return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), grantedAuthorities);
            }
        };
    }

}
