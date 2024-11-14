package com.projeto.sistema.repository;

import com.projeto.sistema.models.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepositorio extends JpaRepository<Cidade, Long> {

}
