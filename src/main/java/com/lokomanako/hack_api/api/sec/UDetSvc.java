package com.lokomanako.hack_api.api.sec;

import com.lokomanako.hack_api.store.ent.AppUsr;
import com.lokomanako.hack_api.store.repo.UsrRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UDetSvc implements UserDetailsService {

    @Autowired
    private UsrRepo usrRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String v = username == null ? "" : username.trim().toLowerCase();
        AppUsr u = usrRepo.findByLoginNorm(v)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AuthUsr(u.getId(), u.getLoginNorm(), u.getPassHash());
    }
}
