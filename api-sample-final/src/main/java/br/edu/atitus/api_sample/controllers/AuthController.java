package br.edu.atitus.api_sample.controllers;

// Imports que você já tinha
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import br.edu.atitus.api_sample.components.JWTUtils;
import br.edu.atitus.api_sample.dtos.SigninDTO;
import br.edu.atitus.api_sample.dtos.SignupDTO;
import br.edu.atitus.api_sample.dtos.TokenDTO;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.entities.UserType;
import br.edu.atitus.api_sample.services.UserService;

// Imports que eu adicionei para corrigir os erros
import org.springframework.security.authentication.AuthenticationManager; // <-- CORREÇÃO 1
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // --- CORREÇÃO 1: Faltava declarar estas variáveis como campos ---
    private final AuthenticationManager authenticationManager;
    private final JWTUtils JWTUtils;
    private final UserService userService;

    // O Construtor (Injeção de Dependências)
    public AuthController(AuthenticationConfiguration authConfig, JWTUtils jwtUtils, UserService userService) throws Exception {
        this.authenticationManager = authConfig.getAuthenticationManager();
        this.JWTUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * Endpoint para Cadastro (SignUp)
     */
    @PostMapping("/signup")
    public ResponseEntity<UserEntity> signup(@Valid @RequestBody SignupDTO signupDTO) {
        UserEntity newUser = new UserEntity();
        BeanUtils.copyProperties(signupDTO, newUser);
        newUser.setType(UserType.Common); 

        try {
            userService.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Endpoint para Login (SignIn)
     */
    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody SigninDTO signinDTO) {
        try {
            // 1. Tenta autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinDTO.email(), signinDTO.password()));

            // 2. Coloca o utilizador no contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. --- CORREÇÃO 2: O teu JWTUtils espera uma String (o email) ---
            // O authentication.getName() retorna o email do utilizador logado.
            String jwt = JWTUtils.generateToken(authentication.getName());

            // 4. "Empacota" o token no DTO que o frontend espera
            TokenDTO tokenDTO = new TokenDTO(jwt);
            
            return ResponseEntity.ok(tokenDTO);

        } catch (AuthenticationException e) {
            // Falha de autenticação (email ou senha errados)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}