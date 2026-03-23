package desafio_pagamento.service;

import desafio_pagamento.dto.PagamentoRequestDTO;
import desafio_pagamento.model.Pagamento;
import desafio_pagamento.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;

@Service
public class PagamentoService {
    public final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository){
        this.pagamentoRepository = pagamentoRepository;
    }

    //1 Receber e validar novo pagamento
    public Pagamento receberPagamento(PagamentoRequestDTO dto){
        // 1. Validação do Método de Pagamento (A Lista VIP)
        List<String> metodosValidos = Arrays.asList("boleto", "pix", "cartao_credito", "cartao_debito");
        String metodo = dto.getMetodoPagamento();
        
        if (metodo == null || !metodosValidos.contains(metodo.toLowerCase())) {
            throw new IllegalArgumentException("Método de pagamento inválido. Aceitos: boleto, pix, cartao_credito ou cartao_debito.");
        }

        // 2. Validação exigida no PDF: Número do cartão só para crédito ou débito
        if ((metodo.equals("cartao_credito") || metodo.equals("cartao_debito")) && 
            (dto.getNumeroCartao() == null || dto.getNumeroCartao().trim().isEmpty())) {
            throw new IllegalArgumentException("Número do cartão é obrigatório para pagamentos com cartão.");
        }

        //Validação de numero do cartão somente para as opções credito ou debito
        if ((metodo.equals("cartao_credito") || metodo.equals("cartao_debito"))&&
            (dto.getNumeroCartao() == null || dto.getNumeroCartao().trim().isEmpty())){
            throw new IllegalArgumentException("Numero do cartão é obrigatorio para pagamento com cartão");
        }

        // Converte o DTO para a entidade (Model)
        Pagamento pagamento = new Pagamento();
        pagamento.setCodigoDebito(dto.getCodigoDebito());
        pagamento.setCpfCnpj(dto.getCpfCnpj());
        pagamento.setMetodoPagamento(dto.getMetodoPagamento());
        pagamento.setNumeroCartao(dto.getNumeroCartao());
        pagamento.setValor(dto.getValor());

        //o status "Pendente = True" já é colocadono construtor do pagamento

        return pagamentoRepository.salvar(pagamento);
    }

    //2 Função que atualiza os Status (Maquina de de Estados)
    public void atualizarStatus(Long id, String novoStatus) {
        Pagamento pagamento = pagamentoRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado ou inativo"));

        String statusAtual = pagamento.getStatus();

        //Regra C: Quando Processado com sucesso, não pode ser alterado.
        if (statusAtual.equals("Processado com Sucesso")){
            throw new IllegalStateException("Um pagamento já processado com sucesso não pode ter o seu status alterado.");
        }

        // Regra D: Quando Processado com Falha, SÓ PODE ir para Pendente de Processamento.
        if (statusAtual.equals("Processado com Falha")){
            if(!novoStatus.equals("Pendente de Processamento")){
                throw new IllegalStateException("Pagamentos com falha apenas podem voltar para 'Pendente de Processamento'.");
            }
        }

        // Regra B: Quando Pendente de Processamento, pode ir para Sucesso ou Falha.
        if (statusAtual.equals("Pendente de Processamento")){
            if(!novoStatus.equals("Processado com sSucesso") && !novoStatus.equals("Processado com Falha")) {
                throw new IllegalStateException("Status inválido. De Pendente, apenas pode ir para Sucesso ou Falha.");    
            }
        }

        // Se passou por todas as barreiras de segurança, atualizamos no banco
        pagamentoRepository.atualizarStatus(id, novoStatus);
    }

    // 3. Exclusão Lógica (Inativar)
    public void inativarPagamento(Long id){
        Pagamento pagamento = pagamentoRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
    
        // Regra 4: A exclusão lógica apenas pode ocorrer se estiver Pendente de Processamento
        if (!pagamento.getStatus().equals("Pendente de Processamento")){
            throw new IllegalStateException("Apenas pagamento com status 'Pendente de Processamento' podem ser excuidos.");
        }
        pagamentoRepository.inativar(id);
    }

    //4 Lista com Filtros
    public List<Pagamento> listaPagamento(Integer codigoDebito, String cpfCnpj, String status){
        return pagamentoRepository.listaComFiltros(codigoDebito, cpfCnpj, status);
    }
}
