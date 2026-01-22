package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.values.Columns;

import lombok.EqualsAndHashCode;

/**
 * <p>Title: FormularioPesquisaTO</p>
 * <p>Description: Transfer Object da tabela de Formulário de Pesquisa</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@EqualsAndHashCode(callSuper=false)
public class FormularioPesquisaTO extends CustomTransferObject {

    public FormularioPesquisaTO() {
        super();
    }

    public FormularioPesquisaTO(String fpeCodigo) {
        this();
        setAttribute(Columns.FPE_CODIGO, fpeCodigo);
    }

    public FormularioPesquisaTO(FormularioPesquisaTO formularioPesquisaTO) {
        this();
        setAtributos(formularioPesquisaTO.getAtributos());
    }
    
    public FormularioPesquisaTO(FormularioPesquisa formularioPesquisa){
        this();
        setFpeCodigo(formularioPesquisa.getFpeCodigo());
        setFpeNome(formularioPesquisa.getFpeNome());
        setFpeBloqueiaSistema(formularioPesquisa.getFpeBloqueiaSistema());
        setFpeDtCriacao(formularioPesquisa.getFpeDtCriacao());
        setFpeDtFim(formularioPesquisa.getFpeDtFim());
        setFpeJson(formularioPesquisa.getFpeJson());
        setFpePublicado(formularioPesquisa.getFpePublicado());
    }

    public String getFpeCodigo() {
        return (String) getAttribute(Columns.FPE_CODIGO);
    }

    public void setFpeCodigo(String fpeCodigo) {
        setAttribute(Columns.FPE_CODIGO, fpeCodigo);
    }

    public String getFpeNome() {
        return (String) getAttribute(Columns.FPE_NOME);
    }

    public void setFpeNome(String fpeNome) {
        setAttribute(Columns.FPE_NOME, fpeNome);
    }

    public Boolean isFpeBloqueiaSistema() {
        return (Boolean) getAttribute(Columns.FPE_BLOQUEIA_SISTEMA);
    }

    public void setFpeBloqueiaSistema(Boolean fpeBloqueiaSistema) {
        setAttribute(Columns.FPE_BLOQUEIA_SISTEMA, fpeBloqueiaSistema);
    }

    public Date getFpeDtCriacao() {
        return (Date) getAttribute(Columns.FPE_DT_CRIACAO);
    }

    public void setFpeDtCriacao(Date fpeDtCriacao) {
        setAttribute(Columns.FPE_DT_CRIACAO, fpeDtCriacao);
    }

    public Date getFpeDtFim() {
        return (Date) getAttribute(Columns.FPE_DT_FIM);
    }

    public void setFpeDtFim(Date fpeDtFim) {
        setAttribute(Columns.FPE_DT_FIM, fpeDtFim);
    }

    public Boolean isFpePublicado() {
        return (Boolean) getAttribute(Columns.FPE_PUBLICADO);
    }

    public void setFpePublicado(Boolean fpePublicado) {
        setAttribute(Columns.FPE_PUBLICADO, fpePublicado);
    }

    public String getFpeJson() {
        return (String) getAttribute(Columns.FPE_JSON);
    }

    public void setFpeJson(String fpeJson){
        setAttribute(Columns.FPE_JSON, fpeJson);
    }
}
