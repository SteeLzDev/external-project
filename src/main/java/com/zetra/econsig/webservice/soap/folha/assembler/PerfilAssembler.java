package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.PER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DATA_EXPIRACAO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.STATUS;

import java.util.Date;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.folha.v1.Perfil;

/**
 * <p>Title: PerfilUsuarioAssembler</p>
 * <p>Description: Assembler para PerfilUsuario.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class PerfilAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PerfilAssembler.class);

    private PerfilAssembler() {
    }

    public static Perfil toPerfilV1(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final ObjectFactory factory = new ObjectFactory();
        final Perfil perfil = new Perfil();

        perfil.setCodigo(paramResposta.get(PER_CODIGO).toString());
        perfil.setDescricao(paramResposta.get(PER_DESCRICAO).toString());
        try {
            perfil.setDataExpiracao(factory.createPerfilDataExpiracao(toXMLGregorianCalendar((Date) paramResposta.get(PER_DATA_EXPIRACAO), false)));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn(e.getMessage(), e);
        }
        perfil.setAtivo(factory.createPerfilAtivo((short) paramResposta.get(STATUS)));

        return perfil;
    }
}