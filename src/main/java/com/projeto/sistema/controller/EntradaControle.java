package com.projeto.sistema.controller;


import com.projeto.sistema.models.*;
import com.projeto.sistema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class EntradaControle {

    @Autowired //faz a conexao com EntradaRepositorio.
    private EntradaRepositorio entradaRepositorio;

    @Autowired //faz a conexao com ItemEntradaRepositorio.
    private ItemEntradaRepositorio itemEntradaRepositorio;

    @Autowired // faz a conexao com ProdutoRepositorio.
    private ProdutoRepositorio produtoRepositorio;

    @Autowired // faz a conexao com FuncionarioRepositorio.
    private FuncionarioRepositorio funcionarioRepositorio;

    @Autowired // faz a conexao com FornecedorRepositorio.
    private FornecedorRepositorio fornecedorRepositorio;

    private List<ItemEntrada> listaItemEntrada = new ArrayList<ItemEntrada>();
    // essa linha superior cria uma lista temporaria dos itens para carregamento.

    @GetMapping("/cadastroEntrada")
    public ModelAndView cadastrar(Entrada entrada,ItemEntrada itemEntrada) {
        ModelAndView mv = new ModelAndView("administrativo/entradas/cadastro");
        mv.addObject("entrada", entrada);
        mv.addObject("itemEntrada",itemEntrada);
        mv.addObject("listaItemEntrada", this.listaItemEntrada);
        mv.addObject("listaFuncionarios",funcionarioRepositorio.findAll());
        mv.addObject("listaFornecedores",fornecedorRepositorio.findAll());
        mv.addObject("listaProdutos",produtoRepositorio.findAll());

        return mv;
    }

    @PostMapping("/salvarEntrada")
    public ModelAndView salvar(String acao, Entrada entrada, ItemEntrada itemEntrada, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(entrada, itemEntrada);
        }

        if(acao.equals("itens")){
            this.listaItemEntrada.add(itemEntrada);
            entrada.setValorTotal(entrada.getValorTotal() + (itemEntrada.getValor() * itemEntrada.getQuantidade()));
            entrada.setQuantidadeTotal(entrada.getQuantidadeTotal()+ itemEntrada.getQuantidade());

        }else if(acao.equals("salvar")){
            entradaRepositorio.saveAndFlush(entrada);

            for (ItemEntrada it: listaItemEntrada){
                it.setEntrada(entrada);
                itemEntradaRepositorio.saveAndFlush(it);

                Optional<Produto> prod = produtoRepositorio.findById(it.getProduto().getId());
            Produto produto = prod.get();
            produto.setEstoque(produto.getEstoque() + it.getQuantidade());
            produto.setPrecoVenda(it.getValor());
            produto.setPrecoCusto(it.getValorCusto());
            produtoRepositorio.saveAndFlush(produto);

            this.listaItemEntrada = new ArrayList<>();



            }
            return cadastrar(new Entrada(), new ItemEntrada());

        }
        return cadastrar(entrada, new ItemEntrada());
    }

    //Get salvar novo

//    @PostMapping("/salvarEntrada")
//    public ModelAndView salvar(String acao, Entrada entrada, ItemEntrada itemEntrada, BindingResult result) {
//        if (result.hasErrors()) {
//            return cadastrar(entrada, itemEntrada);
//        }
//
//        if (acao.equals("itens")) {
//            if (itemEntrada.getId() != null) {
//                // Atualiza o item de entrada existente na lista
//                listaItemEntrada = listaItemEntrada.stream()
//                        .map(it -> it.getId().equals(itemEntrada.getId()) ? itemEntrada : it)
//                        .toList();
//            } else {
//                // Adiciona novo item na lista
//                listaItemEntrada.add(itemEntrada);
//            }
//            entrada.setValorTotal(entrada.getValorTotal() + (itemEntrada.getValor() * itemEntrada.getQuantidade()));
//            entrada.setQuantidadeTotal(entrada.getQuantidadeTotal() + itemEntrada.getQuantidade());
//
//        } else if (acao.equals("salvar")) {
//            entradaRepositorio.saveAndFlush(entrada);
//
//            for (ItemEntrada it : listaItemEntrada) {
//                it.setEntrada(entrada);
//                itemEntradaRepositorio.saveAndFlush(it);
//
//                Optional<Produto> prod = produtoRepositorio.findById(it.getProduto().getId());
//                Produto produto = prod.get();
//                produto.setEstoque(produto.getEstoque() + it.getQuantidade());
//                produto.setPrecoVenda(it.getValor());
//                produto.setPrecoCusto(it.getValorCusto());
//                produtoRepositorio.saveAndFlush(produto);
//            }
//
//            listaItemEntrada = new ArrayList<>();
//            return cadastrar(new Entrada(), new ItemEntrada());
//        }
//
//        return cadastrar(entrada, new ItemEntrada());
//    }




//    @GetMapping("/editarEntrada/{id}")
//    public ModelAndView editar(@PathVariable("id") Long id) {
//        Optional<Entrada> entrada = entradaRepositorio.findById(id);
//       this.listaItemEntrada = itemEntradaRepositorio.buscarPorEntrada(id);
//        return cadastrar(entrada.get(), new ItemEntrada());
//    }

    //Get editar novo

    @GetMapping("/editarEntrada/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Entrada> entrada = entradaRepositorio.findById(id);
        if (entrada.isPresent()) {
            // Carregar os itens da entrada específica
            this.listaItemEntrada = itemEntradaRepositorio.buscarPorEntrada(id);

            ModelAndView mv = cadastrar(entrada.get(), new ItemEntrada());
            mv.addObject("editar", true); // Indica que estamos no modo de edição
            return mv;
        }

        return listar(); // Caso não encontre a entrada, retorna para a listagem
    }

    @GetMapping("/listarEntrada")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/entradas/lista");
        mv.addObject("listaEntrada", entradaRepositorio.findAll());
        return mv;
    }

//
//    @GetMapping("/removerEntrada/{id}")
//    public ModelAndView remover(@PathVariable("id") Long id) {
//        Optional<Entrada> entrada = entradaRepositorio.findById(id);
//        entradaRepositorio.delete(entrada.get());
//        return listar();
//    }


    @GetMapping("/removerEntrada/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Entrada> entrada = entradaRepositorio.findById(id);
        if (entrada.isPresent()) {
            // Remover os itens da entrada
            List<ItemEntrada> itensEntrada = itemEntradaRepositorio.buscarPorEntrada(id);
            for (ItemEntrada item : itensEntrada) {
                // Atualizar o estoque dos produtos
                Optional<Produto> produto = produtoRepositorio.findById(item.getProduto().getId());
                if (produto.isPresent()) {
                    Produto p = produto.get();
                    p.setEstoque(p.getEstoque() + item.getQuantidade()); // Repor a quantidade de volta no estoque
                    produtoRepositorio.saveAndFlush(p);
                }
                // Remover os itens da entrada
                itemEntradaRepositorio.delete(item);
            }

            // Remover a entrada
            entradaRepositorio.delete(entrada.get());
        }

        // Retornar à lista de entradas após a exclusão
        return listar();
    }

    public List<ItemEntrada> getListaItemEntrada() {
        return listaItemEntrada;
    }

    public void setListaItemEntrada(List<ItemEntrada> listaItemEntrada) {
        this.listaItemEntrada = listaItemEntrada;
    }
}
