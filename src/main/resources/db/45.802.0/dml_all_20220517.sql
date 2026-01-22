-- DESENV-17988
UPDATE tb_aut_desconto ade 
SET ade.ade_vlr_parcela_folha = NULL
WHERE ade.sad_codigo NOT IN ('3','7','8','9')
AND ade.ade_vlr_parcela_folha IS NOT NULL
AND ade.usu_codigo = '1'
AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca WHERE ade.ade_codigo = oca.ade_codigo AND oca.toc_codigo = '86')
;