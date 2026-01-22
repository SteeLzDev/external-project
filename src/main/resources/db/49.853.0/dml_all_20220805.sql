-- DESENV-18404
UPDATE tb_param_consignataria 
SET pcs_vlr = CASE WHEN pcs_vlr = 'S' THEN 'N' ELSE 'S' END 
WHERE tpa_codigo = '75';

UPDATE tb_tipo_param_consignataria 
SET tpa_descricao = 'Permite desbloquear por CSE quando houver sido bloqueada manualmente pelo suporte' 
WHERE tpa_codigo = '75';
