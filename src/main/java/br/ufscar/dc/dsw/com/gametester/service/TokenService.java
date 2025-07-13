package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Injeta o segredo do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;


    public String gerarToken(Usuario usuario) {
        try {
            // Define o algoritmo de assinatura usando o nosso segredo
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            // Cria e assina o token
            return JWT.create()
                    .withIssuer(issuer) // Emissor do token
                    .withSubject(usuario.getEmail()) // "Dono" do token (identificador do usuário)
                    .withExpiresAt(dataExpiracao()) // Define a data de expiração
                    .sign(algoritmo); // Assina com o algoritmo
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida um token JWT e retorna o "dono" (subject) se o token for válido.
     * @param tokenJWT O token a ser validado.
     * @return O e-mail do usuário se o token for válido, caso contrário, uma string vazia.
     */
    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(issuer)
                    .build()
                    .verify(tokenJWT) // Tenta verificar o token
                    .getSubject(); // Se for válido, retorna o subject (e-mail)
        } catch (JWTVerificationException exception){
            // Se o token for inválido (expirado, assinatura incorreta, etc.), retorna vazio.
            return "";
        }
    }

    /**
     * Calcula a data de expiração do token.
     * @return Um Instant representando o momento da expiração (ex: 2 horas a partir de agora).
     */
    private Instant dataExpiracao() {
        // Define o token para expirar em 2 horas, no fuso horário de Brasília (-3)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}