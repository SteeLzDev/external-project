package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.BANCOS;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNANTE;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Consignante;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarConsignante;

/**
 * <p>Title: ModificarConsignanteAssembler</p>
 * <p>Description: Assembler para ModificarConsignante.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ModificarConsignanteAssembler extends BaseAssembler {

    private ModificarConsignanteAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ModificarConsignante modificarConsignante) {
        final ConsignanteTransferObject cseTO = new ConsignanteTransferObject();
        final Consignante consignante = modificarConsignante.getConsignante();
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        cseTO.setCseIdentificador(consignante.getCodigo());
        cseTO.setCseNome(consignante.getNome());
        cseTO.setCseCnpj(consignante.getCnpj());
        cseTO.setCseEmail(getValue(consignante.getEmail()));
        cseTO.setCseEmailFolha(getValue(consignante.getEmailFolha()));
        cseTO.setCseResponsavel(getValue(consignante.getResponsavel()));
        cseTO.setCseRespCargo(getValue(consignante.getCargoResponsavel()));
        cseTO.setCseRespTelefone(getValue(consignante.getTelefoneResponsavel()));
        cseTO.setCseResponsavel2(getValue(consignante.getResponsavel2()));
        cseTO.setCseRespCargo2(getValue(consignante.getCargoResponsavel2()));
        cseTO.setCseRespTelefone2(getValue(consignante.getTelefoneResponsavel2()));
        cseTO.setCseResponsavel3(getValue(consignante.getResponsavel3()));
        cseTO.setCseRespCargo3(getValue(consignante.getCargoResponsavel3()));
        cseTO.setCseRespTelefone3(getValue(consignante.getTelefoneResponsavel3()));
        cseTO.setCseLogradouro(consignante.getLogradouro().getValue());
        final Integer numero = getValue(consignante.getNumero());
        if ((numero != null) && (numero != Integer.MAX_VALUE) && (numero > 0)) {
            cseTO.setCseNro(numero);
        }
        cseTO.setCseCompl(getValue(consignante.getComplemento()));
        cseTO.setCseBairro(getValue(consignante.getBairro()));
        cseTO.setCseCidade(getValue(consignante.getCidade()));
        cseTO.setCseUf(getValue(consignante.getUf()));
        cseTO.setCseCep(getValue(consignante.getCep()));
        cseTO.setCseTel(getValue(consignante.getTelefone()));
        cseTO.setCseFax(getValue(consignante.getFax()));
        final Short ativo = getValue(consignante.getAtivo());
        if ((ativo != null) && (ativo != Short.MAX_VALUE) && (ativo > 0)) {
            cseTO.setCseAtivo(ativo);
        }
        cseTO.setCseIPAcesso(getValue(consignante.getIpAcesso()));
        cseTO.setCseDDNSAcesso(getValue(consignante.getDdnsAcesso()));
        cseTO.setCseFolha(getValue(consignante.getCodigoFolha()));
        final Date dataCobranca = getValueAsDate(consignante.getDataCobranca());
        if (dataCobranca != null) {
            cseTO.setCseDataCobranca(DateHelper.toSQLDate(dataCobranca));
        }
        cseTO.setTipoConsignante(getValue(consignante.getTipoConsignante()));
        cseTO.setCseSistemaFolha(getValue(consignante.getSistemaFolha()));
        cseTO.setIdentificadorInterno(getValue(consignante.getIdentificadorInterno()));

        if (!consignante.getBcoCodigo().isEmpty()) {
            parametros.put(BANCOS, consignante.getBcoCodigo());
        }

        parametros.put(CONSIGNANTE, cseTO);
        parametros.put(USUARIO, modificarConsignante.getUsuario());
        parametros.put(SENHA, modificarConsignante.getSenha());

        return parametros;
    }
}