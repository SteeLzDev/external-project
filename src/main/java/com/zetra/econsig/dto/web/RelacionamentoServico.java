package com.zetra.econsig.dto.web;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: RelacionamentoServico</p>
 * <p>Description: POJO para relacionamentos na edição de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelacionamentoServico {

    private String codigo;
    private String nome;
    private String chaveDescricao;
    private boolean desabilitado;
    private List<TransferObject> valores;

    public RelacionamentoServico(String codigo, String nome, String chaveDescricao, boolean desabilitado, List<TransferObject> valores) {
        super();
        this.codigo = codigo;
        this.nome = nome;
        this.chaveDescricao = chaveDescricao;
        this.desabilitado = desabilitado;
        this.valores = valores;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getChaveDescricao() {
        return chaveDescricao;
    }

    public void setChaveDescricao(String chaveDescricao) {
        this.chaveDescricao = chaveDescricao;
    }

    public boolean isDesabilitado() {
        return desabilitado;
    }

    public void setDesabilitado(boolean desabilitado) {
        this.desabilitado = desabilitado;
    }

    public List<TransferObject> getValores() {
        return valores;
    }

    public void setValores(List<TransferObject> valores) {
        this.valores = valores;
    }
}
