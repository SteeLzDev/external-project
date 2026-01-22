package com.zetra.econsig.service.beneficios;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ProvedorBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.beneficios.provedor.ListaProvedorBeneficioQuery;
import com.zetra.econsig.persistence.query.beneficios.provedor.ListarProvedorBeneficioCorEmAreaGeograficaQuery;
import com.zetra.econsig.persistence.query.beneficios.provedor.ListarProvedorBeneficioCsaEmAreaGeograficaQuery;

/**
 * <p>Title: ProvedorBeneficioControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ProvedorBeneficioControllerBean implements ProvedorBeneficioController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProvedorBeneficioControllerBean.class);

    @Override
    public TransferObject buscarProvedorBeneficioPorProCodigo (String proCodigo) throws ProvedorBeneficioControllerException {

        try {

            ListaProvedorBeneficioQuery listaProvedorBeneficioQuery = new ListaProvedorBeneficioQuery();
            listaProvedorBeneficioQuery.proCodigo = proCodigo;
            List<TransferObject> provedorBeneficios = listaProvedorBeneficioQuery.executarDTO();

            if (!provedorBeneficios.isEmpty()) {
                return provedorBeneficios.get(0);
            }

            return null;

        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ProvedorBeneficioControllerException(e);
        }

    }

    /**
     * retorna provedores de benefício de uma natureza de serviço dada (opcional) dentro de um raio a partir do ponto de referência dado por latitude e longitude.
     * @param latitude - latitude de referência inicial do perímetro
     * @param longitude - longitude de referência inicial do perímetro
     * @param raioMax - Distância máxima para retorno de provedores em relação à referência inicial
     * @param nseCodigos -
     */
    @Override
    public List<TransferObject> listarProvedorBeneficioEmPerimetro(Float latitude, Float longitude, Float raioMax, List<String> nseCodigos, String chaveBuscaBen, AcessoSistema responsavel) throws ProvedorBeneficioControllerException {
        ListarProvedorBeneficioCsaEmAreaGeograficaQuery provCsaList = new ListarProvedorBeneficioCsaEmAreaGeograficaQuery();
        ListarProvedorBeneficioCorEmAreaGeograficaQuery provCorList = new ListarProvedorBeneficioCorEmAreaGeograficaQuery();
        List<TransferObject> result = new ArrayList<>();

        provCsaList.latReferencia = latitude;
        provCsaList.longReferencia = longitude;
        provCsaList.raioAlcance = raioMax;
        provCsaList.nseCodigos = nseCodigos;
        provCsaList.textoBusca = chaveBuscaBen;
        provCsaList.orgCodigo = responsavel.getOrgCodigo();

        try {
            result = provCsaList.executarDTO();

            provCorList.latReferencia = latitude;
            provCorList.longReferencia = longitude;
            provCorList.raioAlcance = raioMax;
            provCorList.nseCodigos = nseCodigos;
            provCorList.textoBusca = chaveBuscaBen;
            provCorList.orgCodigo = responsavel.getOrgCodigo();

            if (result != null && !result.isEmpty()) {
                result.addAll(provCorList.executarDTO());
            } else {
                result = provCorList.executarDTO();
            }

        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ProvedorBeneficioControllerException(e);
        }
        return result;
    }

    @Override
    public List<TransferObject> buscarProvedorBeneficioPorCsaCodigoAgrupa (String csaCodigo) throws ProvedorBeneficioControllerException {
        try {
            ListaProvedorBeneficioQuery listaProvedorBeneficioQuery = new ListaProvedorBeneficioQuery();
            listaProvedorBeneficioQuery.csaCodigo = csaCodigo;

            return listaProvedorBeneficioQuery.executarDTO();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new ProvedorBeneficioControllerException(e);
        }

    }

}
