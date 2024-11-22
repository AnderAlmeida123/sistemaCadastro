package com.projeto.sistema.controller;


import com.projeto.sistema.models.Venda;
import com.projeto.sistema.models.ItemVenda;
import com.projeto.sistema.models.Produto;
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
public class VendaControle {

    @Autowired //faz a conexao com VendaRepositorio.
    private VendaRepositorio vendaRepositorio;

    @Autowired //faz a conexao com ItemVendaRepositorio.
    private ItemVendaRepositorio itemVendaRepositorio;

    @Autowired // faz a conexao com ProdutoRepositorio.
    private ProdutoRepositorio produtoRepositorio;

    @Autowired // faz a conexao com FuncionarioRepositorio.
    private FuncionarioRepositorio funcionarioRepositorio;

    @Autowired // faz a conexao com ClienteRepositorio.
    private ClienteRepositorio clienteRepositorio;

    private List<ItemVenda> listaItemVenda = new ArrayList<ItemVenda>();
    // essa linha superior cria uma lista temporaria dos itens para carregamento.

    @GetMapping("/cadastroVenda")
    public ModelAndView cadastrar(Venda venda, ItemVenda itemVenda) {
        ModelAndView mv = new ModelAndView("administrativo/vendas/cadastro");
        mv.addObject("venda", venda);
        mv.addObject("itemVenda", itemVenda);
        mv.addObject("listaItemVenda", this.listaItemVenda);
        mv.addObject("listaFuncionarios", funcionarioRepositorio.findAll());
        mv.addObject("listaClientes", clienteRepositorio.findAll());
        mv.addObject("listaProdutos", produtoRepositorio.findAll());

        return mv;
    }

    @PostMapping("/salvarVenda")
    public ModelAndView salvar(String acao, Venda venda, ItemVenda itemVenda, BindingResult result) {
        if (result.hasErrors()) {
            return cadastrar(venda, itemVenda);
        }

        if (acao.equals("itens")) {
            itemVenda.setValor(itemVenda.getProduto().getPrecoVenda());
            itemVenda.setSubtotal(itemVenda.getProduto().getPrecoVenda() * itemVenda.getQuantidade());

            venda.setValorTotal(venda.getValorTotal() + (itemVenda.getValor() * itemVenda.getQuantidade()));
            venda.setQuantidadeTotal(venda.getQuantidadeTotal() + itemVenda.getQuantidade());

            this.listaItemVenda.add(itemVenda);

        } else if (acao.equals("salvar")) {
            vendaRepositorio.saveAndFlush(venda);

            for (ItemVenda it : listaItemVenda) {
                it.setVenda(venda);
//                it.setSubtotal(it.getValor() * it.getQuantidade());
                itemVendaRepositorio.saveAndFlush(it);

                Optional<Produto> prod = produtoRepositorio.findById(it.getProduto().getId());
                Produto produto = prod.get();
                produto.setEstoque(produto.getEstoque() - it.getQuantidade());
                produto.setPrecoVenda(it.getValor());
//            produto.setPrecoCusto(it.getValorCusto());
                produtoRepositorio.saveAndFlush(produto);

                this.listaItemVenda = new ArrayList<>();


            }
            return cadastrar(new Venda(), new ItemVenda());

        }
        return cadastrar(venda, new ItemVenda());
    }


        @GetMapping("/editarVenda/{id}")
    public ModelAndView editar(@PathVariable("id") Long id) {
        Optional<Venda> venda = vendaRepositorio.findById(id);
        this.listaItemVenda = itemVendaRepositorio.buscarPorVenda(id);
        return cadastrar(venda.get(), new ItemVenda());
    }


    @GetMapping("/listarVenda")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("administrativo/vendas/lista");
        mv.addObject("listaVenda", vendaRepositorio.findAll());
        return mv;
    }

//
//    @GetMapping("/removerVenda/{id}")
//    public ModelAndView remover(@PathVariable("id") Long id) {
//        Optional<Venda> venda = vendaRepositorio.findById(id);
//        vendaRepositorio.delete(venda.get());
//        return listar();
//    }

    public List<ItemVenda> getListaItemVenda() {
        return listaItemVenda;
    }

    public void setListaItemVenda(List<ItemVenda> listaItemVenda) {
        this.listaItemVenda = listaItemVenda;
    }
}
