package com.zetra.econsig.persistence.query.anexo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaAnexoContratoQuery extends HNativeQuery{
	
	public List<String> csaCodigos;
    public List<String> svcCodigos;
    public List<String> sadCodigos;
    public Date aadDataIni;
    public Date aadDataFim;

    @Override
    public void setCriterios(TransferObject criterio) {
    	
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> tocCodigos = new ArrayList<>();
        tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
        tocCodigos.add(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO);

        String campos =
                "sad.sad_descricao, " +
                "coalesce(nullif(cnv.cnv_cod_verba_ref, ''), cnv.cnv_cod_verba) as cod_verba, " +
                "ser.ser_cpf, " +
                "aad.aad_data, " +
                "ade.ade_numero, " +
                "ade.ade_data, " +
                "aad.aad_nome, " +
                "ade.ade_codigo, " +
                "ade.ade_indice, " +
                "ade.ade_identificador, " +
                "rse.rse_matricula, " +
                "ser.ser_nome";

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ").append(campos);
        corpoBuilder.append(" from tb_anexo_autorizacao_desconto aad");
        corpoBuilder.append(" inner join tb_aut_desconto ade on (ade.ade_codigo = aad.ade_codigo)");
        corpoBuilder.append(" inner join tb_status_autorizacao_desconto sad on (ade.sad_codigo = sad.sad_codigo)");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
        corpoBuilder.append(" inner join tb_orgao org on (cnv.org_codigo = org.org_codigo)");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo)");
        corpoBuilder.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
        corpoBuilder.append(" where 1 = 1");
        
        corpoBuilder.append(" and aad.aad_ativo = 1");
        corpoBuilder.append(" and concat('.',substring_index(aad.aad_nome, '.', -1)) in ('").append(TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO_INTEGRACAO, "','")).append("')");

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            corpoBuilder.append(" and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }
        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (aadDataIni != null && aadDataFim != null) {
        	corpoBuilder.append(" and aad.aad_data between :aadDataIni and :aadDataFim");
        }

        corpoBuilder.append(" order by sad.sad_descricao ASC, cod_verba ASC, ser.ser_cpf ASC, ade.ade_numero ASC, aad.aad_data DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        
        if (aadDataIni != null && aadDataFim != null) {
            defineValorClausulaNomeada("aadDataIni", aadDataIni, query);
            defineValorClausulaNomeada("aadDataFim", aadDataFim, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SAD_DESCRICAO,
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
