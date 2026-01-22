package com.zetra.econsig.persistence.query.anexo;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAnexoAdeMovFinQuery</p>
 * <p>Description: Lista informações dos anexos de contratos do período a serem enviados à folha
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAnexoAdeMovFinQuery extends HNativeQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public List<String> verbas;

    @Override
    public void setCriterios(TransferObject criterio) {
        estCodigos = (List<String>) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> tocCodigos = new ArrayList<>();
        tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
        tocCodigos.add(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO);

        boolean incluiTodosAnexosPeriodo = ParamSist.paramEquals(CodedValues.TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN_TODOS_PERIODO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        String campos =
                    "tmp.situacao, " +
                    "coalesce(nullif(tmp.cnv_cod_verba_ref, ''), tmp.cnv_cod_verba) as cod_verba, " +
                    "tmp.ser_cpf, " +
                    "aad.aad_data, " +
                    "ade.ade_numero, " +
                    "ade.ade_data, " +
                    "aad.aad_nome, " +
                    "ade.ade_codigo, " +
                    "ade.ade_indice, " +
                    "ade.ade_identificador, " +
                    "tmp.rse_matricula, " +
                    "tmp.ser_nome";

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ").append(campos);
        corpoBuilder.append(" from tb_anexo_autorizacao_desconto aad");
        corpoBuilder.append(" inner join tb_aut_desconto ade on (ade.ade_codigo = aad.ade_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        corpoBuilder.append(" inner join tb_orgao org on (cnv.org_codigo = org.org_codigo)");
        corpoBuilder.append(" inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo)");
        if (!incluiTodosAnexosPeriodo) {
            corpoBuilder.append(" inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo)");
        }
        corpoBuilder.append(" inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo)");
        corpoBuilder.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        corpoBuilder.append(" inner join tb_tmp_exportacao_ordenada tmp on (tmp.cnv_cod_verba = cnv.cnv_cod_verba ");

        // Quando o sistema consolida não podemos utilizar o svc_identificador como chave, pois podem existir serviços sem anexos fruta da consolidação, pode acontecer de fazer o mapeamento com o contrato
        // a partir do serviço sem anexo.
        if (!ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and tmp.svc_identificador = svc.svc_identificador ");
        }
        corpoBuilder.append(" and tmp.csa_identificador = csa.csa_identificador and tmp.est_identificador = est.est_identificador and tmp.org_identificador = org.org_identificador and tmp.rse_matricula = rse.rse_matricula)");
        corpoBuilder.append(" where 1=1");

        if (!incluiTodosAnexosPeriodo) {
            // É um contrato incluído/alterado/reimplantado no período
            corpoBuilder.append(" and oca.oca_periodo = pex.pex_periodo");
            corpoBuilder.append(" and oca.toc_codigo in ('").append(TextHelper.join(tocCodigos, "','")).append("')");
            // Que possui anexo entre a data da operação e a data fim do período
            corpoBuilder.append(" and (aad.aad_data between least(pex.pex_data_ini, oca.oca_data) and pex.pex_data_fim");
            // Quando o sistema valida documentos (param 880) e limita o anexo na exportação, é preciso verificar se o anexo é do periodo, além de verificar a data.
            if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, AcessoSistema.getAcessoUsuarioSistema())) {
                corpoBuilder.append(" or (aad.aad_periodo = pex.pex_periodo) ");
            }
            corpoBuilder.append(" ) ");
        } else {
            corpoBuilder.append(" and (aad.aad_periodo = pex.pex_periodo or aad.aad_data between pex.pex_data_ini and pex.pex_data_fim)");
        }
        corpoBuilder.append(" and aad.aad_ativo = 1");
        corpoBuilder.append(" and concat('.',substring_index(aad.aad_nome, '.', -1)) in ('").append(TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO_INTEGRACAO, "','")).append("')");

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" and org.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (verbas != null && !verbas.isEmpty()) {
            corpoBuilder.append(" and cnv.cnv_cod_verba ").append(criaClausulaNomeada("cnvCodVerba", verbas));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and not exists (");
            corpoBuilder.append("    select 1 ");
            corpoBuilder.append("    from tb_tmp_contratos_sem_permissao tmp ");
            corpoBuilder.append("    where tmp.ade_codigo = ade.ade_codigo ");
            corpoBuilder.append(") ");
        }

        corpoBuilder.append(" and exists (select 1 from tb_tmp_exportacao tte where tte.ade_codigo=ade.ade_codigo) ");

        corpoBuilder.append(" order by tmp.situacao ASC, cod_verba ASC, tmp.ser_cpf ASC, tmp.ade_numero ASC, aad.aad_data DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (verbas != null && !verbas.isEmpty()) {
            defineValorClausulaNomeada("cnvCodVerba", verbas, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "SITUACAO",
                Columns.CNV_COD_VERBA,
                Columns.SER_CPF,
                Columns.AAD_DATA,
                Columns.ADE_NUMERO,
                Columns.ADE_DATA,
                Columns.AAD_NOME,
                Columns.ADE_CODIGO,
                Columns.ADE_INDICE,
                Columns.ADE_IDENTIFICADOR,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME
         };
    }
}
