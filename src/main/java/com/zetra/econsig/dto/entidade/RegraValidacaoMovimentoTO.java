package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegraValidacaoMovimentoTO</p>
 * <p>Description: Transfer Object da Regra Validacao Movimento</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoMovimentoTO extends CustomTransferObject {

    public RegraValidacaoMovimentoTO() {
        super();
    }

    public RegraValidacaoMovimentoTO(String rvmCodigo) {
        this();
        setAttribute(Columns.RVM_CODIGO, rvmCodigo);
    }

    public RegraValidacaoMovimentoTO(TransferObject to) {
        this();
        setAtributos(to.getAtributos());
    }

    // Getter
    public String getRvmCodigo() {
        return (String) getAttribute(Columns.RVM_CODIGO);
    }

    public String getRvmIdentificador() {
        return (String) getAttribute(Columns.RVM_IDENTIFICADOR);
    }

    public String getRvmDescricao() {
        return (String) getAttribute(Columns.RVM_DESCRICAO);
    }

    public Boolean getRvmAtivo() {
        return (Boolean) getAttribute(Columns.RVM_ATIVO);
    }

    public String getRvmJavaClassName() {
        return (String) getAttribute(Columns.RVM_JAVA_CLASS_NAME);
    }

    public Integer getRvmSequencia() {
        return (Integer) getAttribute(Columns.RVM_SEQUENCIA);
    }

    public Boolean getRvmInvalidaMovimento() {
        return (Boolean) getAttribute(Columns.RVM_INVALIDA_MOVIMENTO);
    }

    public String getRvmLimiteErro() {
        return (String) getAttribute(Columns.RVM_LIMITE_ERRO);
    }

    public String getRvmLimiteAviso() {
        return (String) getAttribute(Columns.RVM_LIMITE_AVISO);
    }

    // Setter
    public void setRvmIdentificador(String rvmIdentificador) {
        setAttribute(Columns.RVM_IDENTIFICADOR, rvmIdentificador);
    }

    public void setRvmDescricao(String rvmDescricao) {
        setAttribute(Columns.RVM_DESCRICAO, rvmDescricao);
    }

    public void setRvmAtivo(Boolean rvmAtivo) {
        setAttribute(Columns.RVM_ATIVO, rvmAtivo);
    }

    public void setRvmJavaClassName(String rvmJavaClassName) {
        setAttribute(Columns.RVM_JAVA_CLASS_NAME, rvmJavaClassName);
    }

    public void setRvmSequencia(Integer rvmSequencia) {
        setAttribute(Columns.RVM_SEQUENCIA, rvmSequencia);
    }

    public void setRvmInvalidaMovimento(Boolean rvmInvalidaMovimento) {
        setAttribute(Columns.RVM_INVALIDA_MOVIMENTO, rvmInvalidaMovimento);
    }

    public void setRvmLimiteErro(String rvmLimiteErro) {
        setAttribute(Columns.RVM_LIMITE_ERRO, rvmLimiteErro);
    }

    public void setRvmLimiteAviso(String rvmLimiteAviso) {
        setAttribute(Columns.RVM_LIMITE_AVISO, rvmLimiteAviso);
    }
}
