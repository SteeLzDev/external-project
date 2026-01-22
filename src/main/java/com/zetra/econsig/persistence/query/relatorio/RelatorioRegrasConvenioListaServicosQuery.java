package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioRegrasConvenioListaServicosQuery</p>
 * <p>Description: Query Relatório de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioRegrasConvenioListaServicosQuery extends HNativeQuery {

    private final AcessoSistema responsavel;
    private final String csaCodigo;

    public RelatorioRegrasConvenioListaServicosQuery(String csaCodigo, AcessoSistema responsavel) {
        this.csaCodigo = csaCodigo;
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
        String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);

        String orgCodigo = responsavel.getOrgCodigo();

        List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO);
        tpsCodigos.add(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS);
        tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG);
        tpsCodigos.add(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO);
        tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG);
        tpsCodigos.add(CodedValues.TPS_MINIMO_VIGENCIA_RENEG);
        tpsCodigos.add(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA);
        tpsCodigos.add(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV);
        tpsCodigos.add(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA);
        tpsCodigos.add(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO);
        tpsCodigos.add(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE);
        tpsCodigos.add(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA);
        tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA);
        tpsCodigos.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP);
        tpsCodigos.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR);
        tpsCodigos.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER);
        tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);
        tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
        tpsCodigos.add(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);
        tpsCodigos.add(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
        tpsCodigos.add(CodedValues.TPS_MAX_PRAZO);
        tpsCodigos.add(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF);
        tpsCodigos.add(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO);
        tpsCodigos.add(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        tpsCodigos.add(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS);
        tpsCodigos.add(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS);
        tpsCodigos.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
        tpsCodigos.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE);
        tpsCodigos.add(CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC);
        tpsCodigos.add(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);

        List<String> tpsCodigosSimNao = new ArrayList<>();
        tpsCodigosSimNao.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
        tpsCodigosSimNao.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE);
        tpsCodigosSimNao.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP);
        tpsCodigosSimNao.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR);
        tpsCodigosSimNao.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER);
        tpsCodigosSimNao.add(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO);
        tpsCodigosSimNao.add(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        tpsCodigosSimNao.add(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS);
        tpsCodigosSimNao.add(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS);
        tpsCodigosSimNao.add(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);
        tpsCodigosSimNao.add(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS);

        List<String> tpsCodigosPenalidadeCompra = new ArrayList<>();
        tpsCodigosPenalidadeCompra.add(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV);
        tpsCodigosPenalidadeCompra.add(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO);
        tpsCodigosPenalidadeCompra.add(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE);

        List<String> tpsCodigosCarencia = new ArrayList<>();
        tpsCodigosCarencia.add(CodedValues.TPS_CARENCIA_MINIMA);
        tpsCodigosCarencia.add(CodedValues.TPS_CARENCIA_MAXIMA);
        tpsCodigosCarencia.add(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);

        List<String> tpsCodigoQtdMaxParcela = new ArrayList<>();
        tpsCodigoQtdMaxParcela.add(CodedValues.TPS_MAX_PRAZO);

        List<String> tpsSubtituloCaracteristica = new ArrayList<>();
        tpsSubtituloCaracteristica.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_MAX_PRAZO);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_CARENCIA_MAXIMA);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_CARENCIA_MINIMA);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);
        tpsSubtituloCaracteristica.add(CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC);

        List<String> tpsSubtituloRenegociacao = new ArrayList<>();
        tpsSubtituloRenegociacao.add(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG);
        tpsSubtituloRenegociacao.add(CodedValues.TPS_MINIMO_VIGENCIA_RENEG);

        List<String> tpsSubtituloPortabilidade = new ArrayList<>();
        tpsSubtituloPortabilidade.add(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA);
        tpsSubtituloPortabilidade.add(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF);

        String rotuloPenalidadeNaoAplica = ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel);
        String rotuloPenalidadeBloqueiaCsa = ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel);
        String rotuloPenalidadeCancelaProcesso = ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel);
        String rotuloNaoHa = ApplicationResourcesHelper.getMessage("rotulo.nao.ha", responsavel);

        String rotuloRelatorioRegrasConvenioSubtituloCaracteristicas = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.subtitulo.caracteristica", responsavel);
        String rotuloRelatorioRegrasConvenioSubtituloRenegociacao = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.subtitulo.renegociacao", responsavel);
        String rotuloRelatorioRegrasConvenioSubtituloPortabilidade = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.subtitulo.portabilidade", responsavel);

        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("csa.csa_codigo as CSA_CODIGO, ");
        corpo.append("csa.csa_nome as CSA_NOME, ");
        corpo.append("svc.svc_codigo as SVC_CODIGO, ");
        corpo.append("svc.svc_descricao as SVC_DESCRICAO, ");
        corpo.append("tps.tps_codigo as TPS_CODIGO, ");
        corpo.append("tps.tps_descricao as TPS_DESCRICAO, ");

        corpo.append("coalesce(case ");
        // Parâmetros sim ou não
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosSimNao, "', '")).append("') and psc.pse_vlr = '1' then '").append(rotuloSim).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosSimNao, "', '")).append("') and psc.pse_vlr = '0' then '").append(rotuloNao).append("' ");
        // Parâmetros penalidade
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '0' then '").append(rotuloPenalidadeNaoAplica).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '1' then '").append(rotuloPenalidadeBloqueiaCsa).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '2' then '").append(rotuloPenalidadeCancelaProcesso).append("' ");
        // Parâmetros de carência
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosCarencia, "', '")).append("') then coalesce( cmv.valor_maximo, psc.pse_vlr ) ");
        // Parâmetro de quantidade máxima de parcelas
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigoQtdMaxParcela, "', '")).append("') then coalesce(prm.prazo_maximo, vlr.segundo_valor_maximo, '").append(rotuloNaoHa).append("') ");
        // Valor padrão
        corpo.append("else psc.pse_vlr end, '").append(rotuloNaoHa).append("') as PSE_VALOR, ");
        corpo.append("coalesce(psc.pse_vlr_ref, '").append(rotuloNaoHa).append("') as PSE_VALOR_REF, ");

        //Agrupamento dos subtitulos dos parametros
        corpo.append("case ");
        //Caracteristicas Gerais
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloCaracteristica, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' ");
        //Renegociação
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloRenegociacao, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloRenegociacao).append("' ");
        //Portabilidade
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloPortabilidade, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloPortabilidade).append("' ");
        // Valor padrão
        corpo.append("else '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' end as SUBTITULO ");

        corpo.append("from tb_tipo_param_svc tps ");
        corpo.append("cross join tb_convenio cnv ");
        corpo.append("inner join tb_consignataria csa on csa.csa_codigo = cnv.csa_codigo ");
        corpo.append("inner join tb_servico svc on svc.svc_codigo = cnv.svc_codigo ");
        corpo.append("left join tb_param_svc_consignante psc on svc.svc_codigo = psc.svc_codigo AND tps.tps_codigo = psc.tps_codigo ");
        corpo.append("left join (select tps_codigo, svc_codigo, csa_codigo, max(psca.psc_vlr) as segundo_valor_maximo from tb_param_svc_consignataria psca group by tps_codigo, svc_codigo, csa_codigo) vlr on ");
        corpo.append("vlr.tps_codigo = tps.tps_codigo ");
        corpo.append("AND vlr.svc_codigo = svc.svc_codigo ");
        corpo.append("AND vlr.csa_codigo = csa.csa_codigo ");
        corpo.append("left join (select tps_codigo, svc_codigo, csa_codigo, MAX(psc_vlr) as valor_maximo from tb_param_svc_consignataria psca2 group by tps_codigo, svc_codigo, csa_codigo) cmv on ");
        corpo.append("cmv.tps_codigo = tps.tps_codigo ");
        corpo.append("and cmv.svc_codigo = svc.svc_codigo ");
        corpo.append("and cmv.csa_codigo = csa.csa_codigo ");
        corpo.append("left join (select prz_csa_ativo, prz_ativo, svc_codigo, csa_codigo, to_string(max(prz.prz_vlr)) as prazo_maximo from tb_prazo prz join tb_prazo_consignataria pcs on prz.prz_codigo = pcs.prz_codigo where prz.prz_ativo = 1 and pcs.prz_csa_ativo = 1 group by prz_csa_ativo, prz_ativo, svc_codigo, csa_codigo) prm on ");
        corpo.append("prm.csa_codigo = csa.csa_codigo ");
        corpo.append("AND prm.svc_codigo = svc.svc_codigo ");

        corpo.append("where cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("' ");
        corpo.append("and tps.tps_codigo in ('").append(TextHelper.join(tpsCodigos, "', '")).append("') ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append(" and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append(" and cnv.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpo.append(" group by csa.csa_codigo, csa.csa_nome, svc.svc_codigo, svc.svc_descricao, tps.tps_codigo, tps.tps_descricao, ");
        //subtitulo
        corpo.append("case ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloCaracteristica, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloRenegociacao, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloRenegociacao).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloPortabilidade, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloPortabilidade).append("' ");
        corpo.append("else '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' end, ");
        
        //pse_vlr
        corpo.append("coalesce(case ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosSimNao, "', '")).append("') and psc.pse_vlr = '1' then '").append(rotuloSim).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosSimNao, "', '")).append("') and psc.pse_vlr = '0' then '").append(rotuloNao).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '0' then '").append(rotuloPenalidadeNaoAplica).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '1' then '").append(rotuloPenalidadeBloqueiaCsa).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosPenalidadeCompra, "', '")).append("') and psc.pse_vlr = '2' then '").append(rotuloPenalidadeCancelaProcesso).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigosCarencia, "', '")).append("') then coalesce( cmv.valor_maximo, psc.pse_vlr ) ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsCodigoQtdMaxParcela, "', '")).append("') then coalesce(prm.prazo_maximo, vlr.segundo_valor_maximo, '").append(rotuloNaoHa).append("') ");
        corpo.append("else psc.pse_vlr end, '").append(rotuloNaoHa).append("'), ");
        
        //pse_valor_ref
        corpo.append("coalesce(psc.pse_vlr_ref, '").append(rotuloNaoHa).append("') ");
        corpo.append(" order by csa.csa_nome, svc.svc_descricao, ");
        
        //subtitulo
        corpo.append("case ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloCaracteristica, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloRenegociacao, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloRenegociacao).append("' ");
        corpo.append("when tps.tps_codigo in ('").append(TextHelper.join(tpsSubtituloPortabilidade, "', '")).append("') then '").append(rotuloRelatorioRegrasConvenioSubtituloPortabilidade).append("' ");
        corpo.append("else '").append(rotuloRelatorioRegrasConvenioSubtituloCaracteristicas).append("' end, ");
        corpo.append("tps.tps_descricao ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {

        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.TPS_CODIGO,
                Columns.TPS_DESCRICAO,
                Columns.PSE_VLR,
                Columns.PSE_VLR_REF,
                "SUBTITULO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
