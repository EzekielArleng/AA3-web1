UPDATE usuarios
SET
    nome = 'Admin Principal Atualizado',
    senha = '123456',
    tipo_perfil = 'ADMINISTRADOR' -- Corrigindo para o padrão do Spring Security
WHERE
    email = 'admin@gametester.com'; -- A cláusula WHERE é essencial!