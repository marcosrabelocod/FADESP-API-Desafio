package desafio_pagamento.model;

import java.math.BigDecimal;

public class Pagamento {

    private Long id;
    private Integer codigoDebito;
    private String cpfCnpj;
    private String metodoPagamento;
    private String numeroCartao;
    private BigDecimal valor;
    private String status;
    private Boolean ativo;

    // Construtor padrão que já aplica a regra de negócio inicial do PDF
    public Pagamento() {
        this.status = "Pendente de Processamento";
        this.ativo = true;
    }

    //Getters e Setters para todos os atributos acima!

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id; }

    public Integer getCodigoDebito() { return codigoDebito; }
    public void setCodigoDebito(Integer codigoDebito) { this.codigoDebito = codigoDebito; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    
    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
