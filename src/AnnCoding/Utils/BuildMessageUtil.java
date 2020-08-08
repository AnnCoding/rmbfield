package AnnCoding.Utils;

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
