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
    private final JwtUtil jwtUtil;
    private final String SECRET_KEY;

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/error")
                || request.getRequestURI().startsWith("/scrd/auth/") // 로그인 하려는 사용자 패쓰
                || request.getRequestURI().startsWith("/scrd/every")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
        if (authorizationHeader == null) throw new DoNotLoginException();

        // Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if (!authorizationHeader.startsWith("Bearer "))
            throw new WrongTokenException("Bearer 로 시작하지 않는 토큰입니다.");

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        // refreshToken 확인
        String refreshToken = null;
        String refreshTokenHeader = request.getHeader("X-Refresh-Token");
        System.out.println("refreshToken 검증 시작!!!!!!!! " + refreshTokenHeader);
        // refreshToken = null이 아니거나, 비어있지 않다면 검증
        if (refreshTokenHeader != null && !refreshTokenHeader.isEmpty()) {
            // X-Refresh-Token 헤더에서 리프레시 토큰 값을 바로 가져옴
            refreshToken = request.getHeader("X-Refresh-Token");
            System.out.println("refreshToken 확인 : " + refreshToken);
        }

        User loginUser = null;
        if(refreshToken == null){ // accessToken 만료되지 않을 경우
            loginUser = authService.getLoginUser(JwtUtil.getUserId(token, SECRET_KEY));

        } else { // accessToken 만료 + refreshToken 같이 받아서 refreshToken 검증 후 refreshToken이랑 accessToken 둘 다 재발급
            // refresh 토큰 검증 및 access/refresh 토큰 발급
            System.out.println("accessToken 만료!!!!!");
            List<String> newTokens = jwtUtil.validateRefreshToken(refreshToken, SECRET_KEY);

            // 응답 헤더에 access token, refresh token 전달
            response.setHeader("Authorization", "Bearer " + newTokens.get(0)); // Access Token
            response.setHeader("X-Refresh-Token", newTokens.get(1));


            // User 객체에 새로운 액세스 토큰으로 ID 찾아오기
            loginUser = authService.getLoginUser(JwtUtil.getUserId(newTokens.get(0),SECRET_KEY));
        }

        // loginUser 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginUser.getId(), // 인증 주체
                        null,
                        List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        System.out.println("사용자에게 권한 부여");
        filterChain.doFilter(request, response);
    }
}
