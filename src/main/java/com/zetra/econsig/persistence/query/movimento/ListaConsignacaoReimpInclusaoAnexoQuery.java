package com.zetra.econsig.persistence.query.movimento;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaConsignacaoReimpInclusaoAnexoQuery</p>
 * <p>Description: Lista os contratos que tem parcelas não exportadas por motivo de ausência de anexo
 * que tiveram a inclusão do anexo no período de exportção, e que deverão ser reimplantadas.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoReimpInclusaoAnexoQuery extends HNativeQuery {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaConsignacaoReimpInclusaoAnexoQuery.class);

    public List<String> estCodigos;
    public List<String> orgCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        Object paramValidaVlrEOuPrzAlteracaoSemAnexo = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDA_VLR_E_OU_PRZ_EXPORTA_ALT_SEM_ANEXO, responsavel);
        Integer validaVlrEOuPrzAlteracaoSemAnexo = CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO;
        try {
            // Caso parâmetro de sistema esteja configurado errado, padrão será validar somente alteração de valor para maior
            if (!TextHelper.isNull(paramValidaVlrEOuPrzAlteracaoSemAnexo)) {
                validaVlrEOuPrzAlteracaoSemAnexo = Integer.parseInt(paramValidaVlrEOuPrzAlteracaoSemAnexo.toString());
            }
        } catch (NumberFormatException e) {
            LOG.error("Parâmetro para validar valor e/ou prazo na alteração sem anexo inválido.", e);
        }

        List<String> sadCodigosPermiteReimplante = new ArrayList<>();
        sadCodigosPermiteReimplante.add(CodedValues.SAD_DEFERIDA);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_ESTOQUE);
        sadCodigosPermiteReimplante.add(CodedValues.SAD_ESTOQUE_MENSAL);

        List<String> tocCodigosNaoPermiteReimplante = new ArrayList<>();
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_RELANCAMENTO);
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR);
        tocCodigosNaoPermiteReimplante.add(CodedValues.TOC_ALTERACAO_CONTRATO);

        List<String> tarCodigosPermitidos = new ArrayList<>();
        tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO.getCodigo());
        tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_SUSPENSAO.getCodigo());
        tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_LIQUIDACAO.getCodigo());
        if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ade.ade_codigo, pex.pex_periodo, pex.pex_data_fim, org.org_codigo ");
        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
        if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            corpoBuilder.append("inner join tb_solicitacao_autorizacao soa on (soa.ade_codigo = ade.ade_codigo) ");
        }

        // Contratos que permitem reimplante e que não foram pagos pela folha ou foram com valor distinto ao valor da parcela
        corpoBuilder.append("where ade.sad_codigo in ('").append(TextHelper.join(sadCodigosPermiteReimplante, "','")).append("') ");
        corpoBuilder.append("and (coalesce(ade.ade_paga, 'N') <> 'S'");

        // Ou contrato ativo que foi alterado para maior para valor e/ou prazo, dependendo da configuração do parâmetro
        if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO)) {
            corpoBuilder.append(" or ade.ade_vlr_folha < ade.ade_vlr");
        } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
            corpoBuilder.append(" or coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo");
        } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_E_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
            corpoBuilder.append(" or (ade.ade_vlr_folha < ade.ade_vlr and coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo)");
        } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_OU_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
            corpoBuilder.append(" or (ade.ade_vlr_folha < ade.ade_vlr or coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo)");
        }

        corpoBuilder.append(") ");

        // Que tiveram anexo ativo incluído no período de exportação
        corpoBuilder.append("and (exists ( ");
        corpoBuilder.append("select 1 from tb_anexo_autorizacao_desconto aad ");
        corpoBuilder.append("where aad.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("and (aad.aad_periodo = pex.pex_periodo or (aad.aad_periodo is null and aad.aad_data between pex.pex_data_ini and pex.pex_data_fim)) ");
        corpoBuilder.append("and aad.aad_ativo = 1 ");
        // Não deve apenas possuir anexo, deve possuir os tipo de arquivos permitidos (13,16,49)
        corpoBuilder.append("and aad.tar_codigo in ('").append(TextHelper.join(tarCodigosPermitidos, "','")).append("') ");
        corpoBuilder.append(") ");

        // Ou possui anexo ativo, independente do período, na consignação de provisionamento de margem
        corpoBuilder.append("or exists ( ");
        corpoBuilder.append("select 1 from tb_relacionamento_autorizacao rad ");
        corpoBuilder.append("inner join tb_anexo_autorizacao_desconto aad on (rad.ade_codigo_origem = aad.ade_codigo) ");
        corpoBuilder.append("where rad.ade_codigo_destino = ade.ade_codigo ");
        corpoBuilder.append("and rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
        corpoBuilder.append("and aad.aad_ativo = 1 ");
        corpoBuilder.append("and aad.tar_codigo in ('").append(TextHelper.join(tarCodigosPermitidos, "','")).append("') ");
        corpoBuilder.append(")) ");

        // Contratos que possuem ocorrência de relançamento por falta de anexo (132)
        corpoBuilder.append("and exists ( ");
        corpoBuilder.append("  select 1 from tb_ocorrencia_autorizacao oca ");
        corpoBuilder.append("  where oca.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("    and oca.toc_codigo = '").append(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO).append("' ");
        corpoBuilder.append("    and oca.oca_periodo = pex.pex_periodo ");
        corpoBuilder.append(") ");

        // Contratos que não possuem ocorrência de relançamento (10 ou 107), ou de alteração (14) para este período, pois já serão reimplantados
        corpoBuilder.append("and not exists ( ");
        corpoBuilder.append("  select 1 from tb_ocorrencia_autorizacao oca ");
        corpoBuilder.append("  where oca.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("    and oca.toc_codigo in ('").append(TextHelper.join(tocCodigosNaoPermiteReimplante, "','")).append("') ");
        corpoBuilder.append("    and oca.oca_periodo = pex.pex_periodo ");
        corpoBuilder.append(") ");

        // Quando exige permissão do gestor, pode acontecer de ter somente um anexo, porém não é suficiente assim precisamos remover esses contratos para não serem reimplatados erroneamene
        if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM tb_solicitacao_autorizacao soa1 WHERE soa1.soa_data > soa.soa_data AND soa1.soa_data BETWEEN pex.pex_data_ini and pex.pex_data_fim AND ade.ade_codigo = soa1.ade_codigo AND soa1.tis_codigo ='").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("') ");
            corpoBuilder.append("AND soa.sso_codigo = '").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA).append("' ");
            corpoBuilder.append("AND soa.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        }

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.est_codigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_FIM,
                Columns.ORG_CODIGO
         };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigos = (List<String>) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
    }
}
