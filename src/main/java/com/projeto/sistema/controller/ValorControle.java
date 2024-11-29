package com.projeto.sistema.controller;


import com.projeto.sistema.models.Valor;
import com.projeto.sistema.repository.ProdutoRepositorio;
import com.projeto.sistema.repository.ValorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ValorControle {

    @Autowired //faz a conexao com valorRepositorio.
    private ValorRepositorio valorRepositorio;
    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @GetMapping("/cadastroValor")
    public ModelAndView cadastrar(Valor valor) {
        ModelAndView mv = new ModelAndView("administrativo/valores/cadastro");
        mv.addObject("valor", valor);
        mv.addObject("listaProdutos", produtoRepositorio.findAll());

        return mv;
    }



    @PostMapping("/salvarValor")
    public ModelAndView salvar(Valor valor, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(valor);
        }
        valorRepositorio.saveAndFlush(valor);

        return cadastrar(new Valor());
    }


    @GetMapping("/editarValor/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Valor> valor = valorRepositorio.findById(id);
        return cadastrar(valor.get());
    }

    @GetMapping("/listarValor")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/valores/lista");
        mv.addObject("listaValor", valorRepositorio.findAll());
        return mv;
    }

    @GetMapping("/removerValor/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Valor> valor = valorRepositorio.findById(id);
        valorRepositorio.delete(valor.get());
        return listar();
    }


}
