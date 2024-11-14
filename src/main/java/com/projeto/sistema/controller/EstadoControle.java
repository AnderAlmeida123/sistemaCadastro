package com.projeto.sistema.controller;


import com.projeto.sistema.models.Estado;
import com.projeto.sistema.repository.EstadoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class EstadoControle {

    @Autowired //faz a conexao com estadoRepositorio.
    private EstadoRepositorio estadoRepositorio;

    @GetMapping("/cadastroEstado")
    public ModelAndView cadastrar(Estado estado) {
        ModelAndView mv = new ModelAndView("administrativo/estados/cadastro");
        mv.addObject("estado", estado);

        return mv;
    }



    @PostMapping("/salvarEstado")
    public ModelAndView salvar(Estado estado, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(estado);
        }
        estadoRepositorio.saveAndFlush(estado);

        return cadastrar(new Estado());
    }


    @GetMapping("/editarEstado/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Estado> estado = estadoRepositorio.findById(id);
        return cadastrar(estado.get());
    }

    @GetMapping("/listarEstado")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/estados/lista");
        mv.addObject("listaEstado", estadoRepositorio.findAll());
        return mv;
    }

    @GetMapping("/removerEstado/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Estado> estado = estadoRepositorio.findById(id);
        estadoRepositorio.delete(estado.get());
        return listar();
    }


}
