package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: RelatorioConsignatariasQuery</p>
 * <p> Description: Relatório de cadastro de Consignatárias.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConsignatariasQuery extends ReportHQuery {

    public List<String> nseCodigos;
    public Short[] csaAtivo;
    public Boolean possuiAdeAtiva;
    public Boolean permiteIncluirAde;

    @Override
    public void setCriterios(TransferObject criterio) {
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
        csaAtivo = (Short[]) criterio.getAttribute("CSA_ATIVO");

        possuiAdeAtiva = (Boolean) criterio.getAttribute("POSSUI_ADE_ATIVA");
        permiteIncluirAde = (Boolean) criterio.getAttribute("PERMITE_INCLUIR_ADE");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", (AcessoSistema) null);
        final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", (AcessoSistema) null);
        final String rotuloCsaAtiva = ApplicationResourcesHelper.getMessage("rotulo.relatorio.consignatarias.ativo", (AcessoSistema) null);
        final String rotuloCsaInativa = ApplicationResourcesHelper.getMessage("rotulo.relatorio.consignatarias.bloqueado", (AcessoSistema) null);

        final String corpo = "select csa.csaCodigo as csa_codigo,"
                     + "csa.csaIdentificador as csa_identificador,"
                     + "csa.csaNome as csa_nome,"
                     + "csa.csaNomeAbrev as csa_nome_abrev,"
                     + "csa.csaCnpj as csa_cnpj,"
                     + "csa.csaEmail as csa_email,"
                     + "csa.csaResponsavel as csa_responsavel,"
                     + "csa.csaRespCargo as csa_resp_cargo,"
                     + "csa.csaRespTelefone as csa_resp_telefone,"
                     + "csa.csaResponsavel2 as csa_responsavel_2,"
                     + "csa.csaRespTelefone2 as csa_resp_telefone_2,"
                     + "csa.csaRespCargo2 as csa_resp_cargo_2,"
                     + "csa.csaResponsavel3 as csa_responsavel_3,"
                     + "csa.csaRespCargo3 as csa_resp_cargo_3,"
                     + "csa.csaRespTelefone3 as csa_resp_telefone_3,"
                     + "csa.csaLogradouro as csa_logradouro,"
                     + "csa.csaNro as csa_nro,"
                     + "csa.csaCompl as csa_compl,"
                     + "csa.csaBairro as csa_bairro,"
                     + "csa.csaCidade as csa_cidade,"
                     + "csa.csaUf as csa_uf,"
                     + "csa.csaCep as csa_cep,"
                     + "csa.csaTel as csa_tel,"
                     + "csa.csaFax as csa_fax,"
                     + "csa.csaNroBco as csa_nro_bco,"
                     + "csa.csaNroCta as csa_nro_cta,"
                     + "csa.csaNroAge as csa_nro_age,"
                     + "csa.csaDigCta as csa_dig_cta,"
                     + "csa.csaContato as csa_contato,"
                     + "csa.csaContatoTel as csa_contato_tel,"
                     + "csa.csaEndereco2 as csa_endereco_2,"
                     + "csa.csaDataExpiracao as csa_data_expiracao,"
                     + "csa.csaNroContrato as csa_nro_contrato,"
                     + "coalesce(to_locale_datetime(csa.csaDataExpiracaoCadastral), '') as csa_data_expiracao_cadastral,"
                     + "(case when csa.csaPermiteIncluirAde = 'S' then '"+rotuloSim+"' else '"+rotuloNao+"' end) as csa_permite_incluir_ade,"
                     + "cast(csa.csaAtivo as int) as csa_ativo,"
                     + "tgc.tgcCodigo as tgc_codigo,"
                     + "tgc.tgcIdentificador as tgc_identificador,"
                     + "tgc.tgcDescricao as tgc_descricao,"
                     + "(select max(aut.adeData) from AutDesconto aut inner join aut.verbaConvenio vco inner join vco.convenio cnv where cnv.consignataria.csaCodigo = csa.csaCodigo) as csa_ultimo_contrato,"
                     + "(select case when count(*) = 0 then '"+rotuloNao+"' else '"+rotuloSim+"' end FROM AutDesconto aut INNER JOIN aut.verbaConvenio vco INNER JOIN vco.convenio cnv where cnv.consignataria.csaCodigo = csa.csaCodigo and aut.statusAutorizacaoDesconto.sadCodigo"+criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS)+") as aut_ativas,"
                     + "nca.ncaCodigo as nca_codigo,"
                     + "nca.ncaDescricao as nca_descricao,"
                     + "(case when coalesce(csa.csaAtivo, 1) = 1 then '" + rotuloCsaAtiva + "' else '" + rotuloCsaInativa + "' end) as csa_status_curto,"
                     + "(case when coalesce(csa.csaAtivo, 1) = 1 then '" + rotuloCsaAtiva + "' else concat('" + rotuloCsaInativa + "', (case when tmb.tmbCodigo is not null then concat(' (', tmb.tmbDescricao, ')') else '' end)) end) as csa_status"
                     ;

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria csa");
        corpoBuilder.append(" inner join csa.naturezaConsignataria nca ");
        corpoBuilder.append(" left outer join csa.tipoGrupoConsignataria tgc");
        corpoBuilder.append(" left outer join csa.tipoMotivoBloqueio as tmb");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(nseCodigos)) {
            corpoBuilder.append(" and exists ( ");
            corpoBuilder.append(" select 1 from Convenio cnv");
            corpoBuilder.append(" inner join cnv.servico svc");
            corpoBuilder.append(" inner join svc.naturezaServico nse");
            corpoBuilder.append(" where nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigos)).append(" ");
            corpoBuilder.append(" and csa.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(")");
        }

        if (csaAtivo != null) {
            corpoBuilder.append(" and csa.csaAtivo ").append(criaClausulaNomeada("csaAtivo", csaAtivo));
        }

        if (permiteIncluirAde != null) {
            corpoBuilder.append(" and csa.csaPermiteIncluirAde = '").append(permiteIncluirAde ? "S" : "N" ).append("'");
        }

        if (possuiAdeAtiva != null) {
            corpoBuilder.append(possuiAdeAtiva ? " and exists (" : " and not exists (");
            corpoBuilder.append(" select 1 from AutDesconto ade");
            corpoBuilder.append(" inner join ade.verbaConvenio vco");
            corpoBuilder.append(" inner join vco.convenio cnv");
            corpoBuilder.append(" where csa.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS));
            corpoBuilder.append(") ");
        }

        corpoBuilder.append(" order by tgc_identificador, csa_identificador");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);

        if (!TextHelper.isNull(nseCodigos)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigos, query);
        }

        if (csaAtivo != null) {
            defineValorClausulaNomeada("csaAtivo", csaAtivo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "csa_codigo",
                "csa_identificador",
                "csa_nome",
                "csa_nome_abrev",
                "csa_cnpj",
                "csa_email",
                "csa_responsavel",
                "csa_resp_cargo",
                "csa_resp_telefone",
                "csa_responsavel_2",
                "csa_resp_telefone_2",
                "csa_resp_cargo_2",
                "csa_responsavel_3",
                "csa_resp_cargo_3",
                "csa_resp_telefone_3",
                "csa_logradouro",
                "csa_nro",
                "csa_compl",
                "csa_bairro",
                "csa_cidade",
                "csa_uf",
                "csa_cep",
                "csa_tel",
                "csa_fax",
                "csa_nro_bco",
                "csa_nro_cta",
                "csa_nro_age",
                "csa_dig_cta",
                "csa_contato",
                "csa_contato_tel",
                "csa_endereco_2",
                "csa_data_expiracao",
                "csa_nro_contrato",
                "csa_data_expiracao_cadastral",
                "csa_permite_incluir_ade",
                "csa_ativo",
                "tgc_codigo",
                "tgc_identificador",
                "tgc_descricao",
                "csa_ultimo_contrato",
                "aut_ativas",
                "nca_codigo",
                "nca_descricao",
                "csa_status_curto",
                "csa_status"
        };
    }
}
