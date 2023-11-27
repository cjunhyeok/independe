//package community.independe.config.handler;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Slf4j
//public class LogInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("url : {}", request.getRequestURL());
//        log.info("uri : {}", request.getRequestURI());
//        return true;
//    }
//}