-- DESENV-22376
-- ALTERAR NO APPLICATION RESOURCES
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_contrato_beneficio.scb_codigo' WHERE TEX_CHAVE = 'tb_contrato_beneficio.sbc_codigo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_modelo_email.mem_codigo' WHERE TEX_CHAVE = 'tb_modelo_email.nem_codigo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_modelo_email.mem_texto' WHERE TEX_CHAVE = 'tb_modelo_email.nem_texto';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_modelo_email.mem_titulo' WHERE TEX_CHAVE = 'tb_modelo_email.nem_titulo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_servidor.nes_codigo' WHERE TEX_CHAVE = 'tb_servidor.ser_nes_codigo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_consignante.bco_codigo' WHERE TEX_CHAVE = 'tb_consignante.cse_bco_codigo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_consignataria.csa_nro_processo' WHERE TEX_CHAVE = 'tb_consignataria.csa_num_processo';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_notificacao_dispositivo.ndi_data_envio' WHERE TEX_CHAVE = 'tb_notificacao_dispositivondi_data_envio';
UPDATE tb_texto_sistema SET TEX_CHAVE = 'tb_notificacao_dispositivo.usu_codigo_destinatario' WHERE TEX_CHAVE = 'tb_notificacao_dispositivousu_codigo_destinatario';

-- REMOVER NO APPLICATION RESOURCES
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_cse.cfc_divisao';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_est.cfe_divisao';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_org.cfo_divisao';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_cse.cfc_num_semanas';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_est.cfe_num_semanas';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_calendario_folha_org.cfo_num_semanas';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_periodo_exportacao.pex_num_semanas';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_convenio_vinculo_registro.cnv_codigo';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_convenio_vinculo_registro.cvr_ativo';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_hist_integracao_beneficio.hib_periodo';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_ocorrencia_perfil.opr_usu_codigo';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'tb_relacionamento_autorizacao.rad_data_ref';