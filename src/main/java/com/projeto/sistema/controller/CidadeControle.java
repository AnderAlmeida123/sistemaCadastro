package com.projeto.sistema.controller;


import com.projeto.sistema.models.Cidade;
import com.projeto.sistema.repository.CidadeRepositorio;
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
public class CidadeControle {

    @Autowired //faz a conexao com cidadeRepositorio.
    private CidadeRepositorio cidadeRepositorio;

    @Autowired //faz a conexao com cidadeRepositorio.
    private EstadoRepositorio estadoRepositorio;


    @GetMapping("/cadastroCidade")
    public ModelAndView cadastrar(Cidade cidade) {
        ModelAndView mv = new ModelAndView("administrativo/cidades/cadastro");
        mv.addObject("cidade", cidade);
        mv.addObject("listaEstado", estadoRepositorio.findAll());
        return mv;
    }

    @PostMapping("/salvarCidade")
    public ModelAndView salvar(Cidade cidade, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(cidade);
        }
        cidadeRepositorio.saveAndFlush(cidade);

        return cadastrar(new Cidade());
    }


    @GetMapping("/editarCidade/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Cidade> cidade = cidadeRepositorio.findById(id);
        return cadastrar(cidade.get());
    }


    @GetMapping("/listarCidade")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/cidades/lista");
        mv.addObject("listaCidade", cidadeRepositorio.findAll());
        return mv;
    }


    @GetMapping("/removerCidade/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Cidade> cidade = cidadeRepositorio.findById(id);
        cidadeRepositorio.delete(cidade.get());
        return listar();
    }


}
