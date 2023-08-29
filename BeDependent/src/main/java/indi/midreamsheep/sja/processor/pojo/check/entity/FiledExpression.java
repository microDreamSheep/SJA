package indi.midreamsheep.sja.processor.pojo.check.entity;


import com.sun.tools.javac.tree.JCTree;
import indi.midreamsheep.sja.annotation.pojo.check.Check;
import indi.midreamsheep.sja.annotation.pojo.check.NotCheck;

public class FiledExpression {
    private String fieldCheck;

    public String getFieldCheck() {
        return fieldCheck;
    }

    public void setFieldCheck(JCTree.JCVariableDecl jcVariableDecl){
        //获取jcVariableDecl的注解
        jcVariableDecl.getModifiers().getAnnotations().forEach(jcAnnotation -> {
            //判断是否是NotCheck注解
            if (jcAnnotation.getAnnotationType().toString().equals(NotCheck.class.getName())){
                return;
            }
            //判断是否是Check注解
            if (jcAnnotation.getAnnotationType().toString().equals(Check.class.getName())){
                //获取注解的值
                fieldCheck = jcAnnotation.getArguments().get(0).toString();
            }
        });
        //都不是,则进行默认的校验
        fieldCheck = getDefaultExpressionByTypeStr(jcVariableDecl.getType().type.toString(),jcVariableDecl.getName().toString());
    }

    private String getDefaultExpressionByTypeStr(String type,String varName){
        switch (type){
            case "java.lang.String":
                return varName+"!=null&&!\"\".equals("+varName+")";
            case "int":
            case "long":
            case "double":
            case "float":
            case "short":
            case "byte":
                return varName+"!=0";
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Double":
            case "java.lang.Float":
            case "java.lang.Short":
            case "java.lang.Byte":
                return varName+"!=null&&"+varName+"!=0";
            case "java.lang.Boolean":
            case "boolean":
                return varName;

            default:
                return varName+"!=null";
        }
    }

}
