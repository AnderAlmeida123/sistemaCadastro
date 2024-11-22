package com.projeto.sistema.repository;

import com.projeto.sistema.models.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemVendaRepositorio extends JpaRepository<ItemVenda, Long> {

    @Query("SELECT e FROM ItemVenda e WHERE e.venda.id = ?1")

    List<ItemVenda> buscarPorVenda(long id);
}
