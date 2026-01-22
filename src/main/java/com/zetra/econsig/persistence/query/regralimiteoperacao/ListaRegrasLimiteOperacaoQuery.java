package com.zetra.econsig.persistence.query.regralimiteoperacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;


public class ListaRegrasLimiteOperacaoQuery extends HQuery {

    public String csaCodigo;
    public String rloCodigo;
    public boolean count;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("SELECT count(*) ");
        } else {
            corpoBuilder.append("SELECT rlo.rloCodigo, ");
            corpoBuilder.append("rlo.usuCodigo, ");
            corpoBuilder.append("usu.usuNome, ");
            corpoBuilder.append("rlo.estCodigo, ");
            corpoBuilder.append("est.estNome, ");
            corpoBuilder.append("rlo.orgCodigo, ");
            corpoBuilder.append("org.orgNome, ");
            corpoBuilder.append("rlo.sboCodigo, ");
            corpoBuilder.append("sbo.sboDescricao, ");
            corpoBuilder.append("rlo.uniCodigo, ");
            corpoBuilder.append("uni.uniDescricao, ");
            corpoBuilder.append("rlo.svcCodigo, ");
            corpoBuilder.append("svc.svcDescricao, ");
            corpoBuilder.append("rlo.nseCodigo, ");
            corpoBuilder.append("nse.nseDescricao, ");
            corpoBuilder.append("rlo.ncaCodigo, ");
            corpoBuilder.append("nsa.ncaDescricao, ");
            corpoBuilder.append("rlo.csaCodigo, ");
            corpoBuilder.append("csa.csaNome, ");
            corpoBuilder.append("rlo.corCodigo, ");
            corpoBuilder.append("cor.corNome, ");
            corpoBuilder.append("rlo.crsCodigo, ");
            corpoBuilder.append("crs.crsDescricao, ");
            corpoBuilder.append("rlo.capCodigo, ");
            corpoBuilder.append("cap.capDescricao, ");
            corpoBuilder.append("rlo.prsCodigo, ");
            corpoBuilder.append("prs.prsDescricao, ");
            corpoBuilder.append("rlo.posCodigo, ");
            corpoBuilder.append("pos.posDescricao, ");
            corpoBuilder.append("rlo.srsCodigo, ");
            corpoBuilder.append("srs.srsDescricao, ");
            corpoBuilder.append("rlo.trsCodigo, ");
            corpoBuilder.append("trs.trsDescricao, ");
            corpoBuilder.append("rlo.vrsCodigo, ");
            corpoBuilder.append("vrs.vrsDescricao, ");
            corpoBuilder.append("rlo.funCodigo, ");
            corpoBuilder.append("fun.funDescricao, ");
            corpoBuilder.append("rlo.rloDataCadastro, ");
            corpoBuilder.append("rlo.rloDataVigenciaIni, ");
            corpoBuilder.append("rlo.rloDataVigenciaFim, ");
            corpoBuilder.append("rlo.rloFaixaEtariaIni, ");
            corpoBuilder.append("rlo.rloFaixaEtariaFim, ");
            corpoBuilder.append("rlo.rloFaixaTempoServicoIni, ");
            corpoBuilder.append("rlo.rloFaixaTempoServicoFim, ");
            corpoBuilder.append("rlo.rloFaixaSalarioIni, ");
            corpoBuilder.append("rlo.rloFaixaSalarioFim, ");
            corpoBuilder.append("rlo.rloFaixaMargemFolhaIni, ");
            corpoBuilder.append("rlo.rloFaixaMargemFolhaFim, ");
            corpoBuilder.append("rlo.rloPadraoMatricula, ");
            corpoBuilder.append("rlo.rloPadraoCategoria, ");
            corpoBuilder.append("rlo.rloPadraoVerba, ");
            corpoBuilder.append("rlo.rloPadraoVerbaRef, ");
            corpoBuilder.append("rlo.rloMensagemErro, ");
            corpoBuilder.append("rlo.rloLimiteQuantidade, ");
            corpoBuilder.append("rlo.rloLimiteDataFimAde, ");
            corpoBuilder.append("rlo.rloLimitePrazo, ");
            corpoBuilder.append("rlo.rloLimiteValorParcela, ");
            corpoBuilder.append("rlo.rloLimiteValorLiberado, ");
            corpoBuilder.append("rlo.rloLimiteCapitalDevido ");
        }
        corpoBuilder.append("FROM RegraLimiteOperacao rlo ");
        corpoBuilder.append("LEFT JOIN rlo.estabelecimento est ");
        corpoBuilder.append("LEFT JOIN rlo.orgao org ");
        corpoBuilder.append("LEFT JOIN rlo.subOrgao sbo ");
        corpoBuilder.append("LEFT JOIN rlo.unidade uni ");
        corpoBuilder.append("LEFT JOIN rlo.servico svc ");
        corpoBuilder.append("LEFT JOIN rlo.naturezaServico nse ");
        corpoBuilder.append("LEFT JOIN rlo.naturezaConsignataria nsa ");
        corpoBuilder.append("LEFT JOIN rlo.consignataria csa ");
        corpoBuilder.append("LEFT JOIN rlo.correspondente cor ");
        corpoBuilder.append("LEFT JOIN rlo.cargoRegistroServidor crs ");
        corpoBuilder.append("LEFT JOIN rlo.capacidadeRegistroSer cap ");
        corpoBuilder.append("LEFT JOIN rlo.padraoRegistroServidor prs ");
        corpoBuilder.append("LEFT JOIN rlo.postoRegistroServidor pos ");
        corpoBuilder.append("LEFT JOIN rlo.statusRegistroServidor srs ");
        corpoBuilder.append("LEFT JOIN rlo.tipoRegistroServidor trs ");
        corpoBuilder.append("LEFT JOIN rlo.vinculoRegistroServidor vrs ");
        corpoBuilder.append("LEFT JOIN rlo.funcao fun ");
        corpoBuilder.append("LEFT JOIN rlo.usuario usu ");
        corpoBuilder.append("LEFT JOIN rlo.usuario.usuarioCsaSet usuCsa ");
        corpoBuilder.append("WHERE 1=1 ");

        if(!TextHelper.isNull(rloCodigo)){
            corpoBuilder.append("AND rlo.rloCodigo ").append(criaClausulaNomeada("rloCodigo", rloCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND usuCsa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(rloCodigo)){
            defineValorClausulaNomeada("rloCodigo", rloCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }


        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RLO_CODIGO,
                Columns.RLO_USU_CODIGO,
                Columns.USU_NOME,
                Columns.RLO_EST_CODIGO,
                Columns.EST_NOME,
                Columns.RLO_ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.RLO_SBO_CODIGO,
                Columns.SBO_DESCRICAO,
                Columns.RLO_UNI_CODIGO,
                Columns.UNI_DESCRICAO,
                Columns.RLO_SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.RLO_NSE_CODIGO,
                Columns.NSE_DESCRICAO,
                Columns.RLO_NCA_CODIGO,
                Columns.NCA_DESCRICAO,
                Columns.RLO_CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.RLO_COR_CODIGO,
                Columns.COR_NOME,
                Columns.RLO_CRS_CODIGO,
                Columns.CRS_DESCRICAO,
                Columns.RLO_CAP_CODIGO,
                Columns.CAP_DESCRICAO,
                Columns.RLO_PRS_CODIGO,
                Columns.PRS_DESCRICAO,
                Columns.RLO_POS_CODIGO,
                Columns.POS_DESCRICAO,
                Columns.RLO_SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.RLO_TRS_CODIGO,
                Columns.TRS_DESCRICAO,
                Columns.RLO_VRS_CODIGO,
                Columns.VRS_DESCRICAO,
                Columns.RLO_FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.RLO_DATA_CADASTRO,
                Columns.RLO_DATA_VIGENCIA_INI,
                Columns.RLO_DATA_VIGENCIA_FIM,
                Columns.RLO_FAIXA_ETARIA_INI,
                Columns.RLO_FAIXA_ETARIA_FIM,
                Columns.RLO_FAIXA_TEMPO_SERVICO_INI,
                Columns.RLO_FAIXA_TEMPO_SERVICO_FIM,
                Columns.RLO_FAIXA_SALARIO_INI,
                Columns.RLO_FAIXA_SALARIO_FIM,
                Columns.RLO_FAIXA_MARGEM_FOLHA_INI,
                Columns.RLO_FAIXA_MARGEM_FOLHA_FIM,
                Columns.RLO_PADRAO_MATRICULA,
                Columns.RLO_PADRAO_CATEGORIA,
                Columns.RLO_PADRAO_VERBA,
                Columns.RLO_PADRAO_VERBA_REF,
                Columns.RLO_MENSAGEM_ERRO,
                Columns.RLO_LIMITE_QUANTIDADE,
                Columns.RLO_LIMITE_DATA_FIM_ADE,
                Columns.RLO_LIMITE_PRAZO,
                Columns.RLO_LIMITE_VALOR_PARCELA,
                Columns.RLO_LIMITE_VALOR_LIBERADO,
                Columns.RLO_LIMITE_CAPITAL_DEVIDO,
        };
    }
}
