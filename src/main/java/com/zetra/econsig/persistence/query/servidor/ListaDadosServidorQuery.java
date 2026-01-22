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
 * <p>Title: ListaDadosServidorQuery</p>
 * <p>Description: Listagem de dados do servidor</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26247 $
 * $Date: 2020-04-16 09:44:20 -0200 (qui, 14 fev 2019) $
 */
public class ListaDadosServidorQuery extends HQuery {

    public AcessoSistema responsavel;

    public AcaoTipoDadoAdicionalEnum acao;

    public VisibilidadeTipoDadoAdicionalEnum visibilidade;

    public String serCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.isCor() ? responsavel.getCodigoEntidadePai() : null;

        final StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("tda.tdaCodigo, ");
        corpo.append("tda.tdaDescricao, ");
        corpo.append("das.dasValor, ");
        corpo.append("tpe.tenCodigo");

        corpo.append(" FROM DadosServidor das ");

        corpo.append(" INNER JOIN das.tipoDadoAdicional tda");
        corpo.append(" INNER JOIN tda.tipoEntidade tpe");
        corpo.append(" left outer join tda.consignatariaPermiteTdaSet cpt with cpt.csaCodigo = :csaCodigo ");
        corpo.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append("and coalesce(cpt.cptExibe, 'S') <> 'N' ");
            corpo.append("and (cpt.tdaCodigo is not null or not exists ( ");
            corpo.append("select 1 from ConsignatariaPermiteTda cpt2 ");
            corpo.append("where das.tdaCodigo = cpt2.tdaCodigo ");
            corpo.append(")) ");
        }

        List<String> visibilidadeTda = new ArrayList<>();
        if (!TextHelper.isNull(visibilidade)) {
            visibilidadeTda = visibilidade.getVisibilidade();
        }

        if (!TextHelper.isNull(acao) && !TextHelper.isNull(visibilidadeTda)) {
            switch (acao) {
                case EXPORTA:
                    corpo.append(" and das.tipoDadoAdicional.tdaExporta");
                    break;

                case CONSULTA:
                    if (responsavel.isSup()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaSupConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isCseOrg()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaCseConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isCsaCor()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaCsaConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isSer()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaSerConsulta").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else {
                        throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    break;

                case ALTERA:
                    if (responsavel.isSup()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaSupAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isCseOrg()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaCseAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isCsaCor()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaCsaAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else if (responsavel.isSer()) {
                        corpo.append(" and das.tipoDadoAdicional.tdaSerAltera").append(criaClausulaNomeada("visibilidade", visibilidadeTda));

                    } else {
                        throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    break;

                default:
                    throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
            }
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpo.append(" AND das.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        corpo.append(" ORDER BY tda.tdaOrdenacao ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

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

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        } else {
            defineValorClausulaNomeada("csaCodigo", "_NULL_", query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TDA_CODIGO,
                Columns.TDA_DESCRICAO,
                Columns.DAS_VALOR,
                Columns.TDA_TEN_CODIGO
        };
    }

}
