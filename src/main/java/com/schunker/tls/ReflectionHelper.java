package com.schunker.tls;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionHelper {

    public static List<Field> getDeclaredFields(Class clazz) {
        System.out.println("getDeclaredFields");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(clazz.getName() + " field: " + field.toString());
        }

        List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));

        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            //Field[] superClassFields = getDeclaredFields(superClass);
            //fieldList.addAll(Arrays.asList(superClassFields));
            List<Field> superClassFieldList = getDeclaredFields(superClass);
            fieldList.addAll(superClassFieldList);
        }

        return fieldList;
    }

    public static Field getDeclaredField(Class clazz, String fieldName) throws NoSuchFieldException {
        System.out.println(clazz.getName() + " field: " + fieldName);
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return getDeclaredField(superClass, fieldName);
        }
    }
}
