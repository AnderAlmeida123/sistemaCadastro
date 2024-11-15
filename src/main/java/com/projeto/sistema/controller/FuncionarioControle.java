package com.projeto.sistema.controller;


import com.projeto.sistema.models.Funcionario;
import com.projeto.sistema.repository.FuncionarioRepositorio;
import com.projeto.sistema.repository.CidadeRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class FuncionarioControle {

    @Autowired //faz a conexao com funcionarioRepositorio.
    private FuncionarioRepositorio funcionarioRepositorio;

    @Autowired //faz a conexao com funcionarioRepositorio.
    private CidadeRepositorio cidadeRepositorio;


    @GetMapping("/cadastroFuncionario")
    public ModelAndView cadastrar(Funcionario funcionario) {
        ModelAndView mv = new ModelAndView("administrativo/funcionarios/cadastro");
        mv.addObject("funcionario", funcionario);
        mv.addObject("listaCidade", cidadeRepositorio.findAll());
        return mv;
    }

    @PostMapping("/salvarFuncionario")
    public ModelAndView salvar(Funcionario funcionario, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(funcionario);
        }
        funcionarioRepositorio.saveAndFlush(funcionario);

        return cadastrar(new Funcionario());
    }


    @GetMapping("/editarFuncionario/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Funcionario> funcionario = funcionarioRepositorio.findById(id);
        return cadastrar(funcionario.get());
    }


    @GetMapping("/listarFuncionario")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/funcionarios/lista");
        mv.addObject("listaFuncionario", funcionarioRepositorio.findAll());
        return mv;
    }


    @GetMapping("/removerFuncionario/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Funcionario> funcionario = funcionarioRepositorio.findById(id);
        funcionarioRepositorio.delete(funcionario.get());
        return listar();
    }


}
