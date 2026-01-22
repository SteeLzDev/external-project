package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: NacionalidadeHome</p>
 * <p>Description: Classe home da entidade Nacionalidade</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NacionalidadeHome extends AbstractEntityHome {

    public static Nacionalidade findByPrimaryKey(String nacCodigo) throws FindException {
        Nacionalidade nacionalidade = new Nacionalidade();
        nacionalidade.setNacCodigo(nacCodigo);

        return find(nacionalidade, nacCodigo);
    }

}
