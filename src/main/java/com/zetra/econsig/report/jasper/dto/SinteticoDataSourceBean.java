package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;

/**
 * @author rodrigo
 *
 */
public class SinteticoDataSourceBean implements Serializable {

    String nome;
    Long valor;
    String periodo;

    public SinteticoDataSourceBean(String nome, Long valor) {
        super();
        this.nome = nome;
        this.valor = valor;
    }
    public SinteticoDataSourceBean(String nome, Long valor, String periodo) {
        super();
        this.nome = nome;
        this.valor = valor;
        this.periodo = periodo;
    }

    public SinteticoDataSourceBean(String nome, String periodo) {
        super();
        this.nome = nome;
        this.periodo = periodo;
    }


    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public Long getValor() {
        return valor;
    }
    public void setValor(Long valor) {
        this.valor = valor;
    }
    public String getPeriodo() {
        return periodo;
    }
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }


//    @Override
//    public boolean equals(Object bean){
//        SinteticoDataSourceBean dataBean = (SinteticoDataSourceBean) bean;
//        if(dataBean.getNome().equalsIgnoreCase(nome) &&
//           dataBean.getPeriodo().equalsIgnoreCase(periodo) ){
//            return true;
//        }
//        else{
//            return false;
//        }
//    }

}
