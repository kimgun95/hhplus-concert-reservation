package hhplus.concertreservation.config;

import hhplus.concertreservation.app.domain.checker.QueueChecker;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
import hhplus.concertreservation.config.exception.FailException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class QueueAuthenticationFilter extends OncePerRequestFilter {

    private final QueueRepository queueRepository;
    private final QueueChecker queueChecker;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private String filterProcessesUrl = "/seat/**";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !pathMatcher.match(filterProcessesUrl, path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader("AuthorizationQueue");
        log.info("대기열 인증 토큰의 헤더값: {}", token);

        try {
            Queue queue = queueChecker.getOrThrowIfNotFound(queueRepository.findByToken(token));
            queueChecker.checkActiveOrThrow(queue);

            filterChain.doFilter(request, response);
        } catch (FailException e) {
            log.info("대기열 인증 토큰이 없거나 대기열 순번이 아닙니다: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("토큰 인증에 실패했습니다.");
        }
    }

}
