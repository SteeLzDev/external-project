-- DESENV-13923
-- Novos chaves para configurações de campos na "tb_campo_sistema"
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_identificador', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_nome', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_estabelecimento', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_cnpj', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_responsavel', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_cargo', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_telefone', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_responsavel_2', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_cargo_2', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_telefone_2', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_responsavel_3', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_cargo_3', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_resp_telefone_3', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_logradouro', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_bairro', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_cidade', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_uf', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_cep', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_telefone', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_fax', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_email', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_emailIntegraFolha', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_emailValidaServidor', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_diaRepasse', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_codFolha', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_ip_acessos', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('editarOrgao_ddns_acessos', 'S');

-- Atualiza dois campos existentes hoje no banco que estão sendo usados apenas na página jsp de edição de órgão
UPDATE tb_campo_sistema SET CAS_CHAVE = 'editarOrgao_nro' WHERE CAS_CHAVE = 'edtOrgao_nro';
UPDATE tb_campo_sistema SET CAS_CHAVE = 'editarOrgao_complemento' WHERE CAS_CHAVE = 'edtOrgao_complemento';
