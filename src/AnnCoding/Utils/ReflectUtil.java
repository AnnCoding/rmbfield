package AnnCoding.Utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author chenjiena
 * @version 1.0
 * @created 2020/6/20.
 */
public final class ReflectUtil {

    private static final Pattern pattern = Pattern.compile("[0-9]*");

    public ReflectUtil() {
    }


    public static String getSeq(String url) {
        String moneyText = "";
        Pattern p = Pattern.compile("seq = " + "[0-9]*");
        Matcher m = p.matcher(url);
        //如果字符串中存在这种类型的字符串就把这个字符串截取出来
        if (m.find()) {
            moneyText = m.group();
            int indexOf = moneyText.indexOf("seq = ");
            //截取出字符串后面的数字
            moneyText = moneyText.substring(indexOf + 6, moneyText.length());
        }
        return moneyText;
    }

    public static void main(String[] args) {
        String rmb = "seq = 2, title = \"ecifNo\", remark = \"ecif号\"";
        System.out.println(getSeq(rmb));
    }

    public static boolean isNumeric(String str) {
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
