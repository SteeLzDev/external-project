/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     09/05/2022 09:27:40                          */
/*==============================================================*/


/*==============================================================*/
/* Index: ACO_AUDITADO_IDX                                      */
/*==============================================================*/
create index ACO_AUDITADO_IDX on tb_auditoria_cor
(
   ACO_AUDITADO
);

/*==============================================================*/
/* Index: ACS_AUDITADO_IDX                                      */
/*==============================================================*/
create index ACS_AUDITADO_IDX on tb_auditoria_csa
(
   ACS_AUDITADO
);

/*==============================================================*/
/* Index: ACE_AUDITADO_IDX                                      */
/*==============================================================*/
create index ACE_AUDITADO_IDX on tb_auditoria_cse
(
   ACE_AUDITADO
);

/*==============================================================*/
/* Index: AOR_AUDITADO_IDX                                      */
/*==============================================================*/
create index AOR_AUDITADO_IDX on tb_auditoria_org
(
   AOR_AUDITADO
);

/*==============================================================*/
/* Index: ASU_AUDITADO_IDX                                      */
/*==============================================================*/
create index ASU_AUDITADO_IDX on tb_auditoria_sup
(
   ASU_AUDITADO
);

/*==============================================================*/
/* Index: TPS_SVC_IDX                                           */
/*==============================================================*/
create index TPS_SVC_IDX on tb_param_svc_consignante
(
   TPS_CODIGO,
   SVC_CODIGO
);

