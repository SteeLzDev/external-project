package com.zetra.econsig.persistence.query.relatorio;

import java.sql.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioAnexosConsignacaoQuery</p>
 * <p> Description: Relatório de anexos de consignação.</p>
 * <p> Copyright: Copyright (c) 2017 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAnexosConsignacaoQuery extends ReportHQuery {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioAnexosConsignacaoQuery.class);

    private Date dataPeriodo;
    private String estCodigo;
    private List<String> orgCodigos;
    private String csaCodigo;
    private String corCodigo;
    private String tipoPeriodo;
    private List<String> svcCodigo;
    private List<String> sadCodigos;
    private List<String> srsCodigos;
    private Boolean temAnexo;
    public String dataIni;
    public String dataFim;
    public List<String> tocCodigos;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataPeriodo = (Date) criterio.getAttribute("DATA_PERIODO");
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        corCodigo = (String) criterio.getAttribute("COR_CODIGO");
        svcCodigo = (List<String>) criterio.getAttribute("SVC_CODIGO");
        sadCodigos = (List<String>) criterio.getAttribute("SAD_CODIGO");
        tipoPeriodo = (String) criterio.getAttribute("tipoPeriodo");
        srsCodigos = (List<String>) criterio.getAttribute(Columns.SRS_CODIGO);
        temAnexo = (Boolean) criterio.getAttribute("temAnexo");
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        tocCodigos = (List<String>) criterio.getAttribute("TOC_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(orgCodigos) && responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        }

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

        boolean temStatusServidor = (srsCodigos != null && srsCodigos.size() > 0);
        boolean temStatusAde = (sadCodigos != null && sadCodigos.size() > 0);
        
        boolean hasPeriodo = !TextHelper.isNull(dataPeriodo);
		boolean hasTipoPeriodo = !TextHelper.isNull(tipoPeriodo);
		
		boolean periodoCompleto = hasPeriodo && hasTipoPeriodo;

        StringBuilder hql = new StringBuilder();

        hql.append(" SELECT ");
        hql.append(" ser.serNome AS nomeServidor, ");
        hql.append(" rse.rseMatricula AS matricula, ");
        hql.append(" ser.serCpf AS cpf, ");
        hql.append(" cnv.cnvCodVerba AS codVerba, ");
        hql.append(" ade.adeNumero AS adeNumero, ");
        hql.append(" ade.adeIdentificador AS adeIdentificador, ");
        hql.append(" ade.adeVlr AS valor, ");
        hql.append(" ade.adePrazo AS prazo, ");
        hql.append(" ade.adeAnoMesIni AS adeAnoMesIni, ");
        hql.append(" anx.aadNome AS nomeAnexo, ");
        hql.append(" anx.aadDescricao AS descAnexo, ");
        hql.append(" anx.aadData AS aadData, ");
        hql.append(" anx.aadPeriodo AS periodoAnexo, ");
        hql.append(" CONCAT(CONCAT(csa.csaIdentificador, ' - '), CASE WHEN NULLIF(TRIM(csa.csaNomeAbrev), '') IS NULL THEN csa.csaNome ELSE csa.csaNomeAbrev end) AS consignataria, ");
        hql.append(" CONCAT(CONCAT(org.orgIdentificador, ' - '), org.orgNome) AS nomeOrgao, ");
        hql.append(" oca.ocaData as ocaData ");

        hql.append(" FROM AutDesconto ade ");

        hql.append(" INNER JOIN ade.registroServidor rse ");
        hql.append(" INNER JOIN rse.servidor ser ");
        hql.append(" INNER JOIN ade.verbaConvenio vco ");
        hql.append(" INNER JOIN vco.convenio cnv ");
        hql.append(" INNER JOIN cnv.servico svc ");
        hql.append(" INNER JOIN cnv.consignataria csa ");
        if (!TextHelper.isNull(corCodigo)) {
            hql.append(" INNER JOIN csa.correspondenteSet cor ");
        }
        hql.append(" INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        hql.append(" INNER JOIN rse.statusRegistroServidor srs ");
        hql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        hql.append(" INNER JOIN rse.orgao org ");
        hql.append(" INNER JOIN org.estabelecimento est ");
        hql.append(" INNER JOIN est.consignante cse ");
        if(periodoCompleto) {
        	hql.append(" LEFT OUTER JOIN ade.anexoAutorizacaoDescontoSet anx WITH anx.aadPeriodo >= :periodo ");
        } else {
        	hql.append(" LEFT OUTER JOIN ade.anexoAutorizacaoDescontoSet anx WITH anx.aadData BETWEEN :dataIni AND :dataFim ");
        }

        hql.append(" WHERE ");

        if (temAnexo != null && temAnexo) {
            hql.append(" anx.aadNome IS NOT NULL ");
        } else {
            hql.append(" anx.aadNome IS NULL ");
        }        

        if(periodoCompleto) {
        	hql.append(" AND oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tipoOcorrencia", new String()));
        	
        	hql.append(" AND oca.ocaPeriodo ").append(criaClausulaNomeada("periodo", dataPeriodo));
        	
            if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_ALTERACAO_MAIOR)) {
                // Ou contrato ativo que foi alterado para maior para valor e/ou prazo, dependendo da configuração do parâmetro
                if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO)) {
                    hql.append(" AND oca.ocaAdeVlrAnt < oca.ocaAdeVlrNovo ");
                } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                    hql.append(" AND ade.adePrazoRef < ade.adePrazo ");
                } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_E_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                    hql.append(" AND (oca.ocaAdeVlrAnt < oca.ocaAdeVlrNovo AND ade.adePrazoRef < ade.adePrazo) ");
                } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_OU_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                    hql.append(" AND (oca.ocaAdeVlrAnt < oca.ocaAdeVlrNovo OR ade.adePrazoRef < ade.adePrazo) ");
                }

            } else if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_ALTERACAO_MENOR)) {
                hql.append(" AND oca.ocaAdeVlrAnt > oca.ocaAdeVlrNovo ");
            }
        } else {
        	hql.append(" AND oca.ocaData BETWEEN :dataIni AND :dataFim ");
        	
            if((tocCodigos != null) && !tocCodigos.isEmpty()) {
            	hql.append(" AND oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
            }
            
            if ((tocCodigos != null) && tocCodigos.contains(CodedValues.TOC_ALTERACAO_CONTRATO)) {
            	hql.append(" AND NOT EXISTS (SELECT 1 from OcorrenciaAutorizacao oca1 WHERE ade.adeCodigo = oca1.autDesconto.adeCodigo");
            	hql.append("                 AND oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
            	hql.append("                 AND oca1.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO).append("'");
            	hql.append("                 AND oca.ocaCodigo <> oca1.ocaCodigo");
            	hql.append("                 AND oca.ocaAdeVlrAnt = oca1.ocaAdeVlrAnt");
            	hql.append("                 AND oca.ocaAdeVlrNovo = oca1.ocaAdeVlrNovo").append(")");
            }
        }       

        if (!TextHelper.isNull(csaCodigo)) {
            hql.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            hql.append(" AND cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (!TextHelper.isNull(estCodigo)) {
            hql.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            hql.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            hql.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (temStatusServidor) {
            hql.append(" AND srs.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        }

        if (temStatusAde) {
            hql.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        hql.append(" GROUP BY ser.serNome, rse.rseMatricula, ser.serCpf, cnv.cnvCodVerba, ade.adeNumero, ade.adeIdentificador, ade.adeVlr, ade.adePrazo, ade.adeAnoMesIni, anx.aadNome, anx.aadDescricao, anx.aadData, anx.aadPeriodo, csa.csaIdentificador, csa.csaNomeAbrev, csa.csaNome, org.orgIdentificador, org.orgNome, oca.ocaData ");
        hql.append(" ORDER BY consignataria, nomeOrgao, oca.ocaData asc ");


        Query<Object[]> query = instanciarQuery(session, hql.toString());

        if(periodoCompleto) {
	        if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_INCLUSAO)) {
	            defineValorClausulaNomeada("tipoOcorrencia", CodedValues.TOC_TARIF_RESERVA, query);
	
	        } else if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_EXCLUSAO)) {
	            defineValorClausulaNomeada("tipoOcorrencia", CodedValues.TOC_TARIF_LIQUIDACAO, query);
	
	        } else if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_ALTERACAO_MAIOR)) {
	            defineValorClausulaNomeada("tipoOcorrencia", CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR, query);
	
	        } else if (tipoPeriodo.equals(CodedValues.TIPO_PERIODO_ALTERACAO_MENOR)) {
	            defineValorClausulaNomeada("tipoOcorrencia", CodedValues.TOC_ALTERACAO_CONTRATO, query);
	        }

            defineValorClausulaNomeada("periodo", dataPeriodo, query);
        } else {
        	defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
       
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        	
            if ((tocCodigos != null) && !tocCodigos.isEmpty()) {
                defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
            }
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (temStatusServidor) {
            defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        }

        if (temStatusAde) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        return query;
    }
}