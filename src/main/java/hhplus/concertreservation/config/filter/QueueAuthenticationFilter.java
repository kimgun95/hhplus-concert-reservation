package hhplus.concertreservation.config.filter;

import hhplus.concertreservation.app.application.service.QueueService;
import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueueRank;
import hhplus.concertreservation.config.exception.ErrorCode;
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

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private String filterProcessesUrl = "/seat/**";
    private final QueueService queueService;

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
            QueueRank rank = queueService.queryToken(token);
            if (rank.getRank().equals(QueueStatus.WAITING)) throw new FailException(ErrorCode.CONFLICT_TOKEN_ERROR);

        } catch (FailException e) {
            log.info("대기열 인증 토큰이 없거나 대기열 순번이 아닙니다: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("토큰 인증에 실패했습니다.");
        }
    }

}
