package com.example.contentservice.security;

import com.example.contentservice.jwt.JwtUtil;
import com.example.contentservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String userId = jwtUtil.getUserInfoFromToken(authToken);

        return jwtUtil.validateToken(authToken)
                .flatMap((valid)->{
                   if(valid){
                       return userDetailsService.findByUsername(userId)
                               .map(userDetails -> {
                                   return new UsernamePasswordAuthenticationToken(
                                           userDetails,
                                           null,
                                           userDetails.getAuthorities());});
                   }
                   else return null;
                });
    }
}
