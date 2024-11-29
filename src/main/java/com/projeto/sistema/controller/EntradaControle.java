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

//    @PostMapping("/salvarEntrada")
//    public ModelAndView salvar(String acao, Entrada entrada, ItemEntrada itemEntrada, BindingResult result) {
//        if (result.hasErrors()) {
//            return cadastrar(entrada, itemEntrada);
//        }
//
//        if(acao.equals("itens")){
//            this.listaItemEntrada.add(itemEntrada);
//            entrada.setValorTotal(entrada.getValorTotal() + (itemEntrada.getValor() * itemEntrada.getQuantidade()));
//            entrada.setQuantidadeTotal(entrada.getQuantidadeTotal()+ itemEntrada.getQuantidade());
//
//        }else if(acao.equals("salvar")){
//            entradaRepositorio.saveAndFlush(entrada);
//
//            for (ItemEntrada it: listaItemEntrada){
//                it.setEntrada(entrada);
//                itemEntradaRepositorio.saveAndFlush(it);
//
//                Optional<Produto> prod = produtoRepositorio.findById(it.getProduto().getId());
//            Produto produto = prod.get();
//            produto.setEstoque(produto.getEstoque() + it.getQuantidade());
//            produto.setPrecoVenda(it.getValor());
//            produto.setPrecoCusto(it.getValorCusto());
//            produtoRepositorio.saveAndFlush(produto);
//
//            this.listaItemEntrada = new ArrayList<>();
//
//
//
//            }
//            return cadastrar(new Entrada(), new ItemEntrada());
//
//        }
//        return cadastrar(entrada, new ItemEntrada());
//    }

    @PostMapping("/salvarEntrada")
    public ModelAndView salvar(String acao, Entrada entrada, ItemEntrada itemEntrada, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(entrada, itemEntrada);
        }

        if ("itens".equals(acao)) {
            // Adicionar item à lista
            listaItemEntrada.add(itemEntrada);
            entrada.setValorTotal(entrada.getValorTotal() + (itemEntrada.getValor() * itemEntrada.getQuantidade()));
            entrada.setQuantidadeTotal(entrada.getQuantidadeTotal() + itemEntrada.getQuantidade());

        } else if ("atualizarItem".equals(acao)) {
            // Atualizar item na lista
            listaItemEntrada = listaItemEntrada.stream()
                    .map(it -> it.getId().equals(itemEntrada.getId()) ? itemEntrada : it)
                    .toList();

        } else if ("salvar".equals(acao)) {
            entradaRepositorio.saveAndFlush(entrada);
            for (ItemEntrada it : listaItemEntrada) {
                it.setEntrada(entrada);
                itemEntradaRepositorio.saveAndFlush(it);
                Produto produto = produtoRepositorio.findById(it.getProduto().getId()).orElseThrow();
                produto.setQuantidade(produto.getQuantidade() + it.getQuantidade());
                produtoRepositorio.saveAndFlush(produto);
            }
            listaItemEntrada.clear();
            return cadastrar(new Entrada(), new ItemEntrada());
        }

        return cadastrar(entrada, new ItemEntrada());
    }

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
                    p.setQuantidade(p.getQuantidade() + item.getQuantidade()); // Repor a quantidade de volta no estoque
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


    @GetMapping("/editarItemEntrada/{id}")
    public ModelAndView editarItem(@PathVariable("id") Long id) {
        Optional<ItemEntrada> itemEntrada = itemEntradaRepositorio.findById(id);

        ModelAndView mv = new ModelAndView("administrativo/entradas/itemEntrada");
        mv.addObject("itemEntrada", itemEntrada.orElse(new ItemEntrada())); // Garantir que o objeto não seja nulo
        mv.addObject("listaProdutos", produtoRepositorio.findAll()); // Adicionar a lista de produtos
        mv.addObject("listaFuncionarios", funcionarioRepositorio.findAll()); // Adiciona a lista de funcionários
        mv.addObject("listaFornecedores", fornecedorRepositorio.findAll()); // Adiciona a lista de fornecedores

        return mv;
    }


    @PostMapping("/atualizarItemEntrada")
    public ModelAndView atualizarItemEntrada(ItemEntrada itemEntrada) {
        System.out.println("ItemEntrada ID: " + itemEntrada.getId());
        System.out.println("Produto: " + (itemEntrada.getProduto() != null ? itemEntrada.getProduto().getId() : "Produto nulo"));

        if (itemEntrada.getProduto() == null || itemEntrada.getProduto().getId() == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }

        Optional<ItemEntrada> itemExistente = itemEntradaRepositorio.findById(itemEntrada.getId());
        if (itemExistente.isPresent()) {
            ItemEntrada itemAtualizado = itemExistente.get();
            itemAtualizado.setQuantidade(itemEntrada.getQuantidade());
            itemAtualizado.setValor(itemEntrada.getValor());

            Produto produto = produtoRepositorio.findById(itemEntrada.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            itemAtualizado.setProduto(produto);

            itemEntradaRepositorio.saveAndFlush(itemAtualizado);
        }

        return listar(); // Retorna à página de lista com as alterações
    }




    @GetMapping("/removerItemEntrada/{id}")
    public ModelAndView removerItemEntrada(@PathVariable("id") Long id) {
        listaItemEntrada = listaItemEntrada.stream()
                .filter(it -> !it.getId().equals(id))
                .toList();
        return cadastrar(new Entrada(), new ItemEntrada());
    }






    public List<ItemEntrada> getListaItemEntrada() {
        return listaItemEntrada;
    }

    public void setListaItemEntrada(List<ItemEntrada> listaItemEntrada) {
        this.listaItemEntrada = listaItemEntrada;
    }
}
