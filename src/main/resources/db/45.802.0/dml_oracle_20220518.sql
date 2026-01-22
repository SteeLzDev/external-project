/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     09/05/2022 09:34:38                          */
/*==============================================================*/


/*==============================================================*/
/* Index: aco_auditado_idx                                      */
/*==============================================================*/
create index aco_auditado_idx on tb_auditoria_cor (
   aco_auditado asc
);

/*==============================================================*/
/* Index: acs_auditado_idx                                      */
/*==============================================================*/
create index acs_auditado_idx on tb_auditoria_csa (
   acs_auditado asc
);

/*==============================================================*/
/* Index: ace_auditado_idx                                      */
/*==============================================================*/
create index ace_auditado_idx on tb_auditoria_cse (
   ace_auditado asc
);

/*==============================================================*/
/* Index: aor_auditado_idx                                      */
/*==============================================================*/
create index aor_auditado_idx on tb_auditoria_org (
   aor_auditado asc
);

/*==============================================================*/
/* Index: asu_auditado_idx                                      */
/*==============================================================*/
create index asu_auditado_idx on tb_auditoria_sup (
   asu_auditado asc
);

/*==============================================================*/
/* Index: tps_svc_idx                                           */
/*==============================================================*/
create index tps_svc_idx on tb_param_svc_consignante (
   tps_codigo asc,
   svc_codigo asc
);
