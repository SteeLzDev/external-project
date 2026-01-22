-- DESENV-9787

-- Cria a função 450 para consultar correspondente
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('450', '10', 'Consultar Correspondente', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('4', '450');

-- Inclui a função 450 para perfis de correspondentes que ja possuiam a função 86
INSERT INTO tb_funcao_perfil (FUN_CODIGO, PER_CODIGO) SELECT '450', PER_CODIGO FROM tb_funcao_perfil WHERE PER_CODIGO IN (SELECT PER_CODIGO FROM tb_perfil_cor) AND FUN_CODIGO = '86';

-- Inclui a função 450 para perfis personalizados de correspondentes.
INSERT INTO tb_funcao_perfil_cor (COR_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT COR_CODIGO, USU_CODIGO, '450' FROM tb_funcao_perfil_cor WHERE FUN_CODIGO = '86';

-- Altera o acesso recurso da função 86 quando a ação é editar para a nova função 450
UPDATE tb_acesso_recurso SET FUN_CODIGO = '450' where FUN_CODIGO = '86' and ACR_OPERACAO = 'consultar' and PAP_CODIGO = '4';
