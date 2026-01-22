package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: RelatorioDocumentoBeneficiarioTipoValidadeQuery</p>
 * <p>Description: Query usada para gerar relatório de documentos do benefíciario por tipo e validade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioDocumentoBeneficiarioTipoValidadeQuery extends ReportHQuery {
    public String dataIni;

    public String dataFim;

    public List<String> tipoDocumento;

    public List<String> tarCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("dataIni");
        dataFim = (String) criterio.getAttribute("dataFim");
        tipoDocumento = (List<String>) criterio.getAttribute("tipoDocumento");

        List<String> tipoArquivoCodigos = new ArrayList<String>();
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_RG.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_CPF.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_COMPROVANTE_RESIDENCIA.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_CASAMENTO.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_UNIAO_ESTAVEL.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_NASCIMENTO.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_MATRICULA_FREQUENCIA_ESCOLAR.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_TUTELA_CURATELA.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_CARENCIA.getCodigo());
        tipoArquivoCodigos.add(TipoArquivoEnum.ARQUIVO_ATESTADO_MEDICO.getCodigo());

        tarCodigos = tipoArquivoCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT rse.rseMatricula as rse_matricula, ser.serNome as ser_nome, bfc.bfcCpf as bfc_cpf, ");
        corpoBuilder.append(" bfc.bfcNome as bfc_nome, bfc.bfcTelefone as bfc_telefone, ");
        corpoBuilder.append(" tar.tarDescricao as tar_descricao, ");
        corpoBuilder.append(" abf.abfDataValidade as abf_data_validade ");
        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.tipoLancamento tla ");
        corpoBuilder.append(" inner join tla.tipoNatureza tnt ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join ade.contratoBeneficio cbe ");
        corpoBuilder.append(" inner join cbe.beneficiario bfc ");
        corpoBuilder.append(" inner join bfc.servidor ser ");
        corpoBuilder.append(" inner join bfc.anexoBeneficiarioSet abf ");
        corpoBuilder.append(" inner join abf.tipoArquivo tar ");
        corpoBuilder.append(" WHERE abf.abfDataValidade between :dataIni and :dataFim ");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo IN (:sadCodigo) ");
        corpoBuilder.append(" AND tnt.tntCodigo in (:tntCodigo) ");

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            corpoBuilder.append("AND tar.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tipoDocumento));
        } else {
            corpoBuilder.append("AND tar.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);

        if (!TextHelper.isNull(tipoDocumento)) {
            defineValorClausulaNomeada("tarCodigo", tipoDocumento, query);
        } else {
            defineValorClausulaNomeada("tarCodigo", tarCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.RSE_MATRICULA, Columns.SER_NOME, Columns.BFC_CPF, Columns.BFC_NOME, Columns.BFC_TELEFONE, Columns.TAR_DESCRICAO, Columns.ABF_DATA_VALIDADE };
    }
}
