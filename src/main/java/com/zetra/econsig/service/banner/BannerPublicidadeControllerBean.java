package com.zetra.econsig.service.banner;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BannerPublicidadeControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.banner.ListaBannerPublicidadeQuery;
import com.zetra.econsig.values.Columns;

@Service
@Transactional
public class BannerPublicidadeControllerBean implements BannerPublicidadeController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BannerPublicidadeControllerBean.class);

    @Override
    public int countBannerPublicidade(CustomTransferObject criterio, AcessoSistema responsavel) throws BannerPublicidadeControllerException {
        try {
            ListaBannerPublicidadeQuery query = new ListaBannerPublicidadeQuery();
            query.count = true;
            query.responsavel = responsavel;

            if (criterio != null) {
                query.nseCodigo = (String) criterio.getAttribute(Columns.BPU_NSE_CODIGO);
            }

            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BannerPublicidadeControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarBannerPublicidade(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws BannerPublicidadeControllerException {
        try {
            ListaBannerPublicidadeQuery query = new ListaBannerPublicidadeQuery();
            query.responsavel = responsavel;

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.nseCodigo = (String) criterio.getAttribute(Columns.BPU_NSE_CODIGO);
                query.exibeMobile = (String) criterio.getAttribute(Columns.BPU_EXIBE_MOBILE);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BannerPublicidadeControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}