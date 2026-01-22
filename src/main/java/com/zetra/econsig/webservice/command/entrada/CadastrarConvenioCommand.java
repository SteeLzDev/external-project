package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarConvenioCommand</p>
 * <p>Description: Classe que representa operação remota de cadastrar convênio via SOAP.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarConvenioCommand extends RequisicaoExternaFolhaCommand {

    public CadastrarConvenioCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        CustomTransferObject cnvTO = (CustomTransferObject) parametros.get(CONVENIO);

        String csaCodigo = (String) cnvTO.getAttribute(Columns.CSA_CODIGO);
        String svcIdentificador = (String) cnvTO.getAttribute(Columns.SVC_IDENTIFICADOR);
        String estIdentificador = (String) cnvTO.getAttribute(Columns.EST_IDENTIFICADOR);
        String orgIdentificador = (String) cnvTO.getAttribute(Columns.ORG_IDENTIFICADOR);

        ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        ServicoTransferObject svcTO = cnvDelegate.findServicoByIdn(svcIdentificador, responsavel);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.EST_IDENTIFICADOR, estIdentificador);
        criterio.setAttribute(Columns.ORG_IDENTIFICADOR, orgIdentificador);

        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        List<TransferObject> lstOrgaos = cseDelegate.lstOrgaos(criterio, responsavel);

        if (lstOrgaos.isEmpty() || lstOrgaos.size() == 0) {
            throw new ZetraException("mensagem.erro.orgao.nao.encontrado", responsavel);
        }

        TransferObject orgTO = lstOrgaos.get(0);

        cnvDelegate.createConvenio(svcTO.getSvcCodigo(), csaCodigo, (String) orgTO.getAttribute(Columns.ORG_CODIGO),
                (String) cnvTO.getAttribute(Columns.CNV_COD_VERBA), (String) cnvTO.getAttribute(Columns.CNV_COD_VERBA_REF),
                (String) cnvTO.getAttribute(Columns.CNV_COD_VERBA_FERIAS), responsavel);

    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        CustomTransferObject cnvTO = (CustomTransferObject) parametros.get(CONVENIO);
        String codVerba = (String) cnvTO.getAttribute(Columns.CNV_COD_VERBA);
        String csaIdentificador = (String) cnvTO.getAttribute(Columns.CSA_IDENTIFICADOR);

        if (TextHelper.isNull(cnvTO.getAttribute(Columns.CNV_COD_VERBA))) {
            throw new ZetraException("mensagem.informe.codigo.verba", responsavel);
        }

        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        ConsignatariaTransferObject csaTO = csaDelegate.findConsignatariaByIdn(csaIdentificador, responsavel);
        String csaCodigo = csaTO.getCsaCodigo();
        cnvTO.setAttribute(Columns.CSA_CODIGO, csaCodigo);

        // Verifica se o sistema permite código de verba duplicado
        boolean permiteRepetirCodVerba = ParamSist.paramEquals(CodedValues.TPC_PODE_REPETIR_COD_VERBA, CodedValues.TPC_SIM, responsavel);

        // Busca parâmetro a nivel de CSA para determinar se a verba pode estar duplicada
        List<TransferObject> paramCsaList = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_PODE_REPETIR_COD_VERBA, responsavel);
        if (!paramCsaList.isEmpty()) {
            TransferObject paramCsa = paramCsaList.get(0);
            String pcsVlr = (paramCsa != null ? (String) paramCsa.getAttribute(Columns.PCS_VLR) : null);
            if (!TextHelper.isNull(pcsVlr)) {
                permiteRepetirCodVerba = pcsVlr.equals("S");
            }
        }

        // Se não permite repetir verba, então verifica se já está sendo utilizada, e caso positivo, retorna erro
        if (!permiteRepetirCodVerba) {
            ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            List<String> listCsas = cnvDelegate.csaPorCodVerba(codVerba, csaCodigo);
            if (listCsas != null && !listCsas.isEmpty()) {
                throw new ZetraException("mensagem.erro.convenio.codigo.verba.utilizado", responsavel, codVerba, TextHelper.join(listCsas, ", "));
            }
        }
    }
}
