package com.zetra.econsig.webservice.command.saida;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RegistroRespostaRequisicaoExterna</p>
 * <p>Description: classe que representa o tipo de registro de resposta à requisição externa,
 *                 com seus respectivos parâmetros.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroRespostaRequisicaoExterna {
    protected CamposAPI nome;
    protected Map<CamposAPI, Object> atributos;
    //lista definindo ordem de parâmetros na visualização final
    protected List<CamposAPI> keyOrder;

    public CamposAPI getNome() {
        return nome;
    }

    public void setNome(CamposAPI nome) {
        this.nome = nome;
    }

    public Map<CamposAPI, Object> getAtributos() {
        return atributos;
    }

    public void setAtributos(Map<CamposAPI, Object> atributos) {
        this.atributos = atributos;
    }

    public void addAtributo(CamposAPI chave, Object valor) {
        if (atributos == null) {
            atributos = new HashMap<>();
        }

        if (keyOrder == null) {
            keyOrder = new ArrayList<>();
        }

        if (!keyOrder.contains(chave)) {
            keyOrder.add(chave);
        }

        atributos.put(chave, valor);
    }

    public List<CamposAPI> getKeyOrder() {
        return keyOrder;
    }

    public void setKeyOrder(List<CamposAPI> keyOrder) {
        this.keyOrder = keyOrder;
    }
}
