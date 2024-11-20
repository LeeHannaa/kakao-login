package com.example.kakaologin.filter;

import com.example.kakaologin.entity.User;
import com.example.kakaologin.exception.DoNotLoginException;
import com.example.kakaologin.exception.WrongTokenException;
import com.example.kakaologin.service.AuthService;
import com.example.kakaologin.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor // 자동으로 생성자 주입에 대한 코드를 생성
public class JwtTokenFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final String SECRET_KEY;

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/error")
                || request.getRequestURI().startsWith("/scrd/auth/")) { // 로그인 하려는 사용자
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
        if (authorizationHeader == null) throw new DoNotLoginException();

        System.out.println("authorizationHeader 확인 : " + authorizationHeader);

        // Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if (!authorizationHeader.startsWith("Bearer "))
            throw new WrongTokenException("Bearer 로 시작하지 않는 토큰입니다.");

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        System.out.println("'Bearer ' 뒷부분(Jwt Token) 추출 : " + token);

        User loginUser = authService.getLoginUser(JwtUtil.getUserId(token, SECRET_KEY));

        // loginUser 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginUser.getId(),
                        null,
                        List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        filterChain.doFilter(request, response);
    }
}
