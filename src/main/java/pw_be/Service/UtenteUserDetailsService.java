package pw_be.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pw_be.Model.Utente;
import pw_be.Repository.Utente_Repository;

import java.util.Collections;

@Service
public class UtenteUserDetailsService implements UserDetailsService {

    private final Utente_Repository utenteRepository;

    public UtenteUserDetailsService(Utente_Repository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByNomeUtente(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        return new User(
                utente.getNomeUtente(),
                utente.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + utente.getRuolo())
                )
        );
    }
}

