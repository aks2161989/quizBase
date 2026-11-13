package com.example.quizBase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:4200", "http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/api/**",
                        "/dashboard/**",
                        "/quizzes/listallquizzes",
                        "/quizzes/create",
                        "/quizzes/createaquiz",
                        "/quizzes/edit/**",
                        "/quizzes/delete/**",
                        "/quizzes/answer/**",
                        "/quizzes/submit",
                        "/quizzes/your-quizzes",
                        "/categories/create/**"
                )
                .excludePathPatterns(
                        "/api/auth/**",
                        "/users/login",
                        "/users/signin",
                        "/users/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error"
                );
    }
}
