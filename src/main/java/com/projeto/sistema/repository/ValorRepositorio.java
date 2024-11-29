package com.projeto.sistema.repository;

import com.projeto.sistema.models.Valor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValorRepositorio extends JpaRepository<Valor, Long> {
    Optional<Valor> findByProdutoId(Long produtoId); // Busca o valor de venda associado ao produto

}
