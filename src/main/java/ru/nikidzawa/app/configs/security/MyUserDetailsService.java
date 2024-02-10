package ru.nikidzawa.app.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.services.ReaderService;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    ReaderService readerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new MyUserDetails(readerService.getReader(username));
    }
}
