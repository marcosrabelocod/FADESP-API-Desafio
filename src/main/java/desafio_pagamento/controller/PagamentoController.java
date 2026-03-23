package desafio_pagamento.controller;

import desafio_pagamento.dto.PagamentoRequestDTO;
import desafio_pagamento.model.Pagamento;
import desafio_pagamento.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService){
        this.pagamentoService = pagamentoService;
    }

    //1 Rota para receber um novo pagamento(Post)
    @PostMapping
    public ResponseEntity<?> receberPagamento(@RequestBody PagamentoRequestDTO dto){
        try{
            //Passa o DTO para o Service validar e guardar
            Pagamento pagamentoSalvo = pagamentoService.receberPagamento(dto);
            return ResponseEntity.ok(pagamentoSalvo);
        } catch (IllegalArgumentException e){
            //se a validação do cartão credito ou debito falahar, erro 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //2 Rota para atualizar o status (Put)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> payload){
        try{
            String novoStatus = payload.get("status");
            if (novoStatus == null || novoStatus.trim().isEmpty()){
                return ResponseEntity.badRequest().body("O campo 'status' é obrigatório no JSON.");
            }

            // Tenta atualizar. Se as regras da Máquina de Estados falharem, o Service lança uma exceção.
            pagamentoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok("Status do pagamento " + id + " atualizar para: " + novoStatus);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Apanha os erros das regras de negócio (Ex: tentar alterar um sucesso) e devolve Erro 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //3 Rota para a exclusão lógica (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativarPAgamento(@PathVariable Long id){
        try{
            pagamentoService.inativarPagamento(id);
            return ResponseEntity.ok("Exclusão lógica do pagamento realizada com sucesso.");
        } catch(IllegalArgumentException | IllegalStateException e){
            // Apanha o erro se tentar apagar algo que já não está Pendente
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 4 Rota para listar pagamentos com os filtros dinâmicos (GET)
    @GetMapping
    public ResponseEntity<List<Pagamento>> ListaPagamento(
        @RequestParam(required = false) Integer codigoDebito,
        @RequestParam(required = false) String cpfCnpj,
        @RequestParam(required = false) String status) {
        // O @RequestParam permite que a URL receba parâmetros como: 
        // /pagamentos?status=Pendente de Processamento&codigoDebito=123
            List<Pagamento> pagamentos = pagamentoService.listaPagamento(codigoDebito, cpfCnpj, status);
            return ResponseEntity.ok(pagamentos);
        }
}
