package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ListaDadosAutorizacaoQuery</p>
 * <p>Description: Listagem de de Dados de Autorizacao</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaDadosAutorizacaoQuery extends HQuery {

    public String tdaCodigo;
    public AcessoSistema responsavel;
    public VisibilidadeTipoDadoAdicionalEnum visibilidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

    	// Visibilidade é obrigatória
    	if (visibilidade == null){
    		throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
    	}
        List<String> visibilidadeList = visibilidade.getVisibilidade();

        StringBuilder corpo = new StringBuilder();

        corpo.append(" select ");
        corpo.append(" dad.adeCodigo, ");
        corpo.append(" tda.tdaCodigo, ");
        corpo.append(" tda.tdaDescricao, ");
        corpo.append(" dad.dadValor ");
        corpo.append(" from DadosAutorizacaoDesconto dad ");
        corpo.append(" inner join dad.tipoDadoAdicional tda ");
        corpo.append(" where 1=1 ");

        if (responsavel.isSup()) {
            corpo.append(" and tda.tdaSupConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeList));
        } else if (responsavel.isCseOrg()) {
            corpo.append(" and tda.tdaCseConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeList));
        } else if (responsavel.isCsaCor()) {
            corpo.append(" and tda.tdaCsaConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeList));
        } else if (responsavel.isSer()) {
            corpo.append(" and tda.tdaSerConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeList));
        } else {
            throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
        }

        if (!TextHelper.isNull(tdaCodigo)) {
            corpo.append(" and dad.tdaCodigo ").append(criaClausulaNomeada("tdaCodigo", tdaCodigo));
        }

        corpo.append(" order by tda.tdaOrdenacao");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("visibilidade", visibilidadeList, query);

        if (!TextHelper.isNull(tdaCodigo)) {
            defineValorClausulaNomeada("tdaCodigo", tdaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DAD_ADE_CODIGO,
                Columns.TDA_CODIGO,
                Columns.TDA_DESCRICAO,
                Columns.DAD_VALOR
        };
    }
}
