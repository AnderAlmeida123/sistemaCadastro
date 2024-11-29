package com.projeto.sistema.controller;


import com.projeto.sistema.models.Empresa;
import com.projeto.sistema.models.Venda;
import com.projeto.sistema.models.ItemVenda;
import com.projeto.sistema.models.Valor;
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

    @Autowired // faz a conexao com ClienteRepositorio.
    private EmpresaRepositorio empresaRepositorio;

    @Autowired
    private ValorRepositorio valorRepositorio;

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
            // Busca o valor de venda na tabela Valor
            Optional<Valor> valorOptional = valorRepositorio.findByProdutoId(itemVenda.getProduto().getId());
            if (valorOptional.isPresent()) {
                Valor valor = valorOptional.get();
                double valorVenda = (valor.getValorvenda() != null) ? valor.getValorvenda() : 0.0; // Valor padrão de 0.0
                itemVenda.setValor(valorVenda);
                itemVenda.setSubtotal(itemVenda.getValor() * itemVenda.getQuantidade());
                venda.setValorTotal(venda.getValorTotal() + itemVenda.getSubtotal());
                venda.setQuantidadeTotal(venda.getQuantidadeTotal() + itemVenda.getQuantidade());
                this.listaItemVenda.add(itemVenda);
            } else {
                throw new RuntimeException("Valor de venda do produto não encontrado.");
            }

        } else if (acao.equals("salvar")) {
            vendaRepositorio.saveAndFlush(venda);

            for (ItemVenda it : listaItemVenda) {
                it.setVenda(venda); // Relaciona o item com a venda
                itemVendaRepositorio.saveAndFlush(it); // Salva o item

                // Atualiza o estoque do produto
                Optional<Produto> prod = produtoRepositorio.findById(it.getProduto().getId());
                Produto produto = prod.orElseThrow(() -> new RuntimeException("Produto não encontrado"));
                produto.setQuantidade(produto.getQuantidade() - it.getQuantidade());
                produtoRepositorio.saveAndFlush(produto);

                // Limpa a lista de itens de venda após salvar
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


    @GetMapping("/editarItemVenda/{id}")
    public ModelAndView editarItem(@PathVariable("id") Long id) {
        Optional<ItemVenda> itemVenda = itemVendaRepositorio.findById(id);

        ModelAndView mv = new ModelAndView("administrativo/vendas/itemVenda");
        mv.addObject("itemVenda", itemVenda.orElse(new ItemVenda())); // Garantir que o objeto não seja nulo
        mv.addObject("listaProdutos", produtoRepositorio.findAll()); // Adicionar a lista de produtos
        mv.addObject("listaFuncionarios", funcionarioRepositorio.findAll()); // Adiciona a lista de funcionários
        mv.addObject("listaClientes", clienteRepositorio.findAll()); // Adiciona a lista de clientes

        return mv;
    }


    @PostMapping("/atualizarItemVenda")
    public ModelAndView atualizarItemVenda(ItemVenda itemVenda) {
        System.out.println("ItemVenda ID: " + itemVenda.getId());
        System.out.println("Produto: " + (itemVenda.getProduto() != null ? itemVenda.getProduto().getId() : "Produto nulo"));

        if (itemVenda.getProduto() == null || itemVenda.getProduto().getId() == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }

        Optional<ItemVenda> itemExistente = itemVendaRepositorio.findById(itemVenda.getId());
        if (itemExistente.isPresent()) {
            ItemVenda itemAtualizado = itemExistente.get();
            itemAtualizado.setQuantidade(itemVenda.getQuantidade());
            itemAtualizado.setValor(itemVenda.getValor());
            itemAtualizado.setSubtotal(itemVenda.getQuantidade() * itemVenda.getValor());

            Produto produto = produtoRepositorio.findById(itemVenda.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            itemAtualizado.setProduto(produto);

            itemVendaRepositorio.saveAndFlush(itemAtualizado);
        }

        return listar(); // Retorna à página de lista com as alterações
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

    @GetMapping("/removerVenda/{id}")
    public ModelAndView remover(@PathVariable("id") Long id) {
        Optional<Venda> venda = vendaRepositorio.findById(id);
        if (venda.isPresent()) {
            // Remover os itens da venda
            List<ItemVenda> itensVenda = itemVendaRepositorio.buscarPorVenda(id);
            for (ItemVenda item : itensVenda) {
                // Atualizar o estoque dos produtos
                Optional<Produto> produto = produtoRepositorio.findById(item.getProduto().getId());
                if (produto.isPresent()) {
                    Produto p = produto.get();
                    p.setQuantidade(p.getQuantidade() + item.getQuantidade()); // Repor a quantidade de volta no estoque
                    produtoRepositorio.saveAndFlush(p);
                }
                // Remover os itens da venda
                itemVendaRepositorio.delete(item);
            }

            // Remover a venda
            vendaRepositorio.delete(venda.get());
        }

        // Retornar à lista de vendas após a exclusão
        return listar();
    }

// esse remover item remove aquele item que esta na tela principal, logo apos que vc adiciona, antes de salvar e enviar para o db

    @GetMapping("/removerItemVenda/{id}")
    public ModelAndView removerItem(@PathVariable("id") Long id) {
        // Encontra o item da venda a ser removido pela ID
        ItemVenda itemVenda = listaItemVenda.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (itemVenda != null) {
            // Remove o item da lista temporária
            listaItemVenda.remove(itemVenda);
        }

        // Retorna à página de cadastro, com a lista atualizada
        return cadastrar(new Venda(), new ItemVenda());
    }


    @GetMapping("/imprimirVenda/{id}")
    public ModelAndView imprimirVenda(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("administrativo/vendas/impressao");
        Optional<Venda> venda = vendaRepositorio.findById(id);
        if (venda.isPresent()) {
            mv.addObject("venda", venda.get());
            mv.addObject("itensVenda", itemVendaRepositorio.buscarPorVenda(id));
        }
        return mv;
    }

    public List<ItemVenda> getListaItemVenda() {
        return listaItemVenda;
    }

    public void setListaItemVenda(List<ItemVenda> listaItemVenda) {
        this.listaItemVenda = listaItemVenda;
    }
}
