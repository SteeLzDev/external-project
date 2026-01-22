-- DESENV-18156
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE LIKE 'mensagem.erro.ambiente.%';

DELETE FROM tb_regra_validacao_ambiente WHERE REA_CODIGO IN ('2','3');

UPDATE tb_regra_validacao_ambiente SET REA_DESCRICAO = 'Versão 17 ou superior do Java' WHERE REA_CODIGO = '4';

INSERT INTO tb_regra_validacao_ambiente (REA_CODIGO, REA_DESCRICAO, REA_ATIVO, REA_DATA_CADASTRO, REA_JAVA_CLASS_NAME, REA_SEQUENCIA, REA_BLOQUEIA_SISTEMA)
VALUES ('2', 'Nível de transação em Read-Commited', 1, CURRENT_TIMESTAMP, 'com.zetra.econsig.helper.validacaoambiente.RegraValidacaoReadCommittedTxIsolation', 2, 1); 

INSERT INTO tb_regra_validacao_ambiente (REA_CODIGO, REA_DESCRICAO, REA_ATIVO, REA_DATA_CADASTRO, REA_JAVA_CLASS_NAME, REA_SEQUENCIA, REA_BLOQUEIA_SISTEMA)
VALUES ('6', 'Acesso ao banco de dados via ROOT', 1, CURRENT_TIMESTAMP, 'com.zetra.econsig.helper.validacaoambiente.RegraValidacaoAcessoRootBancoDados', 6, 0);

INSERT INTO tb_regra_validacao_ambiente (REA_CODIGO, REA_DESCRICAO, REA_ATIVO, REA_DATA_CADASTRO, REA_JAVA_CLASS_NAME, REA_SEQUENCIA, REA_BLOQUEIA_SISTEMA)
VALUES ('7', 'Acesso excessivo ao sistema de arquivos', 1, CURRENT_TIMESTAMP, 'com.zetra.econsig.helper.validacaoambiente.RegraValidacaoAcessoExcessivoSistemaArquivos', 7, 0);