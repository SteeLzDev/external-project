/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     18/06/2025 11:00:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_modelo_termo_aditivo                               */
/*==============================================================*/
create table tb_modelo_termo_aditivo ( 
   mta_codigo varchar2(32) not null,  
   mta_descricao varchar2(100) not null,  
   mta_texto clob not null,  
   constraint pk_modelo_termo_aditivo primary key (mta_codigo)
);

/*==============================================================*/
/* Table: tb_modelo_termo_tag                                   */
/*==============================================================*/
create table tb_modelo_termo_tag (  
   mtt_codigo varchar2(32) not null,  
   mta_codigo varchar2(32) not null,  
   mtt_tag varchar2(100) not null,  
   mtt_valor varchar2(255) not null,  
   constraint pk_modelo_termo_tag primary key (mtt_codigo) 
);

/*==============================================================*/
/* Index: idx_mta_mtt_1                                              */
/*==============================================================*/
create index idx_mta_mtt_1 on tb_modelo_termo_tag (
   mta_codigo asc
);

alter table tb_modelo_termo_tag
   add constraint fk_mta_mtt_1 foreign key (mta_codigo)
      references tb_modelo_termo_aditivo (mta_codigo);

