package com.zetra.econsig.webservice.command.entrada.v7;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: AlongarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de alongar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DesliquidarConsignacaoCommand extends RequisicaoExternaCommand {

    public DesliquidarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        Long adeNumero = (Long) parametros.get(ADE_NUMERO);
        String adeIdentificador = (String) parametros.get(ADE_IDENTIFICADOR);
        if (adeNumero == null && TextHelper.isNull(adeIdentificador)) {
            throw new ZetraException("mensagem.informe.ade.numero.ou.identificador", responsavel);
        }

    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);

    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        List<TransferObject> consignacao = (List<TransferObject>) parametros.get(CONSIGNACAO);

        ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
        String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);
        String obsMotivoOperacao = (String) parametros.get(TMO_OBS);

        CustomTransferObject tipoMotivoOperacao = null;
        if (!TextHelper.isNull(tmoIdentificador)) {
            tipoMotivoOperacao = new CustomTransferObject();
            tipoMotivoOperacao.setAttribute(Columns.TMO_IDENTIFICADOR, tmoIdentificador);
            tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, obsMotivoOperacao);
        }

        if (consignacao != null && !consignacao.isEmpty()) {

            TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            consigDelegate.desliquidarConsignacao(adeCodigo, tipoMotivoOperacao, responsavel);
            buscarAutorizacao(adeCodigo, parametros);

        }

    }

    /**
     * recupera informações da reserva recém criada para enviar como resposta da requisição externa
     * @param parametros
     * @throws ZetraException
     */
    private void buscarAutorizacao(String adeCodigo, Map<CamposAPI, Object> parametros) throws ZetraException {

        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        // Busca a nova autorização
        TransferObject novaAutorizacao = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

        String serCodigo = (String) novaAutorizacao.getAttribute(Columns.SER_CODIGO);
        String orgCodigo = (String) novaAutorizacao.getAttribute(Columns.ORG_CODIGO);

        // Busca o servidor
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = serDelegate.findServidor(servidor, responsavel);
        // Pega a descrição do codigo de estado civil
        String serEstCivil = serDelegate.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);

        CustomTransferObject boleto = new CustomTransferObject();
        boleto.setAtributos(servidor.getAtributos()); // Adiciona Informações do servidor
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil); // Adiciona a Descrição do estado civil
        boleto.setAtributos(orgao.getAtributos()); // Adiciona Informações do órgão
        boleto.setAtributos(novaAutorizacao.getAtributos()); // Adiciona Informações da autorização

        // Guarda o boleto no hash para ser consultada na geração do resultado
        parametros.put(CONSIGNACAO, boleto);
    }

}
