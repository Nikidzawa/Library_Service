package ru.nikidzawa.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private ReadersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ReaderEntity> readerEntity = repository.findFirstByNickname(username);
        return readerEntity.map(MyUserDetails::new).orElseThrow(() -> new RuntimeException("Ошибка"));
    }
}
