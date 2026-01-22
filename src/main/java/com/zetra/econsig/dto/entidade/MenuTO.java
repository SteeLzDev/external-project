package com.zetra.econsig.dto.entidade;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MenuTO</p>
 * <p>Description: Transfer Object de menu.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MenuTO extends CustomTransferObject {

    private List<ItemMenuTO> itens;

    public MenuTO() {
        super();
    }

    public MenuTO(Integer mnuCodigo) {
        this();
        setAttribute(Columns.MNU_CODIGO, mnuCodigo);
    }

    public MenuTO(MenuTO menu) {
        this();
        setAtributos(menu.getAtributos());
        setItens(menu.getItens());
    }

    public Integer getMnuCodigo() {
        return (Integer) getAttribute(Columns.MNU_CODIGO);
    }

    public void setMnuCodigo(Integer mnuCodigo) {
        setAttribute(Columns.MNU_CODIGO, mnuCodigo);
    }

    public String getMnuDescricao() {
        return (String) getAttribute(Columns.MNU_DESCRICAO);
    }

    public void setMnuDescricao(String mnuDescricao) {
        setAttribute(Columns.MNU_DESCRICAO, mnuDescricao);
    }

    public Short getMnuAtivo() {
        return (Short) getAttribute(Columns.MNU_ATIVO);
    }

    public void setMnuAtivo(Short mnuAtivo) {
        setAttribute(Columns.MNU_ATIVO, mnuAtivo);
    }

    public Short getMnuSequencia() {
        return (Short) getAttribute(Columns.MNU_SEQUENCIA);
    }

    public void setMnuSequencia(Short mnuSequencia) {
        setAttribute(Columns.MNU_SEQUENCIA, mnuSequencia);
    }

    public String getMnuImagem() {
        return (String) getAttribute(Columns.MNU_IMAGEM);
    }

    public void setMnuImagem(String mnuImagem) {
        setAttribute(Columns.MNU_IMAGEM, mnuImagem);
    }

    public List<ItemMenuTO> getItens() {
        if (itens == null) {
            itens = new ArrayList<ItemMenuTO>();
        }
        return itens;
    }

    public void setItens(List<ItemMenuTO> itens) {
        this.itens = itens;
    }
}
