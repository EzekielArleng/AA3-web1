-- Senha para todos é '123456'
INSERT INTO usuarios (nome, email, senha, tipo_perfil) VALUES ('Admin Principal', 'admin@gametester.com', '$2a$10$Abc123...HashParaAdmin...', 'ROLE_ADMINISTRADOR');
INSERT INTO usuarios (nome, email, senha, tipo_perfil) VALUES ('Yohan Duarte', 'yohan@gametester.com', '$2a$10$Xyz456...HashParaTestador1...', 'ROLE_TESTADOR');
INSERT INTO usuarios (nome, email, senha, tipo_perfil) VALUES ('André Endo', 'andre@gametester.com', '$2a$10$Qwe789...HashParaTestador2...', 'ROLE_TESTADOR');

INSERT INTO estrategias (nome, descricao, exemplos, dicas) VALUES ('Estratégia de Empatia', 'Testar o jogo do ponto de vista de um jogador iniciante.', 'Como um jogador que nunca jogou isso reagiria ao tutorial?', 'Evite pular diálogos.');