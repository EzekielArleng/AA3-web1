package br.ufscar.dc.dsw.com.gametester.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorDeSenha {
    public static void main(String[] args) {
        String senhaEmTextoPlano = "admin";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaComHash = encoder.encode(senhaEmTextoPlano);

        System.out.println("Senha em texto plano: " + senhaEmTextoPlano);
        System.out.println("Senha com Hash BCrypt: " + senhaComHash);
    }
}
