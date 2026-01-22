package com.zetra.econsig.dto.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: GrupoParametroServico</p>
 * <p>Description: POJO para grupos de parâmetros na edição de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GrupoParametroServico {

    private String descricao;
    private final List<ParametroServico> parametros = new ArrayList<ParametroServico>();

    public GrupoParametroServico() {
    }

    public GrupoParametroServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ParametroServico> getParametros() {
        return Collections.unmodifiableList(parametros);
    }

    public void addParametros(ParametroServico parametro) {
        parametros.add(parametro);
    }

}
