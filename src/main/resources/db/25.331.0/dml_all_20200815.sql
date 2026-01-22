-- DESENV-14175
REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'edtUsuarioServidor.email', CASE psi_vlr WHEN 'S' THEN 'O' ELSE 'N' END FROM tb_param_sist_consignante WHERE TPC_CODIGO = '493';

REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
SELECT 'edtUsuarioServidor.telefone', CASE psi_vlr WHEN 'S' THEN 'O' ELSE 'N' END FROM tb_param_sist_consignante WHERE TPC_CODIGO = '611';

REPLACE INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR)
VALUES ('edtUsuarioServidor.celular', 'N');
