package com.example.sweaterApp.Services;

import com.example.sweaterApp.Models.Usr;
import com.example.sweaterApp.Repository.UsrRepository;
import com.example.sweaterApp.Security.UsrDetails;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UsrDetailsService implements UserDetailsService {
    private final UsrRepository usrRepository;
    @Autowired
    public UsrDetailsService(UsrRepository usrRepository) {
        this.usrRepository = usrRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usr> usr = Optional.ofNullable(usrRepository.findByUsername(username));
        if(usr.isEmpty()) throw new UsernameNotFoundException("User not found");
        return new UsrDetails(usr.get());
    }

    public Usr findOne(Long id) {
        Optional<Usr> usr = usrRepository.findById(id);
        return usr.orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        usrRepository.deleteById(id);
    }


    public Usr getUsrById(Long id) {
        Optional<Usr> foundUsr = usrRepository.findById(id);

        if (foundUsr.isPresent()) {
            Hibernate.initialize(foundUsr.get());
        }
        return foundUsr.get();
    }

    public void subscribe(Usr currentUsr, Usr usr) {
        usr.getSubscribers().add(currentUsr);
        usrRepository.save(usr);
    }

    public void unsubscribe(Usr currentUsr, Usr usr) {
        usr.getSubscribers().remove(currentUsr);
        usrRepository.save(usr);
    }
}
