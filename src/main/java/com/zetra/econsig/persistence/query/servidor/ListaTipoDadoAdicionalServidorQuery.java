package com.zetra.econsig.persistence.query.servidor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ListaTipoDadoAdicionalServidorQuery</p>
 * <p>Description: Listagem de tipos de dados adicionais de servidor</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26247 $
 * $Date: 2019-02-19 09:44:20 -0200 (qui, 14 fev 2019) $
 */
public class ListaTipoDadoAdicionalServidorQuery extends HQuery {

    public AcessoSistema responsavel;

    public AcaoTipoDadoAdicionalEnum acao;

    public VisibilidadeTipoDadoAdicionalEnum visibilidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("tda.tdaCodigo, ");
        corpo.append("tda.tdaDescricao, ");
        corpo.append("tda.tdaExporta, ");
        corpo.append("tda.tdaSupConsulta, ");
        corpo.append("tda.tdaCseConsulta, ");
        corpo.append("tda.tdaCsaConsulta, ");
        corpo.append("tda.tdaSerConsulta, ");
        corpo.append("tda.tdaSupAltera, ");
        corpo.append("tda.tdaCseAltera, ");
        corpo.append("tda.tdaCsaAltera, ");
        corpo.append("tda.tdaSerAltera, ");
        corpo.append("tda.tdaDominio, ");
        corpo.append("tda.tipoEntidade.tenCodigo ");

        corpo.append(" from TipoDadoAdicional tda ");

        corpo.append(" where 1 = 1 ");

        List<String> visibilidadeTda = new ArrayList<>();
        if (!TextHelper.isNull(visibilidade)) {
            visibilidadeTda = visibilidade.getVisibilidade();
        }

        if (!TextHelper.isNull(acao) && !TextHelper.isNull(visibilidadeTda)) {
            switch (acao) {
                case EXPORTA:
                    corpo.append(" and tda.tdaExporta").append(criaClausulaNomeada("visibilidade", CodedValues.TDA_SIM));
                    break;

                case CONSULTA:
                    if (responsavel.isSup()) {
                        corpo.append(" and tda.tdaSupConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isCseOrg()) {
                        corpo.append(" and tda.tdaCseConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isCsaCor()) {
                        corpo.append(" and tda.tdaCsaConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isSer()) {
                        corpo.append(" and tda.tdaSerConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else {
                        throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    break;

                case ALTERA:
                    if (responsavel.isSup()) {
                        corpo.append(" and tda.tdaSupAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isCseOrg()) {
                        corpo.append(" and tda.tdaCseAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isCsaCor()) {
                        corpo.append(" and tda.tdaCsaAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else if (responsavel.isSer()) {
                        corpo.append(" and tda.tdaSerAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));
                    } else {
                        throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    break;

                default:
                    throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
            }
        }

        // FILTRANDO PELO TIPO_ENTIDADE = SERVIDOR
        corpo.append(" and tda.tipoEntidade.tenCodigo = '6'");

        corpo.append(" order by tda.tdaOrdenacao ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(acao) && !TextHelper.isNull(visibilidadeTda)) {
            switch (acao) {
                case EXPORTA:
                    defineValorClausulaNomeada("visibilidade", CodedValues.TDA_SIM, query);
                    break;

                case CONSULTA:
                    defineValorClausulaNomeada("visibilidade", visibilidadeTda, query);
                    break;

                case ALTERA:
                    defineValorClausulaNomeada("visibilidade", visibilidadeTda, query);
                    break;

                default:
                    throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        final String[] fields = new String[] {
                Columns.TDA_CODIGO,
                Columns.TDA_DESCRICAO,
                Columns.TDA_EXPORTA,
                Columns.TDA_SUP_CONSULTA,
                Columns.TDA_CSE_CONSULTA,
                Columns.TDA_CSA_CONSULTA,
                Columns.TDA_SER_CONSULTA,
                Columns.TDA_SUP_ALTERA,
                Columns.TDA_CSE_ALTERA,
                Columns.TDA_CSA_ALTERA,
                Columns.TDA_SER_ALTERA,
                Columns.TDA_DOMINIO,
                Columns.TDA_TEN_CODIGO
        };

        return fields;
    }
}
