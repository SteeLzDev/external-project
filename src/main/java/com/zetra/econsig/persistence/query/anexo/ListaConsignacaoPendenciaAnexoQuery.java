package com.zetra.econsig.persistence.query.anexo;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoPendenciaAnexoQuery</p>
 * <p>Description: Listagem de consignações com pendência de informação de anexos mínimos.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoPendenciaAnexoQuery extends HNativeQuery {
    private final AcessoSistema responsavel;

    public ListaConsignacaoPendenciaAnexoQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String estCodigo;
    public String orgCodigo;
    public String csaCodigo;
	public String corCodigo;
    public String svcCodigo;
    public Date adeDataIni;
    public Date adeDataFim;
    public List<Long> adeNumero;
    public String serCpf;
    public String rseMatricula;

    public boolean pendenciaAnexo;
    public boolean count;

    @Override
    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
            orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
            csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
            svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);

            adeDataIni   = (Date) criterio.getAttribute("periodoIni");
            adeDataFim   = (Date) criterio.getAttribute("periodoFim");
            adeNumero    = (List<Long>) criterio.getAttribute(Columns.ADE_NUMERO);
            serCpf       = (String) criterio.getAttribute(Columns.SER_CPF);
            rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
        }
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;

        String campos = "ade.ade_codigo, "
                + "ade.ade_data, "
                + "ade.ade_identificador, "
                + "ade.ade_indice, "
                + "ade.ade_numero, "
                + "ade.ade_prazo, "
                + "ade.ade_prd_pagas, "
                + "ade.ade_tipo_vlr, "
                + "ade.ade_vlr, "
                + "sad.sad_descricao, "
                + "cnv.cnv_cod_verba, "
                + "svc.svc_descricao, "
                + "svc.svc_identificador, "
                + "csa.csa_identificador, "
                + "csa.csa_nome, "
                + "csa.csa_nome_abrev, "
                + "rse.rse_matricula, "
                + "ser.ser_cpf, "
                + "ser.ser_nome, "
                + "usu.usu_codigo, "
                + "usu.usu_login, "
                + "usu.usu_tipo_bloq "
                ;

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(distinct ade.ade_codigo) ");
        } else {
            corpoBuilder.append("select max(coalesce(aad.aad_data, ade.ade_data)), ").append(campos).append(" ");
        }

        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append("inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        corpoBuilder.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        corpoBuilder.append("inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
        corpoBuilder.append("inner join tb_status_autorizacao_desconto sad on (ade.sad_codigo = sad.sad_codigo) ");
        corpoBuilder.append("inner join tb_usuario usu on (ade.usu_codigo = usu.usu_codigo) ");
        corpoBuilder.append("left outer join tb_correspondente cor on (ade.cor_codigo = cor.cor_codigo) ");
        corpoBuilder.append("left outer join tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR).append("') ");
        corpoBuilder.append("left outer join tb_anexo_autorizacao_desconto aad on (aad.ade_codigo = ade.ade_codigo and aad.aad_ativo = 1) ");
        corpoBuilder.append("where ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        String codigoEntidade = responsavel.getCodigoEntidade();
        if (!responsavel.isCseSup()) {
            if (responsavel.isOrg()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    codigoEntidade = responsavel.getEstCodigo();
                    corpoBuilder.append(" and est.est_codigo ");
                } else {
                    corpoBuilder.append(" and org.org_codigo ");
                }
            } else if (responsavel.isCor()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    codigoEntidade = responsavel.getCsaCodigo();
                    corpoBuilder.append(" and csa.csa_codigo ");
                } else {
                    corpoBuilder.append(" and ade.cor_codigo ");
                }
            } else if (responsavel.isCsa()) {
                corpoBuilder.append(" and csa.csa_codigo ");
            }
            corpoBuilder.append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and cor.cor_codigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (adeDataIni != null) {
            corpoBuilder.append(" and ade.ade_data >= :adeDataIni ");
        }

        if (adeDataFim != null) {
            corpoBuilder.append(" and ade.ade_data <= :adeDataFim ");
        }

        if (adeNumero != null && adeNumero.size() > 0) {
            corpoBuilder.append(" and ade.ade_numero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        corpoBuilder.append(ListaServidorQuery.gerarClausulaNativaMatriculaCpf(rseMatricula, serCpf, true));

        // Default do parâmetro aqui é 1, ou seja, tem que listar somente ADEs que tenham pelo menos 1 anexo
        corpoBuilder.append(" and ( ");
        corpoBuilder.append("  select count(*) from tb_anexo_autorizacao_desconto aad2 ");
        corpoBuilder.append("   where aad2.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("     and aad2.aad_ativo = 1 ");
        corpoBuilder.append(") ");
        corpoBuilder.append(pendenciaAnexo ? "<" : ">=");
        corpoBuilder.append(" to_numeric(coalesce(nullif(pse.pse_vlr_ref, ''), '1')) ");
        
        if(pendenciaAnexo) {
            corpoBuilder.append(" and pse.pse_vlr_ref is not null");
        }

        if (!count) {
            corpoBuilder.append(" group by ").append(campos);
            corpoBuilder.append(" order by 1 desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, true, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!responsavel.isCseSup()) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (adeDataIni != null) {
            defineValorClausulaNomeada("adeDataIni", adeDataIni, query);
        }

        if (adeDataFim != null) {
            defineValorClausulaNomeada("adeDataFim", adeDataFim, query);
        }

        if (adeNumero != null && adeNumero.size() > 0) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.AAD_DATA,
                Columns.ADE_CODIGO,
                Columns.ADE_DATA,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_INDICE,
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.SAD_DESCRICAO,
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
         };
    }
}
