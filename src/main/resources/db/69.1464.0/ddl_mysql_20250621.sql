/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/06/2025 11:00:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_modelo_termo_aditivo                               */
/*==============================================================*/

CREATE TABLE tb_modelo_termo_aditivo ( 
   MTA_CODIGO varchar(32) not null,  
   MTA_DESCRICAO varchar(100) not null,  
   MTA_TEXTO text not null,  
   PRIMARY KEY (MTA_CODIGO)
);

/*==============================================================*/
/* Table: tb_modelo_termo_tag                                   */
/*==============================================================*/

CREATE TABLE tb_modelo_termo_tag (  
   MTT_CODIGO varchar(32) not null,  
   MTA_CODIGO varchar(32) not null,  
   MTT_TAG varchar(100) not null,  
   MTT_VALOR varchar(255) not null,  
   PRIMARY KEY (MTT_CODIGO), 
   KEY IDX_MTA_MTT_1 (MTA_CODIGO),  
   CONSTRAINT FK_MTA_MTT_1 FOREIGN KEY (MTA_CODIGO) REFERENCES tb_modelo_termo_aditivo (MTA_CODIGO) ON DELETE RESTRICT ON UPDATE RESTRICT
);

