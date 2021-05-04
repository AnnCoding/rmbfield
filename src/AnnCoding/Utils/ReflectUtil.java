package AnnCoding.Utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static AnnCoding.Utils.BuildMessageUtil.changeSnack;

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
        String url = "622262081000939好吧4608";
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(url);
        System.out.println(m);

        String m1 = "62226 081 30009394608";
        String m2 = "29993939ab洁娜jjdin";
        String m3 = "90909090909090";

        //匹配银行的卡号19位数字的正则表达式
        System.out.println(m1.matches("^\\d{19}$"));
        System.out.println(m2.matches("^\\d{19}$"));
        System.out.println(m3.matches("^\\d{19}$"));

        String str = "jienaChenAnnUooK";
        String ss = str;
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            ss = changeSnack(ss);
            charArray = ss.toCharArray();
        }

        System.out.println("抽取大写字母："+ ss);
    }


    public static boolean isNumeric(String str) {
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
