CREATE TABLE tb_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_debito INT NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL,
    metodo_pagamento VARCHAR(20) NOT NULL,
    numero_cartao VARCHAR(20),
    valor DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);