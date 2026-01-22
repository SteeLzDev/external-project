

package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;
import org.hibernate.Session;

public class AnexoConsignatariaHome extends AbstractEntityHome{
    public static AnexoConsignataria findByPrimaryKey(String axcCodigo) throws FindException {
        AnexoConsignataria anexoConsignataria = new AnexoConsignataria();
        anexoConsignataria.setAxcCodigo(axcCodigo);
        return find(anexoConsignataria, axcCodigo);
    }

    public static AnexoConsignataria create(AnexoConsignataria anexoConsignataria) throws CreateException {
        Session session = SessionUtil.getSession();
        AnexoConsignataria bean = new AnexoConsignataria();
        String objectId = null;
        try{
            objectId = DBHelper.getNextId();
            bean.setAxcCodigo(objectId);
            bean.setAxcAtivo(CodedValues.AXC_ATIVO);
            bean.setAxcData(anexoConsignataria.getAxcData());
            bean.setAxcIpAcesso(anexoConsignataria.getAxcIpAcesso());
            bean.setUsuCodigo(anexoConsignataria.getUsuCodigo());
            bean.setCsaCodigo(anexoConsignataria.getCsaCodigo());
            bean.setAxcNome(anexoConsignataria.getAxcNome());
            bean.setTarCodigo(TipoArquivoEnum.ARQUIVO_CONSIGNATARIA.getCodigo());

            create(bean, session);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }finally {
            SessionUtil.closeSession(session);
        }
        return anexoConsignataria;
    }

    public void updateAnexo(AnexoConsignataria anexoConsignataria)throws UpdateException {
        try{
            update(anexoConsignataria);
        } catch (UpdateException e) {
            throw new UpdateException(e);
        }
    }

}