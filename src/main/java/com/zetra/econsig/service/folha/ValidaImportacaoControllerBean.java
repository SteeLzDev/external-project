package com.zetra.econsig.service.folha;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.arquivo.ListaParametroValidacaoArquivoQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ValidaImportacaoControllerBean</p>
 * <p>Description: Implementação do EJB bean do controller de Valida Importação.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ValidaImportacaoControllerBean implements ValidaImportacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaImportacaoControllerBean.class);

    @Override
    public Map<String, String> lstParamValidacaoArq(String tipoEntidade, String codigoEntidade, List<String> tvaCodigos, List<String> tvaChaves, AcessoSistema responsavel) throws ValidaImportacaoControllerException {
        try {
            ListaParametroValidacaoArquivoQuery query = new ListaParametroValidacaoArquivoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.tvaCodigos = tvaCodigos;
            query.tvaChaves = tvaChaves;

            Map<String, String> parametros = new HashMap<>();
            List<TransferObject> lista = query.executarDTO();
            if (lista != null && !lista.isEmpty()) {
                for (TransferObject to : lista) {
                    parametros.put(to.getAttribute(Columns.TVA_CHAVE).toString(), to.getAttribute("VALOR").toString());
                }
            }

            return parametros;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ValidaImportacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
