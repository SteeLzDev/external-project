package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.GrupoConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarConsignatariaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cadastrar consignatária</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarConsignatariaCommand extends RequisicaoExternaFolhaCommand {

    public CadastrarConsignatariaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ConsignatariaTransferObject consignataria = (ConsignatariaTransferObject) parametros.get(CONSIGNATARIA);
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        consignataria.setCsaObsContrato(!TextHelper.isNull(consignataria.getCsaObsContrato()) ? consignataria.getCsaObsContrato() : null);
        consignataria.setCsaDataInicioContrato(!TextHelper.isNull(consignataria.getCsaDataInicioContrato()) ? consignataria.getCsaDataInicioContrato() : null);
        consignataria.setCsaNumeroProcessoContrato(!TextHelper.isNull(consignataria.getCsaNumeroProcessoContrato()) ? consignataria.getCsaNumeroProcessoContrato() : null);
        consignataria.setCsaDataRenovacaoContrato(!TextHelper.isNull(consignataria.getCsaDataRenovacaoContrato()) ? consignataria.getCsaDataRenovacaoContrato() : null);
        // Criando nova consignataria.
        csaDelegate.createConsignataria(consignataria, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        ConsignatariaTransferObject consignataria = (ConsignatariaTransferObject) parametros.get(CONSIGNATARIA);
        String csaIpAcesso = consignataria.getCsaIPAcesso();

        boolean permiteCadIpInternoCsaCor = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_IP_REDE_INTERNA_CSA_COR, responsavel);

        // Valida a lista de IPs.
        if (!permiteCadIpInternoCsaCor && !TextHelper.isNull(csaIpAcesso)) {
            List<String> ipsAcesso = Arrays.asList(csaIpAcesso.split(";"));
            if (!JspHelper.validaListaIps(ipsAcesso)) {
                throw new ZetraException("mensagem.erro.ip.invalido", responsavel);
            }
        }

        // Valida se a natureza passada existe
        if (!TextHelper.isNull(consignataria.getCsaNcaNatureza())) {
            try {
                NaturezaConsignatariaEnum.recuperaNaturezaConsignataria(consignataria.getCsaNcaNatureza());
            } catch (IllegalArgumentException ex) {
                throw new ZetraException("mensagem.erro.natureza.consignataria.nao.encontrada", responsavel, ex);
            }
        }

        // Valida se o grupo passado existe
        if (!TextHelper.isNull(consignataria.getTgcIdentificador())) {
            try {
                ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                GrupoConsignatariaTransferObject grupoCsa = csaDelegate.findGrupoCsaByIdentificador(consignataria.getTgcIdentificador());
                consignataria.setTgcCodigo(grupoCsa.getGrupoCsaCodigo());
            } catch (ConsignatariaControllerException ex) {
                throw new ZetraException("mensagem.erro.grupo.consignataria.nao.encontrada", responsavel, ex);
            }
        }
    }
}
