package com.projeto.sistema.service;


import com.projeto.sistema.models.ItemVenda;
import com.projeto.sistema.repository.ItemVendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemVendaService {

        @Autowired
        private ItemVendaRepositorio repository;

        public ItemVenda findById(Long id) {
            return repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        }

        public void save(ItemVenda itemVenda) {
            repository.save(itemVenda); // Atualiza ou salva o item
        }
    }

