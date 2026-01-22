package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarConsignacaoParaCompraCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar consignação para compra</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarConsignacaoParaCompraCommand extends ConsultarConsignacaoCommand {

    public ConsultarConsignacaoParaCompraCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaCpfMatricula(parametros);
        validaCodigoVerba(parametros);

        if (TextHelper.isNull(parametros.get(SERVICO_CODIGO))) {
            throw new ZetraException("mensagem.informe.svc.identificador", responsavel);
        }
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);

        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final String serSenha = (String)parametros.get(SER_SENHA);
        final String token = (String) parametros.get(TOKEN);
        final String loginExterno = (String) parametros.get(SER_LOGIN);
        final String svcCodigo = (String) parametros.get(SVC_CODIGO);

        // Verifica a exigência ou não da senha do servidor e/ou informações bancárias.
        final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(svcCodigo, rseCodigo, responsavel);
        boolean exigeSenhaAcesso = (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra));
        boolean exigeInfBancaria = (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra));

        final String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
        final boolean csaValidaSenha = (TextHelper.isNull(tpaCsaValidaSenha) || CodedValues.TPA_NAO.equals(tpaCsaValidaSenha)) == false;

        if (exigeSenhaAcesso) {
            // Na consulta de consignação para a compra, deverá validar a segunda senha de autorização
            validaPresencaSenhaServidor(parametros);
            validarSenhaServidor(rseCodigo, serSenha, true, loginExterno, csaCodigo, token, responsavel);
        } else if((!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) && csaValidaSenha){
            validarSenhaServidor(rseCodigo, serSenha, true, loginExterno, csaCodigo, token, responsavel);
        }

        if (exigeInfBancaria) {
            final ServidorDelegate serDelegate = new ServidorDelegate();
            RegistroServidorTO rseTO = null;

            if (TextHelper.isNull(rseCodigo)) {
                final List<TransferObject> autList = (List<TransferObject>) parametros.get(CONSIGNACAO);

                if ((autList == null) || autList.isEmpty()) {
                    throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
                }

                final TransferObject aut = autList.get(0);
                final TransferObject serTO = serDelegate.findServidorProprietarioAde((String) aut.getAttribute(Columns.ADE_CODIGO), responsavel);

                if (serTO == null) {
                    throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
                }

                rseTO = new RegistroServidorTO();
                rseTO.setRseBancoSal((String) serTO.getAttribute(Columns.RSE_BANCO_SAL));
                rseTO.setRseAgenciaSal((String) serTO.getAttribute(Columns.RSE_AGENCIA_SAL));
                rseTO.setRseContaSal((String) serTO.getAttribute(Columns.RSE_CONTA_SAL));
                rseTO.setRseBancoSalAlternativo((String) serTO.getAttribute(Columns.RSE_BANCO_SAL_2));
                rseTO.setRseAgenciaSalAlternativa((String) serTO.getAttribute(Columns.RSE_AGENCIA_SAL_2));
                rseTO.setRseContaSalAlternativa((String) serTO.getAttribute(Columns.RSE_CONTA_SAL_2));
            } else {
                rseTO = serDelegate.findRegistroServidor(rseCodigo, responsavel);
            }

            validarDadosBancariosServidor(true, true, (String) parametros.get(RSE_BANCO), (String) parametros.get(RSE_AGENCIA), (String) parametros.get(RSE_CONTA), rseTO);
        }
    }
}
