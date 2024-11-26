package com.projeto.sistema.controller;

import com.projeto.sistema.models.Empresa;
import com.projeto.sistema.repository.EmpresaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmpresaControle {

    @Autowired
    private EmpresaRepositorio empresaRepositorio;

    @GetMapping("/cadastroEmpresa")
    public ModelAndView cadastrar(Empresa empresa) {
        ModelAndView mv = new ModelAndView("administrativo/empresas/cadastro");
        mv.addObject("empresa", empresa);
        return mv;
    }

    @PostMapping("/salvarEmpresa")
    public ModelAndView salvar(Empresa empresa) {
        empresaRepositorio.save(empresa);
        return new ModelAndView("redirect:/listarEmpresa");
    }

    @GetMapping("/listarEmpresa")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/empresas/lista");
        mv.addObject("listaEmpresas", empresaRepositorio.findAll());
        return mv;
    }
}
