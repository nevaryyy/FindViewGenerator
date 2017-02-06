package com.nevaryyy.fvg;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by waly6 on 2017/1/12.
 */
public class ViewElement {
    private String className;
    private String id;
    private String classNameFull;
    private String layoutName;
    private String fieldName;

    public ViewElement(String className, String id, String layoutName) {
        this.layoutName = layoutName;

        this.id = id.split("/")[1];

        String[] packages = className.split("\\.");
        if (packages.length > 1) {
            this.classNameFull = className;
            this.className = packages[packages.length - 1];
        }
        else {
            this.classNameFull = null;
            this.className = className;
        }

        this.fieldName = getFieldName();
    }

    public String getFieldName() {
        if (fieldName != null) {
            return fieldName;
        }

        String[] layoutWords = layoutName.split("_");
        String[] idWords = id.split("_");
        int index = -1;

        for (int i = 1; ; i ++) {
            if (i == layoutWords.length) {
                index = i;
                break;
            }

            if (i == idWords.length) {
                break;
            }

            if (!idWords[i].equals(layoutWords[i])) {
                break;
            }
        }

        if (index == -1) {
            return null;
        }

        if (index == idWords.length) {
            return StringUtil.toSmallCamelCase(className);
        }

        String fieldNameWithoutClass = StringUtil.toSmallCamelCase(Arrays.copyOfRange(idWords, index, idWords.length));

        return fieldNameWithoutClass + className;
    }

    public String getClassNameFull() {
        return classNameFull;
    }

    public void setClassNameFull(String classNameFull) {
        this.classNameFull = classNameFull;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ViewElement{" +
                "className='" + className + '\'' +
                ", id='" + id + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
