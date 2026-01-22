package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import com.zetra.econsig.helper.log.Log;
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
 * <p>Title: ListaTipoDadoAdicionalQuery</p>
 * <p>Description: Listagem de tipos de dados adicionais</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoDadoAdicionalQuery extends HQuery {

    public AcessoSistema responsavel;
    public AcaoTipoDadoAdicionalEnum acao;
    public VisibilidadeTipoDadoAdicionalEnum visibilidade;
    public String svcCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
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
        corpo.append("tda.tipoEntidade.tenCodigo, ");
        corpo.append("coalesce(spt.sptExibe, 'S'), ");
        corpo.append("coalesce(cpt.cptExibe, 'S') ");

        corpo.append("from TipoDadoAdicional tda ");
        corpo.append("left outer join tda.servicoPermiteTdaSet spt with spt.svcCodigo = :svcCodigo ");
        corpo.append("left outer join tda.consignatariaPermiteTdaSet cpt with cpt.csaCodigo = :csaCodigo ");

        // DESENV-6937: se é ação de alterar via Web, filtra pelas funções que tem acesso ao tipo de dado definidas na tb_funcoes_editaveis_tda
        if (visibilidade == VisibilidadeTipoDadoAdicionalEnum.WEB && acao == AcaoTipoDadoAdicionalEnum.ALTERA && !TextHelper.isNull(responsavel.getFunCodigo())) {
            corpo.append("left outer join tda.funcaoEditavelTdaSet fet with fet.funCodigo = :funCodigo ");
        }

        corpo.append("where 1 = 1 ");

        // DESENV-10490: se o serviço foi informado, lista apenas os tipos de dados que podem ser exibidos para o serviço, ou seja,
        // aqueles tipos de dados que não tem restrição de serviço, ou quando tem, está habilitado para o serviço
        if (!TextHelper.isNull(svcCodigo)) {
            corpo.append(" AND tda.tenCodigo IN ('").append(TextHelper.joinWithEscapeSql(Log.ALTERA_CONSIGNACAO, "' , '")).append("')");
            corpo.append("and coalesce(spt.sptExibe, 'S') <> 'N' ");
            corpo.append("and (spt.tdaCodigo is not null or not exists ( ");
            corpo.append("select 1 from tda.servicoPermiteTdaSet spt2 ");
            corpo.append("where spt2.sptExibe <> 'N' ");
            corpo.append(")) ");
        }
        // DESENV-14337: se a consignatária foi informada, lista apenas os tipos de dados que podem ser exibidos para a consignataria, ou seja,
        // aqueles tipos de dados que não tem restrição de consignatária, ou quando tem, está habilitado para a consignatária
        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append("and coalesce(cpt.cptExibe, 'S') <> 'N' ");
            corpo.append("and (cpt.tdaCodigo is not null or not exists ( ");
            corpo.append("select 1 from tda.consignatariaPermiteTdaSet cpt2 ");
            corpo.append("where cpt2.cptExibe <> 'N' ");
            corpo.append(")) ");
        }

        List<String> visibilidadeTda = visibilidade.getVisibilidade();

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
                if (responsavel.isSup() || responsavel.isSistema()) {
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

                if (visibilidade == VisibilidadeTipoDadoAdicionalEnum.WEB && !TextHelper.isNull(responsavel.getFunCodigo())) {
                    corpo.append(" and ((fet.funCodigo is NULL and ");
                    corpo.append("(select count(*) from FuncaoEditavelTda ftda where ftda.tdaCodigo = tda.tdaCodigo) = 0) ");
                    corpo.append(" OR fet.funCodigo is not NULL) ");
                }
                break;

            default:
                throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
        }

        corpo.append(" order by tda.tdaOrdenacao ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        } else {
            // Caso o serviço não tenha sido informado, define a cláusula com algum valor inválido
            // para que o left join não traga nenhum registro adicional
            defineValorClausulaNomeada("svcCodigo", "_NULL_", query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        } else {
            // Caso a consignatária não tenha sido informado, define a cláusula com algum valor inválido
            // para que o left join não traga nenhum registro adicional
            defineValorClausulaNomeada("csaCodigo", "_NULL_", query);
        }

        switch (acao) {
            case EXPORTA:
                defineValorClausulaNomeada("visibilidade", CodedValues.TDA_SIM, query);
                break;

            case CONSULTA:
            case ALTERA:
                defineValorClausulaNomeada("visibilidade", visibilidadeTda, query);
                if (visibilidade == VisibilidadeTipoDadoAdicionalEnum.WEB && acao == AcaoTipoDadoAdicionalEnum.ALTERA && !TextHelper.isNull(responsavel.getFunCodigo())) {
                    defineValorClausulaNomeada("funCodigo", responsavel.getFunCodigo(), query);
                }
                break;

            default:
                throw new HQueryException("mensagem.usoIncorretoSistema", responsavel);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
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
                Columns.TDA_TEN_CODIGO,
                Columns.SPT_EXIBE,
                Columns.CPT_EXIBE
        };
    }
}
