Desafio tecnico Fadesp - Api de pagamento
API REST desenvolvida em Java e Spring Boot para o recebimento e processamento de pagamentos, contemplando regras de negócio rigorosas (Máquina de Estados) e persistência de dados em memória.

############################
TECNOLOGIAS
Java 17
Spring Boot 3.x
Spring Data JDBC (JdbcTemplate)
Banco de Dados H2 (Embutido/In-Memory)
Maven

##################
modo de execução
abra a pasta do projeto e de o comando 
./mvnw spring-boot:run

OU

rode o arquivo src/main/java/desafio-pagamento/DesafioPagamentoApplication.java


#############################
Endpoints da API (Controller)

Criar pagamneto 

{
    POST http://localhost:8080/pagamentos
Content-Type: application/json

{
    "codigoDebito": Cod,
    "cpfCnpj": "Nº",
    "metodoPagamento": "forma de pagamento ("boleto", "pix", "cartao_credito", "cartao_debito")",
    "valor": $
}
}

Listar pagamentos 
GET http://localhost:8080/pagamentos

Listar com filtro
GET http://localhost:8080/pagamentos?status=Processado com Sucesso

Exclusão logica de pagamento 
DELETE http://localhost:8080/pagamentos/3

mais exemplos no arquivo teste.http

#############################
Observações
Para este trabalho equilibrei o maximo entre codigo limpo e boas praticas e não criar muitas complexidades 
uma opção minha que verão nesse codigo é o uso de Spring Data JDBC pela minha preferencia em usar SQL puro
torço por um feedback positivo, Obrigado pela atenção