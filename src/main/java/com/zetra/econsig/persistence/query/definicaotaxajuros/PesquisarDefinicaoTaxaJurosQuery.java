package com.zetra.econsig.persistence.query.definicaotaxajuros;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PesquisarDefinicaoTaxaJurosQuery</p>
 * <p>Description: Pesquisa de definição de regra de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26246 $
 * $Date: 2019-04-10 10:34:49 -0200 (qua, 10 abr 2019) $
 */
public class PesquisarDefinicaoTaxaJurosQuery extends HQuery {

    public String csaCodigo;
    public String orgCodigo;
    public String svcCodigo;
    public String funCodigo;
    public String statusRegra = null;
    public String data = null;
    public int offset = -1;
    public int maxResults = -1;
    public boolean count = false;
    public boolean pesquisaComDataVigenciaFim = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if (!count) {
            corpo.append(" select dtj.dtjCodigo, ");
            corpo.append(" dtj.dtjDataVigenciaIni, ");
            corpo.append(" dtj.dtjDataVigenciaFim, ");
            corpo.append(" dtj.orgao.orgCodigo, ");
            corpo.append(" org.orgNome as org_nome, ");
            corpo.append(" svc.svcDescricao as svc_descricao, ");
            corpo.append(" dtj.servico.svcCodigo, ");
            corpo.append(" dtj.dtjFaixaEtariaIni, ");
            corpo.append(" dtj.dtjFaixaEtariaFim, ");
            corpo.append(" dtj.dtjFaixaMargemIni, ");
            corpo.append(" dtj.dtjFaixaMargemFim, ");
            corpo.append(" dtj.dtjFaixaTempServicoIni, ");
            corpo.append(" dtj.dtjFaixaTempServicoFim, ");
            corpo.append(" dtj.dtjFaixaValorContratoIni, ");
            corpo.append(" dtj.dtjFaixaValorContratoFim, ");
            corpo.append(" dtj.dtjFaixaValorTotalIni, ");
            corpo.append(" dtj.dtjFaixaValorTotalFim, ");
            corpo.append(" dtj.dtjFaixaPrazoIni, ");
            corpo.append(" dtj.dtjFaixaPrazoFim, ");
            corpo.append(" dtj.dtjFaixaSalarioIni, ");
            corpo.append(" dtj.dtjFaixaSalarioFim, ");
            corpo.append(" dtj.dtjTaxaJuros, ");
            corpo.append(" dtj.dtjTaxaJurosMinima, ");
            corpo.append(" dtj.funCodigo, ");
            corpo.append(" fun.funDescricao as fun_descricao, ");
            corpo.append(" csa.csaCodigo as csa_codigo, ");
            corpo.append(" csa.csaNomeAbrev as csa_nome_abrev, ");
            corpo.append(" csa.csaNome as csa_nome ");

        } else {
            corpo.append(" select count(*) as total ");
        }

        corpo.append(" from DefinicaoTaxaJuros dtj ");
        corpo.append(" left join dtj.orgao org ");
        corpo.append(" left join dtj.funcao fun ");
        corpo.append(" inner join dtj.servico svc ");
        corpo.append(" inner join dtj.consignataria csa ");
        corpo.append(" where 1 = 1 ");

        if (!TextHelper.isNull(svcCodigo)) {
            corpo.append(" and dtj.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append(" and dtj.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append(" and (dtj.orgao is null or dtj.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(") ");
        }

        if (!pesquisaComDataVigenciaFim) {
            corpo.append(" and dtj.dtjDataVigenciaFim is null");
        }

        if (!TextHelper.isNull(statusRegra)) {
            switch (statusRegra) {

                case CodedValues.REGRA_NOVA_TABELA_INICIADA:

                    corpo.append("and dtj.dtjDataVigenciaIni is null ");
                    corpo.append("and dtj.dtjDataVigenciaFim is null ");

                    break;

                case CodedValues.REGRA_TABELA_ATIVA:

                    corpo.append("and dtj.dtjDataVigenciaIni is not null ");
                    corpo.append("and dtj.dtjDataVigenciaFim is null ");

                    break;

                case CodedValues.REGRA_TABELA_VIGENCIA_EXPIRADA:

                    corpo.append("and dtj.dtjDataVigenciaIni is not null ");
                    corpo.append("and dtj.dtjDataVigenciaFim is not null ");

                    break;

            }
        }

        if (!TextHelper.isNull(data)) {
            corpo.append("and :data between to_date(dtj.dtjDataVigenciaIni) and to_date(dtj.dtjDataVigenciaFim) ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(data)) {
            try {
                Date dataParam = DateHelper.parse(data, LocaleHelper.getDatePattern());
                defineValorClausulaNomeada("data", dataParam, query);
            } catch (java.text.ParseException ex) {
                throw new HQueryException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
            }
        }

        if (offset != -1) {
            query.setFirstResult(offset);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DTJ_CODIGO,
                Columns.DTJ_DATA_VIGENCIA_INI,
                Columns.DTJ_DATA_VIGENCIA_FIM,
                Columns.DTJ_ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.SVC_DESCRICAO,
                Columns.DTJ_SVC_CODIGO,
                Columns.DTJ_FAIXA_ETARIA_INI,
                Columns.DTJ_FAIXA_ETARIA_FIM,
                Columns.DTJ_FAIXA_MARGEM_INI,
                Columns.DTJ_FAIXA_MARGEM_FIM,
                Columns.DTJ_FAIXA_TEMP_SERVICO_INI,
                Columns.DTJ_FAIXA_TEMP_SERVICO_FIM,
                Columns.DTJ_FAIXA_VALOR_CONTRATO_INI,
                Columns.DTJ_FAIXA_VALOR_CONTRATO_FIM,
                Columns.DTJ_FAIXA_VALOR_TOTAL_INI,
                Columns.DTJ_FAIXA_VALOR_TOTAL_FIM,
                Columns.DTJ_FAIXA_PRAZO_INI,
                Columns.DTJ_FAIXA_PRAZO_FIM,
                Columns.DTJ_FAIXA_SALARIO_INI,
                Columns.DTJ_FAIXA_SALARIO_FIM,
                Columns.DTJ_TAXA_JUROS,
                Columns.DTJ_TAXA_JUROS_MINIMA,
                Columns.DTJ_FUN_CODIGO,
                Columns.FUN_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
        };
    }
}
