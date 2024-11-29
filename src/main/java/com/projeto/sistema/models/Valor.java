package com.projeto.sistema.models;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Valor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double entrada = 0.00;
    private Double valorvenda = 0.00;

    @ManyToOne
    private Produto produto;

    @ManyToOne
    private Venda venda;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getEntrada() {
        return entrada;
    }

    public void setEntrada(Double entrada) {
        this.entrada = entrada;
    }

    public Double getValorvenda() {
        return valorvenda;
    }

    public void setValorvenda(Double valorvenda) {
        this.valorvenda = valorvenda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
}
