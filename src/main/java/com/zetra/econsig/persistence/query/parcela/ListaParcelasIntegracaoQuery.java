package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelasIntegracaoQuery</p>
 * <p>Description: Seleciona as parcelas para um determinado servidor, ou
 * para todos se a matrícula e o cpf forem nulos. As parcelas devem
 * ter o status presente na lista spdCodigos, ou qualquer status se
 * a lista for nula ou vazia.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasIntegracaoQuery extends HQuery {
    public AcessoSistema responsavel;
    public boolean count = false;

    public String tipo;
    public String adeNumero;
    public String adeIdentificador;
    public String rseMatricula;
    public String serCpf;
    public String orgCodigo;
    public String csaCodigo;
    public List<String> tocCodigos;
    public List<String> spdCodigos;
    public List<String> papCodigos;
    public Date periodoIni;
    public Date periodoFim;
    public boolean matriculaExataSoap = false;
    public boolean buscaCse = false;
    public boolean buscaCsa = false;
    public boolean buscaCor = false;
    public boolean buscaOrg = false;
    public boolean buscaSup = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if (count) {
            corpo = "SELECT COUNT(*) as Contador ";
        } else if (!count && papCodigos != null && papCodigos.size() > 0 && !spdCodigos.contains(CodedValues.SPD_EMPROCESSAMENTO)) {
            corpo = "SELECT " +
                    "rse.rseMatricula, " +
                    "ser.serNome, " +
                    "est.estIdentificador, " +
                    "org.orgIdentificador, " +
                    "svc.svcDescricao, " +
                    "prd.prdCodigo, " +
                    "prd.prdNumero, " +
                    "prd.prdVlrPrevisto, " +
                    "prd.prdVlrRealizado, " +
                    "prd.prdDataDesconto, " +
                    "prd.prdDataRealizado, " +
                    "ade.adeCodigo, " +
                    "ade.adeNumero, " +
                    "ade.adePaga, " +
                    "spd.spdCodigo, " +
                    "spd.spdDescricao, " +
                    "cnv.cnvCodVerba, "+
                    "case " +
                    " when (usuCse.usuario.usuCodigo is not null) then '"+AcessoSistema.ENTIDADE_CSE+"' " +
                    " when (usuCsa.usuario.usuCodigo is not null) then '"+AcessoSistema.ENTIDADE_CSA+"' " +
                    " when (usuCor.usuario.usuCodigo is not null) then '"+AcessoSistema.ENTIDADE_COR+"' " +
                    " when (usuOrg.usuario.usuCodigo is not null) then '"+AcessoSistema.ENTIDADE_ORG+"' " +
                    " when (usuSup.usuario.usuCodigo is not null) then '"+AcessoSistema.ENTIDADE_SUP+"' " +
                    " else 'SISTEMA' " +
                    "end AS PAP_DESCRICAO ";
        } else {
            corpo = "SELECT " +
                    "rse.rseMatricula, " +
                    "ser.serNome, " +
                    "est.estIdentificador, " +
                    "org.orgIdentificador, " +
                    "svc.svcDescricao, " +
                    "prd.prdCodigo, " +
                    "prd.prdNumero, " +
                    "prd.prdVlrPrevisto, " +
                    "prd.prdVlrRealizado, " +
                    "prd.prdDataDesconto, " +
                    "prd.prdDataRealizado, " +
                    "ade.adeCodigo, " +
                    "ade.adeNumero, " +
                    "ade.adePaga, " +
                    "spd.spdCodigo, " +
                    "spd.spdDescricao, " +
                    "cnv.cnvCodVerba, "+
                    "'' as PAP_DESCRICAO";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (spdCodigos != null && spdCodigos.contains(CodedValues.SPD_EMPROCESSAMENTO)) {
            corpoBuilder.append(" FROM ParcelaDescontoPeriodo prd ");
        } else {
            corpoBuilder.append(" FROM ParcelaDesconto prd ");
        }
        corpoBuilder.append(" INNER JOIN prd.statusParcelaDesconto spd ");
        corpoBuilder.append(" INNER JOIN prd.autDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est ");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append(" INNER JOIN rse.servidor ser ");

        if (spdCodigos != null && !spdCodigos.contains(CodedValues.SPD_EMPROCESSAMENTO)) {
        	corpoBuilder.append("inner join prd.ocorrenciaParcelaSet ocp ");
        	corpoBuilder.append("inner join ocp.usuario usu ");
        	corpoBuilder.append("left outer join usu.usuarioCseSet usuCse ");
            corpoBuilder.append("left outer join usu.usuarioCsaSet usuCsa ");
            corpoBuilder.append("left outer join usu.usuarioCorSet usuCor ");
            corpoBuilder.append("left outer join usu.usuarioOrgSet usuOrg ");
            corpoBuilder.append("left outer join usu.usuarioSupSet usuSup ");
        }

        if (tipo.equalsIgnoreCase("EST")) {
            corpoBuilder.append(" INNER JOIN est.orgaoSet ORG_EST ");
        }

        corpoBuilder.append(" WHERE 1=1 ");

        boolean matriculaExata = matriculaExataSoap || ParamSist.getBoolParamSist(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, AcessoSistema.getAcessoUsuarioSistema());
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata));

        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        if (!TextHelper.isNull(adeIdentificador)) {
            corpoBuilder.append(" AND ade.adeIdentificador like :adeIdentificador");
        }

        if (!TextHelper.isNull(orgCodigo)) {
            if (tipo.equalsIgnoreCase("EST")) {
                corpoBuilder.append(" AND ORG_EST.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            } else {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            }
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spdCodigo IN (:spdCodigos)");
        }

        if (papCodigos != null && papCodigos.size() > 0 && !spdCodigos.contains(CodedValues.SPD_EMPROCESSAMENTO)) {

        	//não precisa listar as ocorrências referentes às parcelas desfeitas porque este codigo é casado com o 13 que é retorno e nem contar para a paginação.
        	corpoBuilder.append(" AND ocp.tipoOcorrencia.tocCodigo <> '" + CodedValues.TOC_DESFEITO + "' ");

        	if (papCodigos.size() < 4) {
            	if (papCodigos.contains(CodedValues.PAP_CONSIGNANTE)){
            		if (buscaCsa||buscaOrg||buscaCor||buscaSup) {
            			corpoBuilder.append(" OR");
            		} else {
            			corpoBuilder.append(" AND");
            		}
            		corpoBuilder.append(" usuCse.usuario.usuCodigo is not null ");
            		buscaCse = true;
            	}
            	if (papCodigos.contains(CodedValues.PAP_CONSIGNATARIA)){
            		if (buscaCse||buscaOrg||buscaCor||buscaSup) {
            			corpoBuilder.append(" OR");
            		} else {
            			corpoBuilder.append(" AND");
            		}
            		corpoBuilder.append(" usuCsa.usuario.usuCodigo is not null ");
            		buscaCsa = true;
            	}
            	if (papCodigos.contains(CodedValues.PAP_ORGAO)){
            		if (buscaCse||buscaCsa||buscaCor||buscaSup) {
            			corpoBuilder.append(" OR");
            		} else {
            			corpoBuilder.append(" AND");
            		}
            		corpoBuilder.append(" usuOrg.usuario.usuCodigo is not null ");
            		buscaOrg = true;
            	}
            	if (papCodigos.contains(CodedValues.PAP_CORRESPONDENTE)){
            		if (buscaCse||buscaCsa||buscaOrg||buscaSup) {
            			corpoBuilder.append(" OR");
            		} else {
            			corpoBuilder.append(" AND");
            		}
            		corpoBuilder.append(" usuCor.usuario.usuCodigo is not null ");
            		buscaCor = true;
            	}
            	if (papCodigos.contains(CodedValues.PAP_SUPORTE)){
            		if (buscaCse||buscaCsa||buscaOrg||buscaSup) {
            			corpoBuilder.append(" OR");
            		} else {
            			corpoBuilder.append(" AND");
            		}
            		corpoBuilder.append(" usuSup.usuario.usuCodigo is not null ");
            		buscaSup = true;
            	}
        	}
        }
        
        if (tocCodigos != null && tocCodigos.size() > 0 && !spdCodigos.contains(CodedValues.SPD_EMPROCESSAMENTO)) {
        	corpoBuilder.append(" AND ocp.tipoOcorrencia.tocCodigo IN (:tocCodigos)");
        }

        if (!TextHelper.isNull(periodoIni)) {
            corpoBuilder.append(" AND prd.prdDataDesconto >= :paramPeriodoIni ");
        }
        if (!TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND prd.prdDataDesconto <= :paramPeriodoFim ");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY prd.prdDataRealizado DESC, prd.prdDataDesconto");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata, query);

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", Long.valueOf(adeNumero), query);
        }

        if (!TextHelper.isNull(adeIdentificador)) {
            defineValorClausulaNomeada("adeIdentificador", "%" + adeIdentificador + "%", query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, query);
        }

        if (tocCodigos != null && tocCodigos.size() > 0) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        if (!TextHelper.isNull(periodoIni)) {
            defineValorClausulaNomeada("paramPeriodoIni", periodoIni, query);
        }
        if (!TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("paramPeriodoFim", periodoFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.PRD_CODIGO,
                Columns.PRD_NUMERO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_PAGA,
                Columns.SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                Columns.CNV_COD_VERBA,
                Columns.PAP_DESCRICAO
        };
    }
}
