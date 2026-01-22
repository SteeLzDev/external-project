-- DESENV-18059
-- MYSQL
INSERT INTO tb_regra_validacao_ambiente (REA_CODIGO, REA_DESCRICAO, REA_ATIVO, REA_DATA_CADASTRO, REA_JAVA_CLASS_NAME, REA_SEQUENCIA, REA_BLOQUEIA_SISTEMA) 
VALUES ('8', 'Perfil servidor possui permissões necessárias para app SalaryFits', 0, NOW(), 'com.zetra.econsig.helper.validacaoambiente.RegraValidacaoPermissoesUsuarioSalaryPay', 5, 1);

