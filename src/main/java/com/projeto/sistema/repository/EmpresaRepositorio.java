package com.projeto.sistema.repository;

import com.projeto.sistema.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
    // Pode adicionar métodos personalizados, se necessário
}