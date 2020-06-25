package AnnCoding;

import AnnCoding.Utils.ReflectUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.ui.CollectionListModel;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author chenjiena
 * @version 1.0
 * @created 2020/6/20.
 */
public class RmbSeq extends AnAction {

    public RmbSeq() {
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement curElement = this.getPsiElement(e);
        if (curElement != null) {
            this.generateGSMethod(PsiTreeUtil.getParentOfType(curElement, PsiClass.class));
        }

    }

    private void generateGSMethod(final PsiClass psiMethod) {

        WriteCommandAction.runWriteCommandAction(psiMethod.getProject(), new Runnable() {
            @Override
            public void run() {
                //                RmbSeq.this.addRmbSubCommandMessage(psiMethod);
                Map<Integer, Character> markMap = new HashMap<>();
                RmbSeq.this.addRmbField(psiMethod);
            }
        });

    }

    private void addRmbSubCommandMessage(PsiClass psiClass) {
        String struct = "cn.webank.weup.rmb.annotation.RmbSubCommandMessage";
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
        if (annotations != null && annotations.length > 0) {
            PsiAnnotation[] var4 = annotations;
            int var5 = annotations.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                PsiAnnotation annotation = var4[var6];
                if (struct.equals(annotation.getQualifiedName())) {
                    return;
                }
            }
        }

        psiClass.getModifierList().addAnnotation(buildRmbMessage());
    }

    /**
     * @return
     * @RmbSubCommandMessage( subCommand = "",
     * name = "",
     * useDesc = "",
     * threadPoolName = "TP_Main"
     * )
     */

    private String buildRmbMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("RmbSubCommandMessage(" + "\n");
        sb.append("    subCommand = \" \"," + "\n");
        sb.append("     name = \" \"," + "\n");
        sb.append("     useDesc = \" \"," + "\n");
        sb.append("     threadPoolName = \"TP_Main\"" + "\n");
        sb.append(")");
        return sb.toString();
    }

    /**
     * 1. 用map 将父类和子类所有的序号都存起来
     * 2. 取到RMBField 的内容之后先做去空格和去回车保证取到顺序
     *
     * @param psiClass
     */

    private void addRmbField(PsiClass psiClass) {
        Map<Integer, Character> markMap = new HashMap<>();
        List<PsiField> fields = (new CollectionListModel(psiClass.getFields())).getItems();
        if (fields != null) {
            checkSuperFieldCount(psiClass, markMap);//子类最大
            setMarkMapValue(fields, markMap);
            Iterator var8 = fields.iterator();

            //遍历字段
            Integer i = 1;
            while (var8.hasNext()) {
                PsiField field = (PsiField) var8.next();
                if (!this.ignoreField(field)) {
                    while (markMap.get(i) != null && ReflectUtil.MARK.equals(markMap.get(i))) {
                        ++i;
                    }
                    field.getModifierList().addAnnotation(buildRmbField(i));
                    markMap.put(i, ReflectUtil.MARK);
                }
            }
        }
    }

    private void setMarkMapValue(List<PsiField> list, Map<Integer, Character> markMap) {
        if (list.isEmpty()) {
            return;
        }
        PsiAnnotationParameterList parameterList = null;
        list.forEach(psiField -> {
            PsiAnnotation annotation = this.findAnnotationOnField(psiField, "cn.webank.weup.rmb.annotation.RmbField");
            if (annotation != null) {
                parameterList.add(annotation.getParameterList());
            }

        });

//        Iterator fieldIterator = list.iterator();
//        while (true) {
//            PsiAnnotationParameterList parameterList;
//            do {
//                PsiAnnotation annotation;
//                do {
//                    if (!fieldIterator.hasNext()) {
//                        return;
//                    }
//
//                    PsiField m = (PsiField) fieldIterator.next();
//                    annotation = this.findAnnotationOnField(m, "cn.webank.weup.rmb.annotation.RmbField");
//                } while (annotation == null);
//
//                parameterList = annotation.getParameterList();
//            } while (parameterList == null);

            PsiNameValuePair[] var7 = parameterList.getAttributes();
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                PsiNameValuePair e = var7[var9];
                String value = ReflectUtil.getSeq(e.getValue().getText());
                if (StringUtils.isNotBlank(value) && ReflectUtil.isNumeric(value)) {
                    markMap.put(Integer.valueOf(value), ReflectUtil.MARK);
                }
            }
    }

    /**
     * @return
     * @RmbField(seq = , title = "", remark = "")
     */
    private String buildRmbField(int seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("RmbField(seq = " + seq + ", title = \"\", remark = \"\")");
        return sb.toString();
    }

    private Integer checkSuperFieldCount(PsiClass psiClass, Map<Integer, Character> markMap) {
        Integer maxValue = -2147483648;
        PsiClass[] supers = psiClass.getSupers();
        PsiClass[] var4 = supers;
        int superLength = supers.length;

        for (int i = 0; i < superLength; ++i) {
            PsiClass su = var4[i];
            List<PsiField> list = (new CollectionListModel(psiClass.getFields())).getItems();
            setMarkMapValue(list, markMap);
            checkSuperFieldCount(su, markMap);
        }

        return maxValue;
    }

    private boolean ignoreField(PsiField field) {
        return field.getModifierList().hasModifierProperty("final") || field.getModifierList()
                .hasModifierProperty("static")
                || findAnnotationOnField(field, "cn.webank.weup.rmb.annotation.RmbField") != null;
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = (PsiFile) e.getData(LangDataKeys.PSI_FILE);
        Editor editor = (Editor) e.getData(PlatformDataKeys.EDITOR);
        if (psiFile != null && editor != null) {
            int offset = editor.getCaretModel().getOffset();
            return psiFile.findElementAt(offset);
        } else {
            e.getPresentation().setEnabled(false);
            return null;
        }
    }

    private PsiAnnotation findAnnotationOnField(PsiField psiField, String annotationName) {
        PsiModifierList modifierList = psiField.getModifierList();
        PsiAnnotation[] var4 = modifierList.getAnnotations();
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            PsiAnnotation psiAnnotation = var4[var6];
            if (annotationName.equals(psiAnnotation.getQualifiedName())) {
                return psiAnnotation;
            }
        }

        return null;
    }
}
