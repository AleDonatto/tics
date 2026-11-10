package com.example.tics.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.http.HttpMethod
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.apply

@Configuration
class SecurityConfig(
    @Value("\${app.security.jwt.secret}")
    secretBase64: String,
) {

    private val secretKey: SecretKey = SecretKeySpec(Base64.getDecoder().decode(secretBase64),
        "HmacSHA256")
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = OctetSequenceKey.Builder(secretKey).build()
        val jwkSource =
            ImmutableJWKSet<SecurityContext>(JWKSet(jwk))

        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationConverter: JwtAuthenticationConverter): SecurityFilterChain {
        http
            .cors {  }
            .csrf{it.disable()}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                it.requestMatchers(
                    HttpMethod.GET,
                    "/api/users/me"
                ).authenticated()

                it.requestMatchers(
                    HttpMethod.GET,
                    "/api/roles"
                ).hasRole("admin")

                it.requestMatchers("/api/users", "/api/users/**")
                    .hasRole("admin")

                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt{ jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
            }}
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource{
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000/")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowedHeaders = listOf("Authorization","Content-Type")
            allowCredentials = true
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()

        converter.setJwtGrantedAuthoritiesConverter {
            jwt ->
            val rol = jwt.getClaimAsString("role")

            if(rol.isNullOrBlank()){
                emptyList()
            } else {
                listOf(SimpleGrantedAuthority("ROLE_$rol"))
            }
        }

        return converter
    }

}