package ru.nikidzawa.app.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.store.entities.ReaderEntity;
import ru.nikidzawa.app.store.repositoreis.ReadersRepository;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private ReadersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ReaderEntity> readerEntity = repository.findFirstByNickname(username);
        return readerEntity.map(MyUserDetails::new).orElseThrow(() -> new RuntimeException("Пользователя не существует"));
    }
}
