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

import java.util.Iterator;
import java.util.List;

import static AnnCoding.Utils.BuildMessageUtil.buildRmbField;
import static AnnCoding.Utils.BuildMessageUtil.buildRmbMessage;

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
                RmbSeq.this.addRmbSubCommandMessage(psiMethod);
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


    private void addRmbField(PsiClass psiClass) {
        List<PsiField> fields = (new CollectionListModel(psiClass.getFields())).getItems();
        if (fields != null) {
            int count = this.checkSuperFieldCount(psiClass);
            count = Math.max(count, this.getMaxSeqMaxValue(fields)) + 1;
            Iterator var8 = fields.iterator();

            //遍历字段
            while (var8.hasNext()) {
                PsiField field = (PsiField) var8.next();
                if (!this.ignoreField(field)) {
                    field.getModifierList().addAnnotation(buildRmbField(count));
                    ++count;
                }
            }
        }
    }

    private Integer getMaxSeqMaxValue(List<PsiField> list) {
        Integer maxValue = -2147483648;
        Iterator var3 = list.iterator();

        while (true) {
            PsiAnnotationParameterList parameterList;
            do {
                PsiAnnotation annotation;
                do {
                    if (!var3.hasNext()) {
                        return maxValue.equals(-2147483648) ? 0 : maxValue;
                    }

                    PsiField m = (PsiField) var3.next();
                    annotation = this.findAnnotationOnField(m, "cn.webank.weup.rmb.annotation.RmbField");
                } while (annotation == null);

                parameterList = annotation.getParameterList();
            } while (parameterList == null);

            PsiNameValuePair[] var7 = parameterList.getAttributes();
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                PsiNameValuePair e = var7[var9];
                String value = ReflectUtil.getSeq(e.getValue().getText());
                if (e.getValue() != null && StringUtils.isNotBlank(value) && ReflectUtil.isNumeric(value)) {
                    maxValue = Math.max(maxValue, Integer.parseInt(value));
                }
            }
        }
    }



    private Integer checkSuperFieldCount(PsiClass psiClass) {
        Integer maxValue = -2147483648;
        PsiClass[] supers = psiClass.getSupers();
        PsiClass[] supersClone = supers;
        int superLength = supers.length;

        for(int i = 0; i < superLength; ++i) {
            PsiClass su = supersClone[i];
            List<PsiField> list = (new CollectionListModel(psiClass.getFields())).getItems();
            //当前文件的最大
            Integer value = this.getMaxRmbFieldMaxValue(list);
            maxValue = Math.max(maxValue, value);
            //子类文件的最大
            value = this.checkSuperFieldCount(su);
            maxValue = Math.max(maxValue, value);
        }

        return maxValue;
    }

    private boolean ignoreField(PsiField field) {
        return field.getModifierList().hasModifierProperty("final") ||
                field.getModifierList().hasModifierProperty("static") ||
                findAnnotationOnField(field, "cn.webank.weup.rmb.annotation.RmbField") != null;
    }



    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = (PsiFile)e.getData(LangDataKeys.PSI_FILE);
        Editor editor = (Editor)e.getData(PlatformDataKeys.EDITOR);
        if (psiFile != null && editor != null) {
            int offset = editor.getCaretModel().getOffset();
            return psiFile.findElementAt(offset);
        } else {
            e.getPresentation().setEnabled(false);
            return null;
        }
    }

    private Integer getMaxRmbFieldMaxValue(List<PsiField> list) {
        Integer maxValue = -2147483648;
        Iterator fieldIterator = list.iterator();

        while(true) {
            PsiAnnotationParameterList parameterList;
            do {
                PsiAnnotation annotation;
                do {
                    if (!fieldIterator.hasNext()) {
                        return maxValue.equals(-2147483648) ? 0 : maxValue;
                    }

                    PsiField m = (PsiField)fieldIterator.next();
                    annotation = this.findAnnotationOnField(m, "cn.webank.weup.rmb.annotation.RmbField");
                } while(annotation == null);

                parameterList = annotation.getParameterList();
            } while(parameterList == null);

            PsiNameValuePair[] valuePair = parameterList.getAttributes();
            int valuePairLen = valuePair.length;

            for(int i = 0; i < valuePairLen; ++i) {
                PsiNameValuePair e = valuePair[i];
                if (e.getValue() != null && ReflectUtil.isNumeric(e.getValue().getText())) {
                    maxValue = Math.max(maxValue, Integer.parseInt(e.getValue().getText()));
                }
            }
        }
    }

    private PsiAnnotation findAnnotationOnField(PsiField psiField, String annotationName) {
        PsiModifierList modifierList = psiField.getModifierList();
        PsiAnnotation[] var4 = modifierList.getAnnotations();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            PsiAnnotation psiAnnotation = var4[var6];
            if (annotationName.equals(psiAnnotation.getQualifiedName())) {
                return psiAnnotation;
            }
        }

        return null;
    }
}
