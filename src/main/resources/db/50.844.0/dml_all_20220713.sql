-- DESENV-18035
UPDATE tb_param_svc_consignante SET pse_vlr=CASE WHEN pse_vlr='N' THEN '0' ELSE '1' END WHERE tps_codigo='312';