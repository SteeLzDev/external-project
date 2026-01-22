-- DESENV-13670
-- Novas funções Converter e Remover -> Arquivos Integração
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('481', '20', 'Converter Arquivos Integração', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('482', '20', 'Remover Arquivos Integração', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N');

-- Associação dos acessos recursos antigos antes vinculados a função 36 para excluir e converter:
UPDATE tb_acesso_recurso SET FUN_CODIGO = '481' WHERE ACR_CODIGO IN ('10006', '10007', '11931');
UPDATE tb_acesso_recurso SET FUN_CODIGO = '482' WHERE ACR_CODIGO IN ('10013', '10014', '11934');

-- Associação dos papéis às novas funções que atualmente já tenham a função de download de arquivos de integração
INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) SELECT PAP_CODIGO, '481' FROM tb_papel_funcao WHERE FUN_CODIGO = '36';
INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) SELECT PAP_CODIGO, '482' FROM tb_papel_funcao WHERE FUN_CODIGO = '36';
 
-- Concede as novas permissões para usuários CSE que tenham a permissão de download de arquivos de integração
INSERT INTO tb_funcao_perfil_cse (CSE_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT CSE_CODIGO, USU_CODIGO, '481' FROM tb_funcao_perfil_cse WHERE FUN_CODIGO = '36';
INSERT INTO tb_funcao_perfil_cse (CSE_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT CSE_CODIGO, USU_CODIGO, '482' FROM tb_funcao_perfil_cse WHERE FUN_CODIGO = '36';

-- Concede as novas permissões para usuários ORG que tenham a permissão de download de arquivos de integração
INSERT INTO tb_funcao_perfil_org (ORG_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT ORG_CODIGO, USU_CODIGO, '481' FROM tb_funcao_perfil_org WHERE FUN_CODIGO = '36';
INSERT INTO tb_funcao_perfil_org (ORG_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT ORG_CODIGO, USU_CODIGO, '482' FROM tb_funcao_perfil_org WHERE FUN_CODIGO = '36';

-- Concede as novas permissões para usuários SUP que tenham a permissão de download de arquivos de integração
INSERT INTO tb_funcao_perfil_sup (CSE_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT CSE_CODIGO, USU_CODIGO, '481' FROM tb_funcao_perfil_sup WHERE FUN_CODIGO = '36';
INSERT INTO tb_funcao_perfil_sup (CSE_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT CSE_CODIGO, USU_CODIGO, '482' FROM tb_funcao_perfil_sup WHERE FUN_CODIGO = '36';

-- Concede as novas permissões para perfis de CSE/ORG/SUP que tenham a permissão de download de arquivos de integração
INSERT INTO tb_funcao_perfil (PER_CODIGO, FUN_CODIGO) SELECT PER_CODIGO, '481' FROM tb_funcao_perfil WHERE FUN_CODIGO = '36' AND PER_CODIGO IN (SELECT PER_CODIGO FROM tb_perfil WHERE PAP_CODIGO IN ('1', '3', '7'));
INSERT INTO tb_funcao_perfil (PER_CODIGO, FUN_CODIGO) SELECT PER_CODIGO, '482' FROM tb_funcao_perfil WHERE FUN_CODIGO = '36' AND PER_CODIGO IN (SELECT PER_CODIGO FROM tb_perfil WHERE PAP_CODIGO IN ('1', '3', '7'));
