package com.projeto.sistema.controller;


import com.projeto.sistema.models.Fornecedor;
import com.projeto.sistema.repository.CidadeRepositorio;
import com.projeto.sistema.repository.FornecedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class FornecedorControle {

    @Autowired //faz a conexao com fornecedorRepositorio.
    private FornecedorRepositorio fornecedorRepositorio;

    @Autowired //faz a conexao com fornecedorRepositorio.
    private CidadeRepositorio cidadeRepositorio;


    @GetMapping("/cadastroFornecedor")
    public ModelAndView cadastrar(Fornecedor fornecedor) {
        ModelAndView mv = new ModelAndView("administrativo/fornecedores/cadastro");
        mv.addObject("fornecedor", fornecedor);
        mv.addObject("listaCidade", cidadeRepositorio.findAll());
        return mv;
    }

    @PostMapping("/salvarFornecedor")
    public ModelAndView salvar(Fornecedor fornecedor, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(fornecedor);
        }
        fornecedorRepositorio.saveAndFlush(fornecedor);

        return cadastrar(new Fornecedor());
    }


    @GetMapping("/editarFornecedor/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Fornecedor> fornecedor = fornecedorRepositorio.findById(id);
        return cadastrar(fornecedor.get());
    }


    @GetMapping("/listarFornecedor")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/fornecedores/lista");
        mv.addObject("listaFornecedor", fornecedorRepositorio.findAll());
        return mv;
    }


    @GetMapping("/removerFornecedor/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Fornecedor> fornecedor = fornecedorRepositorio.findById(id);
        fornecedorRepositorio.delete(fornecedor.get());
        return listar();
    }


}
