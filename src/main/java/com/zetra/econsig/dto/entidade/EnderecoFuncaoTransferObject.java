package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: EnderecoFuncaoTransferObject</p>
 * <p>Description: TransferObject da entidade permissoes (restrição de acesso IP/DDNS e descrição da função) </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 */
public class EnderecoFuncaoTransferObject extends CustomTransferObject {
    private static final long serialVersionUID = 74L;

    public EnderecoFuncaoTransferObject() {
        super();
    }

    public EnderecoFuncaoTransferObject(String funCodigo, String funDescricao) {
        this();
        setAttribute(Columns.FUN_CODIGO, funCodigo);
        setAttribute(Columns.FUN_DESCRICAO, funDescricao);
    }

    public EnderecoFuncaoTransferObject(String funCodigo, String funDescricao, String eafIpAcesso, String eafDdnsAcesso) {
        this();
        setAttribute(Columns.FUN_CODIGO, funCodigo);
        setAttribute(Columns.FUN_DESCRICAO, funDescricao);
        setAttribute(Columns.EAF_IP_ACESSO, eafIpAcesso);
        setAttribute(Columns.EAF_DDNS_ACESSO, eafDdnsAcesso);
    }

    // Getter
    public String getFunCodigo() {
        return (String) getAttribute(Columns.FUN_CODIGO);
    }

    public String getFunDescricao() {
        return (String) getAttribute(Columns.FUN_DESCRICAO);
    }

    public String getEafIpAcesso() {
        return (String) getAttribute(Columns.EAF_IP_ACESSO);
    }

    public String getEafDdnsAcesso() {
        return (String) getAttribute(Columns.EAF_DDNS_ACESSO);
    }

    // Setter
    public void setFunCodigo(String funCodigo) {
        setAttribute(Columns.FUN_CODIGO, funCodigo);
    }

    public void setFunDescricao(String funDescricao) {
        setAttribute(Columns.FUN_DESCRICAO, funDescricao);
    }

    public void setEafIpAcesso(String eafIpAcesso) {
        setAttribute(Columns.EAF_IP_ACESSO, eafIpAcesso);
    }

    public void setEafDdnsAcesso(String eafDdnsAcesso) {
        setAttribute(Columns.EAF_DDNS_ACESSO, eafDdnsAcesso);
    }

    @Override
    public String toString() {
        return getFunDescricao();
    }
}
