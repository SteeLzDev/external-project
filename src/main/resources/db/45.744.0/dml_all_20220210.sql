-- DESENV-17185
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO IN ('233','234','235'));
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE FUN_CODIGO IN ('233','234','235'));
DELETE FROM tb_acesso_recurso WHERE FUN_CODIGO IN ('233','234','235');

DELETE FROM tb_funcao_auditavel_cse WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_auditavel_org WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_auditavel_sup WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_auditavel_csa WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_auditavel_cor WHERE FUN_CODIGO IN ('233','234','235');

DELETE FROM tb_funcao_perfil_cse WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_perfil_org WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_perfil_sup WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_perfil_csa WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_funcao_perfil_cor WHERE FUN_CODIGO IN ('233','234','235');

DELETE FROM tb_funcao_perfil WHERE FUN_CODIGO IN ('233','234','235');
DELETE FROM tb_papel_funcao WHERE FUN_CODIGO IN ('233','234','235');
