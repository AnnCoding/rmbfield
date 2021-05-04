package AnnCoding.Utils;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Strings;

/**
 * @author chenjiena
 * @version 1.0
 * @created 2020/8/8.
 */
public class BuildMessageUtil {

    /**
     * @return
     * @RmbField(seq = , title = "", remark = "")
     */
    public static String buildRmbField(int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("RmbField(seq = " + seq + ", title = \"\", remark = \"\")");
        return sb.toString();
    }

    /**
     * @return
     * @JsonProperty("")
     */
    public static String buildJsonProperty(String field) {
        if (StringUtils.isBlank(field)){
            return "";
        }
        String snackSS = field;
        char[] charArray = field.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            snackSS = changeSnack(snackSS);
            charArray = snackSS.toCharArray();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("JsonProperty(\""+ snackSS +"\")");
        return sb.toString();
    }

    public static String changeSnack(String snackSS){
        if (StringUtils.isBlank(snackSS)){
            return "";
        }

        String result = snackSS;
        char[] charArray = snackSS.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if(charArray[i] >= 'A' && charArray[i] <= 'Z'){
                result = StringUtils.substring(snackSS,0,i) + '_' + String.valueOf(charArray[i]).toLowerCase() +
                        StringUtils.substring(snackSS,i+1,charArray.length);
                break;
            }
        }

        return result;

    }

    /**
     * @return
     * @RmbSubCommandMessage( subCommand = "",
     * name = "",
     * useDesc = "",
     * threadPoolName = "TP_Main"
     * )
     */

    public static String buildRmbMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("RmbSubCommandMessage(" + "\n");
        sb.append("    subCommand = \" \"," + "\n");
        sb.append("     name = \" \"," + "\n");
        sb.append("     useDesc = \" \"," + "\n");
        sb.append("     threadPoolName = \"TP_Main\"" + "\n");
        sb.append(")");
        return sb.toString();
    }
}
