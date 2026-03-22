package desafio_pagamento.repository;

import desafio_pagamento.model.Pagamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PagamentoRepository{
    
    private final JdbcTemplate jdbcTemplate;

    public PagamentoRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1 salvar o novo pagamento com o gerador de ID do H2
    public Pagamento salvar(Pagamento pagamento){
        String sql = "INSET INTO tb_pagamento (codigo_debito, cpf_cnpj, metodo_pagamento, numero_cartao, valor, status, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pagamento.getCodigoDebito());
            ps.setString(2, pagamento.getCpfCnpj());
            ps.setString(3, pagamento.getMetodoPagamento());
            ps.setString(4, pagamento.getNumeroCartao());
            ps.setBigDecimal(5, pagamento.getValor());
            ps.setString(6, pagamento.getStatus());
            ps.setBoolean(7, pagamento.getAtivo());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            pagamento.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        }
        return pagamento;
    }

    // 2 Buscar por ID (Apenas os ativos)
    public Optional<Pagamento> buscarPorId(Long id) {
        String sql = "SELECT * FROM tb_pagamento WHERE id = ? AND ativo = true";
        try {
            Pagamento pagamento = jdbcTemplate.queryForObject(sql, new PagamentoRowMapper(), id);
            return Optional.ofNullable(pagamento);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //3 Atualizar Status
    public void atualizarStatus(Long id, String novoStatus){
        String sql = "UPDATE tb_pagamento SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, novoStatus, id);
    }

    // 4. Exclusão Lógica (Inativar em vez de apagar do banco)
    public void inativar(Long id) {
        String sql = "UPDATE tb_pagamento SET ativo = false WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 5. Listar com Filtros Exigidos
    public List<Pagamento> listaComFiltros(Integer codigoDebito, String cpfCnpj, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tb_pagamento WHERE ativo = true");
        List<Object> parametros = new ArrayList<>();

        // Se o utilizador enviou o código, adicionamos à query
        if (codigoDebito != null) {
            sql.append(" AND codigo_debito = ?");
            parametros.add(codigoDebito);
        }
        // Se o utilizador enviou o CPF/CNPJ, adicionamos
        if (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) {
            sql.append(" AND cpf_cnpj = ?");
            parametros.add(cpfCnpj);
        }
        // Se o utilizador enviou o status, adicionamos
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            parametros.add(status);
        }

        return jdbcTemplate.query(sql.toString(), new PagamentoRowMapper(), parametros.toArray());
    }

    //6 MApeador das linhas do banco de dados para codigo java
    private static class PagamentoRowMapper implements RowMapper<Pagamento>{
        @Override
        public Pagamento mapRow(ResultSet rs, int rowNun) throws SQLException {
            Pagamento p = new Pagamento();
            p.setId(rs.getLong("id"));
            p.setCodigoDebito(rs.getInt("codigo_debito"));
            p.setCpfCnpj(rs.getString("cpf_cnpj"));
            p.setMetodoPagamento(rs.getString("metodo_pagamento"));
            p.setNumeroCartao(rs.getString("numero_cartao"));
            p.setValor(rs.getBigDecimal("valor"));
            p.setStatus(rs.getString("status"));
            p.setAtivo(rs.getBoolean("ativo"));
            return p;
        }
    }
}