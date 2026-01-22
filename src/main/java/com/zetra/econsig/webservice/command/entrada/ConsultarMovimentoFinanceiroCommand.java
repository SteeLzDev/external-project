package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.MOVIMENTO_FINANCEIRO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarMovimentoFinanceiroCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig para consultar movimento financeiro</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarMovimentoFinanceiroCommand extends RequisicaoExternaFolhaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarMovimentoFinanceiroCommand.class);

    public ConsultarMovimentoFinanceiroCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String periodo = (String) parametros.get(PERIODO);
        String rseMatricula = (String) parametros.get(RSE_MATRICULA);
        String serCpf = (String) parametros.get(SER_CPF);
        String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
        String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);
        String csaIdentificador = (String) parametros.get(CSA_IDENTIFICADOR);
        String svcIdentificador = (String) parametros.get(SERVICO_CODIGO);
        String cnvCodVerba = (String) parametros.get(CNV_COD_VERBA);

        if (TextHelper.isNull(periodo)) {
            LOG.warn("O parâmetro de período é obrigatório para a consulta.");
            throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
        }

        ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();
        List<TransferObject> movimento = expDelegate.consultarMovimentoFinanceiro(periodo, rseMatricula, serCpf, orgIdentificador, estIdentificador, csaIdentificador, svcIdentificador, cnvCodVerba, responsavel);
        if (movimento != null && !movimento.isEmpty()) {
            parametros.put(MOVIMENTO_FINANCEIRO, movimento);
        } else {
            throw new ZetraException("mensagem.erro.nenhum.registro.encontrado", responsavel);
        }
    }
}
