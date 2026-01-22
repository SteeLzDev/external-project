package com.zetra.econsig.persistence.dialect;

import org.hibernate.query.sqm.FetchClauseType;

/**
 * <p>Title: CustomOracleDialect</p>
 * <p>Description: Customização do Hibernate para Oracle anterior ao 12.2.</p>
 * <p>Copyright: Copyright (c) 2013-2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Marcos Nolasco
 */
public class CustomOracleDialectLegacy extends CustomOracleDialect {

    @Override
    public boolean supportsFetchClause(FetchClauseType type) {
        // Existe um erro na versão 12.1 do Oracle ao utilizar fetch nas clausulas, e como foi corrigido na versão 12.2 e ainda não estamos nesta versão
        // precisei sobrepor este método passando ele como false. Para não dar erro de ambiguidade ORA-00918
        return false;
    }

}
