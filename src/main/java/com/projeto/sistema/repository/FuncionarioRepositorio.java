package com.projeto.sistema.repository;

import com.projeto.sistema.models.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepositorio extends JpaRepository<Funcionario, Long> {

}
