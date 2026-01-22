package com.zetra.econsig.helper.texto;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: LocaleHelper</p>
 * <p>Description: Classe auxiliar para localização e internacionalização do eConsig</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LocaleHelper {

    public static final String BRASIL     = "pt-BR";
    public static final String MEXICO     = "es-MX";
    public static final String EUA        = "en-US";
    public static final String INDIA      = "en-IN";
    public static final String INGLATERRA = "en-GB";
    public static final String NIGERIA    = "en-NG";
    public static final String PORTUGAL   = "pt-PT";
    public static final String ITALIA     = "it-IT";

    public static final String FORMATO_DATA_INGLES = "yyyy-MM-dd";

    private static final String DEFAULT_DATE_JAVASCRIPT_PATTERN = "DD/DD/DDDD";
    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
    private static final String DEFAULT_DDD_MASK = "DD";
    private static final String DEFAULT_DDD_CELULAR_MASK = "DD";
    private static final String DEFAULT_MULTIPLOS_TELEFONES_MASK = "#T100";
    private static final String DEFAULT_TELEFONE_MASK = "#T100";
    private static final String DEFAULT_TIME_PLACE_HOLDER = "hh:mm:ss";

    // Conforme sugestão do Sonar - regra java:S1118
    private LocaleHelper() {
    }

    /**
     * Todas as implementações de localização são dependentes do resultado desta função
     * @return
     */
    public static String getLocale() {
        return Locale.getDefault().toLanguageTag();
    }

    /**
     * As implementações de formatação de valores númericos são, em sua maioria, dependentes do resultado desta função
     * @return
     */
    public static String getLanguage() {
        // O padrão do méxico é diferente do padrão para a língua "es"
        if (getLocale().equals(MEXICO)) {
            return getLocale();
        }
        if (getLocale().equals(INDIA)) {
            return getLocale();
        }
        return Locale.getDefault().getLanguage();
    }

    public static Locale getLocaleObject() {
        return Locale.getDefault();
    }

    public static NumberFormat getCurrencyFormat() {
        return NumberFormat.getCurrencyInstance();
    }

    public static NumberFormat getPercentFormat() {
        return NumberFormat.getPercentInstance();
    }

    @SuppressWarnings("java:S3252")
    public static DecimalFormat getDecimalFormat() {
        return (DecimalFormat) DecimalFormat.getInstance();
    }

    public static DateFormat getDateFormat() {
        return DateFormat.getDateInstance();
    }

    public static DateFormat getDateTimeFormat() {
        return DateFormat.getDateTimeInstance();
    }

    public static String getDatePlaceHolder() {
        String locale = getLocale();

        if (locale.equals(BRASIL) || locale.equals(ITALIA)) {
            return "dd/mm/aaaa";
        } else if (locale.equals(MEXICO) || locale.equals(INGLATERRA) || locale.equals(NIGERIA) || locale.equals(PORTUGAL) || locale.equals(INDIA)) {
            return "dd/mm/yyyy";
        }

        return ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM)).toPattern();
    }

    public static String getDateTimePlaceHolder() {
        // dd/MM/yyyy HH:mm:ss
        return getDatePlaceHolder() + " hh:mm:ss"; // "hh:mm:ss"
    }

    public static String getTimePlaceHolder() {
        return DEFAULT_TIME_PLACE_HOLDER;
    }

    public static String getPeriodoPlaceHolder() {
        final String periodicidade = PeriodoHelper.getPeriodicidadeFolha(AcessoSistema.getAcessoUsuarioSistema());

        String labelPeriodicidade = "mm";
        switch (periodicidade) {
            case CodedValues.PERIODICIDADE_FOLHA_MENSAL:
                labelPeriodicidade = "mm";
                break;
            case CodedValues.PERIODICIDADE_FOLHA_QUINZENAL:
                labelPeriodicidade = "qq";
                break;
            case CodedValues.PERIODICIDADE_FOLHA_QUATORZENAL:
                labelPeriodicidade = "gg";
                break;
            case CodedValues.PERIODICIDADE_FOLHA_SEMANAL:
                labelPeriodicidade = "ss";
                break;
        }

        String locale = getLocale();

        if (locale.equals(BRASIL) || locale.equals(PORTUGAL) || locale.equals(MEXICO) || locale.equals(ITALIA)) {
            return labelPeriodicidade + "/aaaa";
        } else if (locale.equals(INGLATERRA) || locale.equals(NIGERIA) ||  locale.equals(INDIA)) {
            return labelPeriodicidade + "/yyyy";
        }
        return labelPeriodicidade + "/yyyy";
    }

    public static String getDatePattern() {
        // A partir da versão 11 do java o forma MEDIUM mudou. E por convensão vamos deixar o formato fixo.
        return DEFAULT_DATE_PATTERN; // "dd/MM/yyyyy"
    }

    public static String getMediumDatePattern() {
        return ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM)).toPattern();
    }

    public static String getDateTimePattern() {
        // dd/MM/yyyy HH:mm:ss
        return getDatePattern() + " HH:mm:ss";
    }

    public static String getDateJavascriptPattern() {
        return DEFAULT_DATE_JAVASCRIPT_PATTERN; // "DD/DD/DDDD"
    }

    public static String getDateDialectPattern() {
        String locale = getLocale();

        if (locale.equals(BRASIL) || locale.equals(MEXICO) || locale.equals(INGLATERRA) || locale.equals(NIGERIA) || locale.equals(PORTUGAL) || locale.equals(INDIA) || locale.equals(ITALIA)) {
            if (DAOFactory.isMysql()) {
                return "date_format(?1, '%d/%m/%Y')";
            } else if (DAOFactory.isOracle()) {
                return "TO_CHAR(?1, 'DD/MM/YYYY')";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getDateTimeDialectPattern() {
        String locale = getLocale();

        if (locale.equals(BRASIL) || locale.equals(MEXICO) || locale.equals(INGLATERRA) || locale.equals(NIGERIA) || locale.equals(PORTUGAL) || locale.equals(INDIA) || locale.equals(ITALIA)) {
            if (DAOFactory.isMysql()) {
                return "date_format(?1, '%d/%m/%Y %H:%i:%s')";
            } else if (DAOFactory.isOracle()) {
                return "TO_CHAR(?1, 'DD/MM/YYYY HH24:MI:SS')";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String formatarCpf(String cpf) {
        if (!TextHelper.isNull(cpf)) {
            String locale = getLocale();
            if (locale.equals(BRASIL)) {
                cpf = TextHelper.format(cpf.trim(), "###.###.###-##");
            }
        }
        return cpf;
    }

    public static String getCpfMask() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "DDD.DDD.DDD-DD";
        } else if (locale.equals(MEXICO)) {
            return "CCCCDDDDDDAAA";
        } else if (locale.equals(INDIA)) {
            return "CCCCCDDDDC";
        } else if (locale.equals(INGLATERRA)) {
            return "CCDDDDDDC";
        } else if (locale.equals(PORTUGAL)) {
            return "DDDDDDDDD";
        } else if (locale.equals(ITALIA)) {
            return "CCCCCCDDCDDCDDDC";
        } else {
            return "#L19";
        }
    }

    public static String getCpfSize() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "15";
        } else if (locale.equals(MEXICO)) {
            return "14";
        } else if (locale.equals(INDIA)) {
            return "11";
        } else if (locale.equals(INGLATERRA)) {
            return "10";
        } else if (locale.equals(PORTUGAL)) {
            return "10";
        } else if (locale.equals(ITALIA)) {
            return "16";
        } else {
            return "19";
        }
    }

    public static String getCpfMaxLenght() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "14";
        } else if (locale.equals(MEXICO)) {
            return "13";
        } else if (locale.equals(INDIA)) {
            return "10";
        } else if (locale.equals(INGLATERRA)) {
            return "9";
        } else if (locale.equals(PORTUGAL)) {
            return "9";
        } else if (locale.equals(ITALIA)) {
            return "16";
        } else {
            return "19";
        }
    }

    public static String getCnpjMask() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "AA.AAA.AAA/AAAA-DD";
        } else if (locale.equals(MEXICO)) {
            return "CCCDDDDDDAAA";
        } else if (locale.equals(PORTUGAL)) {
            return "DDDDDDDDD";
        } else if (locale.equals(INDIA)) {
            return "DDDDDDDDDD";
        } else if (locale.equals(ITALIA)){
            return "AAAAAAAAAA";
        } else {
            return "#L19";
        }
    }

    public static String getCnpjSize() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "19";
        } else if (locale.equals(MEXICO)) {
            return "13";
        } else if (locale.equals(PORTUGAL)) {
            return "10";
        } else if (locale.equals(INDIA)) {
            return "10";
        } else if (locale.equals(ITALIA)) {
            return "10";
        } else {
            return "20";
        }
    }

    public static String getCnpjMaxLenght() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "18";
        } else if (locale.equals(MEXICO)) {
            return "12";
        } else if (locale.equals(PORTUGAL)) {
            return "9";
        } else if (locale.equals(INDIA)) {
            return "10";
        } else if (locale.equals(ITALIA)) {
            return "10";
        } else {
            return "19";
        }
    }

    /**
     * Máscara de CEP
     * @return
     */
    public static String getCepMask() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "DD.DDD-DDD";
        } else if (locale.equals(MEXICO)) {
            return "DDDDD";
        } else if (locale.equals(PORTUGAL)) {
            return "DDDD-DDD";
        } else if (locale.equals(INDIA)) {
            return "DDDDDD";
        } else if (locale.equals(ITALIA)) {
            return "AAAAAAAAAAA";
        } else {
            return "#L10";
        }
    }

    /**
     * Tamanho do campo de CEP
     * @return
     */
    public static String getCepSize() {
        String locale = getLocale();
        if (locale.equals(BRASIL)) {
            return "9";
        } else if (locale.equals(MEXICO)) {
            return "5";
        } else if (locale.equals(PORTUGAL)) {
            return "8";
        } else if (locale.equals(INDIA)) {
            return "6";
        } else if (locale.equals(ITALIA)) {
            return "11";
        } else {
            return "10";
        }
    }

    /**
     * Máscara de celular
     * @return
     */
    @SuppressWarnings({"java:S3776", "java:S1192"})
    public static String formataCelular(String celular) {
        String locale = getLocale();
        String number = celular.replaceAll("\\D+", "");

        // celulares com ou sem ddd, começando com 9 ou não, sempre com o digito que segue o 9 (caso exista) sendo 6-9
        if (locale.equals(BRASIL)) {
            Pattern r = Pattern.compile("^(\\d{2}9[6-9]\\d{7})$");
            Pattern r2 = Pattern.compile("^(\\d{2}[6-9]\\d{7})$");
            Pattern r3 = Pattern.compile("^(9[6-9]\\d{7})$");
            Pattern r4 = Pattern.compile("^([6-9]\\d{7})$");

            if (r.matcher(number).matches() || r2.matcher(number).matches() || r3.matcher(number).matches() || r4.matcher(number).matches()) {
                return "+55"+number;
            } else {
                return "";
             }
        } else if (locale.equals(MEXICO)) {
            Pattern r = Pattern.compile("^\\d{10}$");
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+52"+number;
            } else {
                return "";
            }

        } else if (locale.equals(INDIA)) {
            Pattern r = Pattern.compile("^[789]\\d{9}$");
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+91"+number;
            } else {
                return "";
            }

        } else if (locale.equals(PORTUGAL)) {
            Pattern r = Pattern.compile("^\\d{9}$");
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+351"+number;
            } else {
                return "";
            }

        } else if (locale.equals(INGLATERRA)) {
            Pattern r = Pattern.compile("^(0){0,1}7\\d{9}$"); //+44 (0)7DDD DDD DDD
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+44"+number;
            } else {
                return "";
            }

        } else if (locale.equals(NIGERIA)) {
            Pattern r = Pattern.compile("^\\d{10}$");
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+234"+number;
            } else {
                r = Pattern.compile("^\\d{13}$");
                m = r.matcher(number);

                if (m.matches()) {
                    return "+"+number;
                } else {
                    return "";
                }
            }

        } else if (locale.equals(ITALIA)) {
            Pattern r = Pattern.compile("^\\d{10}$");//+39 335 7082917
            Matcher m = r.matcher(number);

            if (m.matches()) {
                return "+39"+number;
            } else {
                return "";
            }
        }else {
            return celular;
        }
    }

	public static String getTelefoneMask() {
		String locale = getLocale();
		if (locale.equals(BRASIL)) {
			return "#T8";
		} else if (locale.equals(MEXICO)) {
			return "#T8";
		} else if (locale.equals(INDIA)) {
			return "#T8";
		} else if (locale.equals(PORTUGAL)) {
            return "#T9";
        } else if (locale.equals(INGLATERRA)) {
			return "#T11";
        } else if (locale.equals(NIGERIA)) {
            return "#T10";
		} else if (locale.equals(ITALIA)) {
		    return "#T8";
		}
		return DEFAULT_TELEFONE_MASK; // "#T100"
	}

	public static String getTelefoneSize() {
	        String locale = getLocale();
	        if (locale.equals(BRASIL)) {
	            return "8";
	        } else if (locale.equals(MEXICO)) {
	            return "8";
	        } else if (locale.equals(INDIA)) {
	            return "8";
	        } else if (locale.equals(PORTUGAL)) {
	            return "9";
	        } else if (locale.equals(INGLATERRA)) {
	            return "11";
	        } else if (locale.equals(NIGERIA)) {
	            return "10";
	        } else if (locale.equals(ITALIA)) {
	            return "8";
	        }
	        return "100";
	}

    public static String getDDDMask() {
    	return DEFAULT_DDD_MASK; // "DD"
    }

    public static String getDDDCelularMask() {
        String locale = getLocale();

        if (locale.equals(ITALIA)) {
            return "DDD";
        }
        return DEFAULT_DDD_CELULAR_MASK; // "DD"
    }

    public static String getCelularMask() {
    	String locale = getLocale();

    	if (locale.equals(BRASIL)) {
    		return "#T9";
    	} else if (locale.equals(MEXICO)) {
    		return "#T9";
    	} else if (locale.equals(INDIA)) {
    		return "#T10";
    	} else if (locale.equals(PORTUGAL)) {
            return "#T9";
        } else if (locale.equals(INGLATERRA)) {
    		return "#T11";
        } else if (locale.equals(NIGERIA)) {
            return "#T13";
    	} else if (locale.equals(ITALIA)) {
    	    return "#T7";
    	}
    	return DEFAULT_TELEFONE_MASK; // "#T100"
    }

    public static String getCelularSize() {
        String locale = getLocale();

        if (locale.equals(BRASIL)) {
            return "9";
        } else if (locale.equals(MEXICO)) {
            return "9";
        } else if (locale.equals(INDIA)) {
            return "10";
        } else if (locale.equals(PORTUGAL)) {
            return "9";
        } else if (locale.equals(INGLATERRA)) {
            return "11";
        } else if (locale.equals(NIGERIA)) {
            return "13";
        } else if (locale.equals(ITALIA)) {
            return "7";
        }
        return "100";
    }
    
    public static String getWhatsappMask() {
        //TODO: adicionar mais validações de formatos de whatsapp aqui, caso necessario
        return "#T11";
    }
    
    public static Boolean isWhatsapp0800(String number) {
        Pattern r = Pattern.compile("^(0[0-9]00\\d{7})$");
        
        if(!TextHelper.isNull(number)) {
            return r.matcher(number).matches();
        }
        
        return false;
    }

    public static String getMultiplosTelefonesMask() {
    	return DEFAULT_MULTIPLOS_TELEFONES_MASK; // "#T100"
    }
}
